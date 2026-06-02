package com.andre.pedidosservice.orders.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.gateways.in.OrderGatewayService;
import com.andre.pedidosservice.orders.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.out.UserRepositoryGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService implements OrderGatewayService {

    private final UserRepositoryGateway userRepository;
    private final OrderRepositoryGateway repository;
    private final ProductRepositoryGateway productRepository;

    public OrderService(OrderRepositoryGateway repository, ProductRepositoryGateway productRepository, UserRepositoryGateway userRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDomain createOrder(String userId) {
        UserDomain user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Usuario nao encontrado " + userId));

        OrderDomain domain = OrderDomain.newOrder();
        domain.setUserId(userId);
        return repository.save(domain, user);
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
        order.setExpiresAt(LocalDateTime.now().plusHours(6));
        double newTotal = order.getTotalAmount() + additionalTotal;

        return repository.saveItems(order, preparedItems, newTotal);
    }

    @Override
    public OrderDomain updateOrderStatus(String id, OrderStatus status) {
        getOrderById(id);
        return repository.updateStatus(id, status);
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

        order.setExpiresAt(LocalDateTime.now().plusHours(6));
        order.setTotalAmount(newTotal);
        order.getOrderItems().removeIf(i -> i.getId().equals(item.getId()));
        return order;
    }

    @Override
    public List<OrderDomain> getOrdersByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}
