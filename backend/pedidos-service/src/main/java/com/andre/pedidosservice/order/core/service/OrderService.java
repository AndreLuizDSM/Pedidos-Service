package com.andre.pedidosservice.order.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.gateways.in.OrderGatewayService;
import com.andre.pedidosservice.order.gateways.out.OrderNotificationGateway;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService implements OrderGatewayService {

    private final UserRepositoryGateway userRepository;
    private final OrderRepositoryGateway repository;
    private final ProductRepositoryGateway productRepository;

    // Porta de saída para notificação assíncrona (RabbitMQ na implementação atual).
    // O OrderService depende só da interface, então não sabe nem precisa saber que existe um broker por trás.
    private final OrderNotificationGateway notificationGateway;

    public OrderService(OrderRepositoryGateway repository, ProductRepositoryGateway productRepository,
                         UserRepositoryGateway userRepository, OrderNotificationGateway notificationGateway) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.notificationGateway = notificationGateway;
    }
    @Override
    public OrderDomain createOrder(String userId) {
        UserDomain user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Usuario nao encontrado " + userId));

        OrderDomain domain = OrderDomain.newOrder();
        domain.setUserId(userId);
        OrderDomain savedOrder = repository.save(domain, user);

        // Grava o evento de criação na mesma transação do pedido (ver comentário acima)
        notificationGateway.notifyOrderCreated(buildEvent(savedOrder));

        return savedOrder;
    }

    @Override
    public OrderDomain addOrderItem(String orderId, List<OrderItemDomain> items) {
        OrderDomain order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDENTE) {
            throw new InvalidRequestException("Apenas pedidos pendentes podem receber itens");
        }

        List<OrderItemDomain> preparedItems = new ArrayList<>();
        double additionalTotal = 0;

        for (OrderItemDomain item : items) {
            ProductDomain product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new InvalidRequestException(
                        "Estoque insuficiente para o produto: " + product.getName()
                        + " (disponível: " + product.getStock() + ")");
            }

            OrderItemDomain prepared = new OrderItemDomain();
            prepared.setProductId(item.getProductId());
            prepared.setProductName(product.getName());
            prepared.setProductPrice(product.getPrice());
            prepared.setQuantity(item.getQuantity());
            preparedItems.add(prepared);

            additionalTotal += product.getPrice() * item.getQuantity();
        }
        order.setExpiresAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(6));
        double newTotal = order.getTotalAmount() + additionalTotal;

        return repository.saveItems(order, preparedItems, newTotal);
    }

    @Override
    public OrderDomain updateOrderStatus(String id, OrderStatus status) {
        getOrderById(id);
        OrderDomain updatedOrder = repository.updateStatus(id, status);

        // Só avisamos o resto do sistema quando o pedido realmente chega ao status final.
        // Mudanças intermediárias (ex: PENDENTE -> CONFIRMADO) não disparam o evento de "finished"
        if (updatedOrder.getStatus() == OrderStatus.ENTREGUE) {
            notificationGateway.notifyOrderFinished(buildEvent(updatedOrder));
        }

        return updatedOrder;
    }

    @Override
    public OrderDomain getOrderById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    @Override
    public void deleteOrder(String id) {
        getOrderById(id);
        repository.deleteById(id);
    }

    @Override
    public OrderDomain deleteOrderItem(String orderId, String orderItemId) {
        OrderDomain order = getOrderById(orderId);

        OrderItemDomain item = repository.findItemById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de pedido não encontrado"));

        boolean pertenceAoPedido = order.getOrderItems()
                .stream()
                .anyMatch(i -> i.getId().equals(item.getId()));

        if (!pertenceAoPedido) {
            throw new ResourceNotFoundException("Item não pertence ao pedido informado");
        }

        double newTotal = order.getTotalAmount() - (item.getProductPrice() * item.getQuantity());
        repository.deleteItemById(orderId, orderItemId, newTotal);

        order.setExpiresAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(6));
        order.setTotalAmount(newTotal);
        order.getOrderItems().removeIf(i -> i.getId().equals(item.getId()));
        return order;
    }

    @Override
    public List<OrderDomain> getOrdersByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    // Centraliza a montagem do evento de notificação a partir do domínio,
    // evitando duplicar esse mapeamento em cada ponto que precisa notificar
    private OrderNotificationEvent buildEvent(OrderDomain order) {
        return OrderNotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .occurredAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }
}
