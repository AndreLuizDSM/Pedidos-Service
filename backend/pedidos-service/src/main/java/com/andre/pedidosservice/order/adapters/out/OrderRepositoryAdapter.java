package com.andre.pedidosservice.order.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.dtos.OrderMapper;
import com.andre.pedidosservice.order.entities.OrderEntity;
import com.andre.pedidosservice.order.entities.OrderItemEntity;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.order.ports.out.OrderItemJpaRepository;
import com.andre.pedidosservice.order.ports.out.OrderJpaRepository;
import com.andre.pedidosservice.product.entities.ProductEntity;
import com.andre.pedidosservice.product.ports.out.ProductJpaRepository;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryGateway {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderMapper mapper;
    private final UserMapper userMapper;

    private final String orderNotFound = "Pedido não encontrado ";

    @Override
    public Optional<OrderDomain> findById(String id) {
        if (id == null) return Optional.empty();
        return orderJpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public Optional<OrderItemDomain> findItemById(String itemId) {
        if (itemId == null) return Optional.empty();
        return orderItemJpaRepository.findById(itemId).map(mapper::itemEntityToDomain);
    }

    @Override
    @Transactional
    public OrderDomain save(OrderDomain domain, UserDomain user) {
        OrderEntity entity = mapper.domainToEntity(domain);
        entity.setUser(userMapper.domainToEntity(user));

        return mapper.entityToDomain(orderJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public OrderDomain updateStatus(String orderId, OrderStatus status) {
        OrderEntity entity = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(orderNotFound));
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.entityToDomain(orderJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public OrderDomain saveItems(OrderDomain order, List<OrderItemDomain> preparedItems, double newTotal) {
        OrderEntity orderEntity = orderJpaRepository.findById(order.getId()).orElseThrow(
                () -> new ResourceNotFoundException(orderNotFound + order.getId())
        );

        for (OrderItemDomain itemDomain : preparedItems) {
            ProductEntity product = productJpaRepository.findById(itemDomain.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDomain.getProductId()));

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrder(orderEntity);
            itemEntity.setProduct(product);
            itemEntity.setProductName(itemDomain.getProductName());
            itemEntity.setProductPrice(itemDomain.getProductPrice());
            itemEntity.setQuantity(itemDomain.getQuantity());
            orderEntity.getOrderItems().add(itemEntity);

            product.setStock(product.getStock() - itemDomain.getQuantity());
            productJpaRepository.save(product);

        }

        orderEntity.setExpiresAt(LocalDateTime.now().plusHours(6));
        orderEntity.setTotalAmount(newTotal);
        orderEntity.setUpdatedAt(LocalDateTime.now());

        return mapper.entityToDomain(orderJpaRepository.save(orderEntity));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        OrderEntity order = orderJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(orderNotFound));

        // Loop para cada item que possuia dentro do pedido , fazer o estoque de volta
        for (OrderItemEntity item : order.getOrderItems()) {
            ProductEntity product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productJpaRepository.save(product);
        }

        orderJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteItemById(String orderId, String itemId, double newTotal) {
        OrderEntity order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(orderNotFound));

        OrderItemEntity item = orderItemJpaRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + itemId));

        // Atualiza a persistencia
        order.setTotalAmount(newTotal);
        order.setUpdatedAt(LocalDateTime.now());
        order.getOrderItems().remove(item);
        order.setExpiresAt(LocalDateTime.now().plusHours(6));
        orderJpaRepository.save(order);

        ProductEntity product = item.getProduct();
        product.setStock(product.getStock() + item.getQuantity());
        productJpaRepository.save(product);
    }

    @Override
    public List<OrderDomain> findByUserId(String userId) {
        return orderJpaRepository.findByUserId(userId)
                // Recebe a lista , transforma individualmente cada usando map e transforma em lista novamente
                .stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    // - - - - - - - - - métodos para scheduled - - - - - - - - -
    @Override
    public List<OrderDomain> findExpiredOrders() {
        // Retorna os itens Pendentes que já passaram do prazo de validade
        return orderJpaRepository.findByStatusAndExpiresAtBefore(OrderStatus.PENDENTE, LocalDateTime.now())
                .stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByStatus(OrderStatus status) {
        // Deleta Order pelo status
        orderJpaRepository.deleteByStatus(status);
    }

}
