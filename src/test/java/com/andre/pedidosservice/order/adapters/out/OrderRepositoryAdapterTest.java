package com.andre.pedidosservice.order.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.dtos.OrderMapper;
import com.andre.pedidosservice.order.entities.OrderEntity;
import com.andre.pedidosservice.order.entities.OrderItemEntity;
import com.andre.pedidosservice.order.ports.out.OrderItemJpaRepository;
import com.andre.pedidosservice.order.ports.out.OrderJpaRepository;
import com.andre.pedidosservice.product.entities.ProductEntity;
import com.andre.pedidosservice.product.ports.out.ProductJpaRepository;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserMapper;
import com.andre.pedidosservice.user.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    @InjectMocks
    private OrderRepositoryAdapter adapter;

    @Mock
    private OrderJpaRepository orderJpaRepository;
    @Mock
    private OrderItemJpaRepository orderItemJpaRepository;
    @Mock
    private ProductJpaRepository productJpaRepository;
    @Mock
    private OrderMapper mapper;
    @Mock
    private UserMapper userMapper;

    private OrderDomain orderDomain;
    private OrderEntity orderEntity;
    private UserDomain userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setup() {

        // Por que domain e entity sem parâmetros ? Pois o adpater só faz o mappeamento e save no banco de dados
        // sem qualquer regra, então os parâmetros são irrelevantes , mas necessários na service.

        // o ID é uma regra de negócio do adapter para o save no banco de dados , por isso setamos esse parâmetro
        orderDomain = new OrderDomain();
        orderDomain.setId("order-1");
        orderDomain.setStatus(OrderStatus.PENDENTE);

        orderEntity = new OrderEntity();
        orderEntity.setId("order-1");
        orderEntity.setStatus(OrderStatus.PENDENTE);
        orderEntity.setOrderItems(new ArrayList<>());

        userDomain = new UserDomain();
        userDomain.setId("user-1");

        userEntity = UserEntity.builder().id("user-1").build();
    }

                        // findById

    @Test
    void should_ReturnOrder_when_IdExists() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        Optional<OrderDomain> result = adapter.findById("order-1");

        assertTrue(result.isPresent());
        assertEquals(orderDomain, result.get());

        verify(orderJpaRepository).findById("order-1");
        verify(mapper).entityToDomain(orderEntity);
        verifyNoMoreInteractions(orderJpaRepository);
    }

    @Test
    void should_ReturnEmpty_when_IdDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.empty());

        Optional<OrderDomain> result = adapter.findById("order-1");

        assertFalse(result.isPresent());

        verify(orderJpaRepository).findById("order-1");
        verifyNoMoreInteractions(orderJpaRepository);
    }

    @Test
    void should_ReturnEmpty_when_IdIsNull() {
        Optional<OrderDomain> result = adapter.findById(null);

        assertFalse(result.isPresent());
        // Curto-circuito: id nulo nem chega a consultar o banco
        verifyNoMoreInteractions(orderJpaRepository);
    }

                        // findItemById

    @Test
    void should_ReturnItem_when_ItemIdExists() {
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setId("item-1");
        OrderItemDomain itemDomain = new OrderItemDomain("prod-1", 2);

        when(orderItemJpaRepository.findById("item-1")).thenReturn(Optional.of(itemEntity));
        when(mapper.itemEntityToDomain(itemEntity)).thenReturn(itemDomain);

        Optional<OrderItemDomain> result = adapter.findItemById("item-1");

        assertTrue(result.isPresent());
        assertEquals(itemDomain, result.get());

        verify(orderItemJpaRepository).findById("item-1");
        verify(mapper).itemEntityToDomain(itemEntity);
        verifyNoMoreInteractions(orderItemJpaRepository);
    }

    @Test
    void should_ReturnEmpty_when_ItemIdIsNull() {
        Optional<OrderItemDomain> result = adapter.findItemById(null);

        assertFalse(result.isPresent());
        verifyNoMoreInteractions(orderItemJpaRepository);
    }

                        // save

    @Test
    void should_SetUserAndSave_when_SaveIsCalled() {
        when(mapper.domainToEntity(orderDomain)).thenReturn(orderEntity);
        when(userMapper.domainToEntity(userDomain)).thenReturn(userEntity);
        when(orderJpaRepository.save(orderEntity)).thenReturn(orderEntity);
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        OrderDomain result = adapter.save(orderDomain, userDomain);

        assertNotNull(result);
        assertEquals(orderDomain, result);
        // O dono do pedido deve ter sido vinculado antes de persistir
        assertEquals(userEntity, orderEntity.getUser());

        verify(mapper).domainToEntity(orderDomain);
        verify(userMapper).domainToEntity(userDomain);
        verify(orderJpaRepository).save(orderEntity);
        verify(mapper).entityToDomain(orderEntity);
        verifyNoMoreInteractions(orderJpaRepository);
    }

                        // updateStatus

    @Test
    void should_UpdateStatus_when_IdExists() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(orderJpaRepository.save(orderEntity)).thenReturn(orderEntity);
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        OrderDomain result = adapter.updateStatus("order-1", OrderStatus.CONFIRMADO);

        assertNotNull(result);
        assertEquals(orderDomain, result);
        // O status da entity deve ter sido atualizado antes de salvar
        assertEquals(OrderStatus.CONFIRMADO, orderEntity.getStatus());
        assertNotNull(orderEntity.getUpdatedAt());

        verify(orderJpaRepository).findById("order-1");
        verify(orderJpaRepository).save(orderEntity);
        verify(mapper).entityToDomain(orderEntity);
        verifyNoMoreInteractions(orderJpaRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_UpdateStatusIdDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.updateStatus("order-1", OrderStatus.CONFIRMADO));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Pedido não encontrado"));

        verify(orderJpaRepository).findById("order-1");
        verify(orderJpaRepository, never()).save(any(OrderEntity.class));
    }

                        // saveItems

    @Test
    void should_AddItemAndDecrementStock_when_SaveItemsIsCalled() {
        OrderItemDomain itemDomain = new OrderItemDomain("prod-1", 3);
        itemDomain.setProductName("Café");
        itemDomain.setProductPrice(10.0);

        ProductEntity product = new ProductEntity();
        product.setId("prod-1");
        product.setStock(100);

        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(productJpaRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(orderJpaRepository.save(orderEntity)).thenReturn(orderEntity);
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        OrderDomain result = adapter.saveItems(orderDomain, List.of(itemDomain), 30.0);

        assertNotNull(result);
        // Item adicionado ao pedido e estoque decrementado (100 - 3)
        assertEquals(1, orderEntity.getOrderItems().size());
        assertEquals(97, product.getStock());
        assertEquals(30.0, orderEntity.getTotalAmount());
        assertNotNull(orderEntity.getExpiresAt());

        verify(orderJpaRepository).findById("order-1");
        verify(productJpaRepository).findById("prod-1");
        verify(productJpaRepository).save(product);
        verify(orderJpaRepository).save(orderEntity);
        verify(mapper).entityToDomain(orderEntity);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_SaveItemsOrderDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.saveItems(orderDomain, List.of(new OrderItemDomain("prod-1", 1)), 10.0));

        assertThat(e.getMessage(), is("Pedido nao encontrado order-1"));

        verify(orderJpaRepository).findById("order-1");
        verifyNoMoreInteractions(productJpaRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_SaveItemsProductDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(productJpaRepository.findById("prod-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.saveItems(orderDomain, List.of(new OrderItemDomain("prod-1", 1)), 10.0));

        assertThat(e.getMessage(), is("Produto não encontrado: prod-1"));

        verify(orderJpaRepository).findById("order-1");
        verify(productJpaRepository).findById("prod-1");
        verify(productJpaRepository, never()).save(any(ProductEntity.class));
    }

                        // deleteById

    @Test
    void should_RestoreStockAndDelete_when_IdExists() {
        ProductEntity product = new ProductEntity();
        product.setId("prod-1");
        product.setStock(5);

        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(2);
        orderEntity.getOrderItems().add(item);

        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));

        adapter.deleteById("order-1");

        // Estoque devolvido (5 + 2) antes de remover o pedido
        assertEquals(7, product.getStock());

        verify(orderJpaRepository).findById("order-1");
        verify(productJpaRepository).save(product);
        verify(orderJpaRepository).deleteById("order-1");
    }

    @Test
    void should_ThrowResourceNotFoundException_when_DeleteIdDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.deleteById("order-1"));

        assertThat(e.getMessage(), is("Pedido não encontrado"));

        verify(orderJpaRepository).findById("order-1");
        verify(orderJpaRepository, never()).deleteById("order-1");
    }

                        // deleteItemById

    @Test
    void should_RestoreStockAndUpdateOrder_when_DeleteItemExists() {
        ProductEntity product = new ProductEntity();
        product.setId("prod-1");
        product.setStock(5);

        OrderItemEntity item = new OrderItemEntity();
        item.setId("item-1");
        item.setProduct(product);
        item.setQuantity(2);
        orderEntity.getOrderItems().add(item);

        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(orderItemJpaRepository.findById("item-1")).thenReturn(Optional.of(item));

        adapter.deleteItemById("order-1", "item-1", 0.0);

        // Pedido atualizado e item removido da lista
        assertEquals(0.0, orderEntity.getTotalAmount());
        assertFalse(orderEntity.getOrderItems().contains(item));
        // Estoque devolvido (5 + 2)
        assertEquals(7, product.getStock());

        verify(orderJpaRepository).findById("order-1");
        verify(orderItemJpaRepository).findById("item-1");
        verify(orderJpaRepository).save(orderEntity);
        verify(productJpaRepository).save(product);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_DeleteItemOrderDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.deleteItemById("order-1", "item-1", 0.0));

        assertThat(e.getMessage(), is("Pedido não encontrado"));

        verify(orderJpaRepository).findById("order-1");
        verify(orderJpaRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void should_ThrowResourceNotFoundException_when_DeleteItemDoesNotExist() {
        when(orderJpaRepository.findById("order-1")).thenReturn(Optional.of(orderEntity));
        when(orderItemJpaRepository.findById("item-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.deleteItemById("order-1", "item-1", 0.0));

        assertThat(e.getMessage(), is("Item não encontrado: item-1"));

        verify(orderJpaRepository).findById("order-1");
        verify(orderItemJpaRepository).findById("item-1");
        verify(orderJpaRepository, never()).save(any(OrderEntity.class));
    }

                        // findByUserId

    @Test
    void should_ReturnOrders_when_UserIdExists() {
        when(orderJpaRepository.findByUserId("user-1")).thenReturn(List.of(orderEntity));
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        List<OrderDomain> result = adapter.findByUserId("user-1");
        // apenas 1 pedido foi injetado para ser retornado, e foi o orderDomain
        assertEquals(1, result.size());
        assertEquals(orderDomain, result.get(0));

        verify(orderJpaRepository).findByUserId("user-1");
        verify(mapper).entityToDomain(orderEntity);
        verifyNoMoreInteractions(orderJpaRepository);
    }

                        // findExpiredOrders

    @Test
    void should_ReturnExpiredOrders_when_FindExpiredIsCalled() {
        // qualquer localdatetime
        when(orderJpaRepository.findByStatusAndExpiresAtBefore(eq(OrderStatus.PENDENTE), any(LocalDateTime.class)))
                .thenReturn(List.of(orderEntity));
        when(mapper.entityToDomain(orderEntity)).thenReturn(orderDomain);

        List<OrderDomain> result = adapter.findExpiredOrders();
        // apenas 1 foi injetado, e foi o orderDomain
        assertEquals(1, result.size());
        assertEquals(orderDomain, result.get(0));

        verify(orderJpaRepository).findByStatusAndExpiresAtBefore(eq(OrderStatus.PENDENTE), any(LocalDateTime.class));
        verify(mapper).entityToDomain(orderEntity);
        verifyNoMoreInteractions(orderJpaRepository);
    }

                        // deleteByStatus

    @Test
    void should_DelegateToRepository_when_DeleteByStatusIsCalled() {
        adapter.deleteByStatus(OrderStatus.EXPIRADO);

        verify(orderJpaRepository).deleteByStatus(OrderStatus.EXPIRADO);
        verifyNoMoreInteractions(orderJpaRepository);
    }
}
