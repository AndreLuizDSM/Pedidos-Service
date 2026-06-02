package com.andre.pedidosservice.orders.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.gateways.in.IOrderGatewayService;
import com.andre.pedidosservice.orders.gateways.out.IOrderRepositoryGateway;
import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.gateways.out.IProductRepositoryGateway;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.out.IUserRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderService implements IOrderGatewayService {

    private final IUserRepository userRepository;
    private final IOrderRepositoryGateway repository;
    private final IProductRepositoryGateway productRepository;

    public OrderService(IOrderRepositoryGateway repository, IProductRepositoryGateway productRepository, IUserRepository userRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDomain createOrder(String userId) {
        // Verificar se usuário existe
        UserDomain user = userRepository.findUserById(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Usuario nao encontrado " + userId));

        // Cria Order seguindo as regras de negócio
        OrderDomain domain = OrderDomain.newOrder();
        domain.setUserId(userId);
        return repository.createOrder(domain, user);
    }

    @Override
    public OrderDomain addOrderItem(String orderId, List<OrderItemDomain> items) {
        OrderDomain order = getOrderByID(orderId);

        if (order.getStatus() != OrderStatus.PENDENTE) {
            throw new InvalidRequestException("Apenas pedidos pendentes podem receber itens");
        }

        List<OrderItemDomain> preparedItems = new ArrayList<>();
        double additionalTotal = 0;

        for (OrderItemDomain item : items) {
            ProductDomain product = productRepository.findProductById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new InvalidRequestException(
                        "Estoque insuficiente para o produto: " + product.getName()
                        + " (disponível: " + product.getStock() + ")");
            }

            OrderItemDomain prepared = new OrderItemDomain();
            prepared.setProductId(item.getProductId());
            prepared.setProductName(product.getName());
            prepared.setPrice(product.getPrice());
            prepared.setQuantity(item.getQuantity());
            preparedItems.add(prepared);

            additionalTotal += product.getPrice() * item.getQuantity();
        }

        double newTotal = order.getTotalAmount() + additionalTotal;
        return repository.saveOrderItems(orderId, preparedItems, newTotal);
    }

    @Override
    public OrderDomain updateStatusOrder(OrderStatus status, String id) {
        getOrderByID(id);
        return repository.updateOrderStatus(id, status);
    }

    @Override
    public OrderDomain getOrderByID(String id) {
        return repository.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    @Override
    public void deleteOrder(String id) {
        getOrderByID(id);
        repository.removeOrderById(id);
    }

    @Override
    public OrderDomain deleteOrderItemByID(String orderID, String orderItemID) {
        OrderDomain order = getOrderByID(orderID);

        OrderItemDomain item = repository.getOrderItemById(orderItemID)
                .orElseThrow(() -> new ResourceNotFoundException("Item de pedido não encontrado"));

        boolean pertenceAoPedido = order.getOrderItems()
                .stream()
                .anyMatch(i -> i.getId().equals(item.getId()));

        if (!pertenceAoPedido) {
            throw new ResourceNotFoundException("Item não pertence ao pedido informado");
        }

        double newTotal = order.getTotalAmount() - (item.getPrice() * item.getQuantity());

        repository.removeOrderItemById(orderID, orderItemID, newTotal);

        order.setTotalAmount(newTotal);
        order.getOrderItems().removeIf(i -> i.getId().equals(item.getId()));
        return order;
    }

    @Override
    public List<OrderDomain> getOrdersByUserId(String userId) {
        return repository.getOrdersByUserId(userId);
    }
}
