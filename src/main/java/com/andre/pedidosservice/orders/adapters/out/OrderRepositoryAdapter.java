package com.andre.pedidosservice.orders.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.dtos.OrderMapper;
import com.andre.pedidosservice.orders.entities.OrderEntity;
import com.andre.pedidosservice.orders.entities.OrderItemEntity;
import com.andre.pedidosservice.orders.gateways.out.IOrderRepositoryGateway;
import com.andre.pedidosservice.orders.ports.out.IOrderItemJpaRepository;
import com.andre.pedidosservice.orders.ports.out.IOrderJpaRepository;
import com.andre.pedidosservice.products.entities.ProductEntity;
import com.andre.pedidosservice.products.ports.out.IProductJpaRepository;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.dtos.UserMapper;
import com.andre.pedidosservice.users.entities.UserEntity;
import com.andre.pedidosservice.users.ports.out.IUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements IOrderRepositoryGateway {

    private final IOrderJpaRepository orderJpaRepository;
    private final IOrderItemJpaRepository orderItemJpaRepository;
    private final IProductJpaRepository productJpaRepository;
    private final OrderMapper mapper;
    private final UserMapper userMapper;

    @Override
    public Optional<OrderDomain> getOrderById(String id) {
        if (id == null) return Optional.empty();
        return orderJpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public Optional<OrderItemDomain> getOrderItemById(String itemId) {
        // Vefificação de item
        if (itemId == null) return Optional.empty();
        return orderItemJpaRepository.findById(itemId).map(mapper::itemEntityToDomain);
    }

    @Override
    @Transactional
    public OrderDomain createOrder(OrderDomain domain, UserDomain user) {

        // Persistência e acoplamento de user com order
        OrderEntity entity = mapper.domainToEntity(domain);
        entity.setUser(userMapper.domainToEntity(user));
        return mapper.entityToDomain(orderJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public OrderDomain updateOrderStatus(String orderId, OrderStatus status) {
        OrderEntity entity = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.entityToDomain(orderJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public OrderDomain saveOrderItems(String orderId, List<OrderItemDomain> preparedItems, double newTotal) {
        OrderEntity order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        for (OrderItemDomain itemDomain : preparedItems) {
            ProductEntity product = productJpaRepository.findById(itemDomain.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDomain.getProductId()));

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrder(order);
            itemEntity.setProduct(product);
            itemEntity.setProductName(itemDomain.getProductName());
            itemEntity.setPrice(itemDomain.getPrice());
            itemEntity.setQuantity(itemDomain.getQuantity());
            order.getOrderItems().add(itemEntity);

            product.setStock(product.getStock() - itemDomain.getQuantity());
            productJpaRepository.save(product);
        }

        order.setTotalAmount(newTotal);
        order.setUpdatedAt(LocalDateTime.now());

        return mapper.entityToDomain(orderJpaRepository.save(order));
    }

    @Override
    @Transactional
    public void removeOrderById(String id) {
        OrderEntity order = orderJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        for (OrderItemEntity item : order.getOrderItems()) {
            ProductEntity product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productJpaRepository.save(product);
        }

        orderJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removeOrderItemById(String orderId, String itemId, double newTotal) {
        OrderEntity order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        OrderItemEntity item = orderItemJpaRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + itemId));

        order.setTotalAmount(newTotal);
        order.setUpdatedAt(LocalDateTime.now());
        order.getOrderItems().remove(item);
        orderJpaRepository.save(order);

        ProductEntity product = item.getProduct();
        product.setStock(product.getStock() + item.getQuantity());
        productJpaRepository.save(product);
    }

    @Override
    public List<OrderDomain> getOrdersByUserId(String userId) {
        return orderJpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::entityToDomain)
                .collect(Collectors.toList());
    }
}
