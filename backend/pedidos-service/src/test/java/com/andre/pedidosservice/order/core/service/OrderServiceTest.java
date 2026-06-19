package com.andre.pedidosservice.order.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.gateways.out.OrderNotificationGateway;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepositoryGateway repository;
    @Mock
    private ProductRepositoryGateway productRepository;
    @Mock
    private UserRepositoryGateway userRepository;
    @Mock
    private OrderNotificationGateway notificationGateway;

    private UserDomain userDomain;
    private OrderDomain orderDomain;

    @BeforeEach
    void setup() {
        userDomain = new UserDomain();
        userDomain.setId("user-1");

        orderDomain = new OrderDomain();
        orderDomain.setId("order-1");
        orderDomain.setStatus(OrderStatus.PENDENTE);
        orderDomain.setTotalAmount(0);
    }

                        // createOrder

    @Test
    void should_CreateOrder_when_DataIsValid() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(userDomain));
        when(repository.save(any(OrderDomain.class), any(UserDomain.class))).thenReturn(orderDomain);

        OrderDomain result = orderService.createOrder("user-1");

        assertNotNull(result);
        assertEquals("order-1", result.getId());

        verify(userRepository).findById("user-1");
        verify(repository).save(any(OrderDomain.class), any(UserDomain.class));

        // Toda criação de pedido deve publicar o evento de notificação
        verify(notificationGateway).notifyOrderCreated(any());
        verifyNoMoreInteractions(userRepository, repository, notificationGateway);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_UserNotFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("user-1"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Usuario nao encontrado user-1"));

        verify(userRepository).findById("user-1");
        verifyNoMoreInteractions(userRepository, repository);
    }

                        // getOrderById

    @Test
    void should_ReturnOrder_when_IdExists() {
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));

        OrderDomain result = orderService.getOrderById("order-1");

        assertNotNull(result);
        assertEquals("order-1", result.getId());

        verify(repository).findById("order-1");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_OrderNotFound() {
        when(repository.findById("order-1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById("order-1"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Pedido não encontrado"));

        verify(repository).findById("order-1");
        verifyNoMoreInteractions(repository);
    }

                        // addOrderItem

    @Test
    void should_AddOrderItem_when_DataIsValid() {
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));
        when(productRepository.findById("prod-1"))
                .thenReturn(Optional.of(new ProductDomain("prod-1", 10.0, "Café", 100)));
        // 3 x 10.0 = 30.0
        when(repository.saveItems(any(), any(), eq(30.0))).thenReturn(orderDomain);

        OrderDomain result = orderService.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 3)));

        assertNotNull(result);

        verify(repository).findById("order-1");
        verify(productRepository).findById("prod-1");
        verify(repository).saveItems(any(), any(), eq(30.0));
        verifyNoMoreInteractions(repository, productRepository);
    }

    @Test
    void should_ThrowInvalidRequestException_when_OrderIsNotPending() {
        orderDomain.setStatus(OrderStatus.CONFIRMADO);
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));

        InvalidRequestException e = assertThrows(InvalidRequestException.class,
                () -> orderService.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 1))));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Apenas pedidos pendentes podem receber itens"));

        verify(repository).findById("order-1");
        verifyNoMoreInteractions(repository, productRepository);
    }

    @Test
    void should_ThrowInvalidRequestException_when_StockIsInsufficient() {
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));
        when(productRepository.findById("prod-1"))
                .thenReturn(Optional.of(new ProductDomain("prod-1", 10.0, "Café", 2)));

        InvalidRequestException e = assertThrows(InvalidRequestException.class,
                () -> orderService.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 5))));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), containsString("Estoque insuficiente"));

        verify(repository).findById("order-1");
        verify(productRepository).findById("prod-1");
        verifyNoMoreInteractions(repository, productRepository);
    }

                        // updateOrderStatus

    @Test
    void should_UpdateOrderStatus_when_DataIsValid() {
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));
        when(repository.updateStatus("order-1", OrderStatus.CONFIRMADO)).thenReturn(orderDomain);

        OrderDomain result = orderService.updateOrderStatus("order-1", OrderStatus.CONFIRMADO);

        assertNotNull(result);

        verify(repository).findById("order-1");
        verify(repository).updateStatus("order-1", OrderStatus.CONFIRMADO);

        // Status diferente de ENTREGUE não deve disparar o evento de finalização
        verifyNoMoreInteractions(repository, notificationGateway);
    }

    // testa o notifyOrderFinished quando orderDomain.status é ENTREGUE
    @Test
    void should_NotifyOrderFinished_when_StatusIsEntregue() {
        orderDomain.setStatus(OrderStatus.ENTREGUE);
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));
        when(repository.updateStatus("order-1", OrderStatus.ENTREGUE)).thenReturn(orderDomain);

        OrderDomain result = orderService.updateOrderStatus("order-1", OrderStatus.ENTREGUE);

        assertNotNull(result);

        verify(repository).findById("order-1");
        verify(repository).updateStatus("order-1", OrderStatus.ENTREGUE);
        verify(notificationGateway).notifyOrderFinished(any());
        verifyNoMoreInteractions(repository, notificationGateway);
    }

                        // deleteOrder

    @Test
    void should_DeleteOrder_when_IdExists() {
        when(repository.findById("order-1")).thenReturn(Optional.of(orderDomain));

        orderService.deleteOrder("order-1");

        verify(repository).findById("order-1");
        verify(repository).deleteById("order-1");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_OrderNotFoundOnDelete() {
        when(repository.findById("order-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder("order-1"));

        verify(repository).findById("order-1");
        verifyNoMoreInteractions(repository);
    }

                        // getOrdersByUserId

    @Test
    void should_ReturnOrders_when_UserIdExists() {
        when(repository.findByUserId("user-1")).thenReturn(List.of(new OrderDomain(), new OrderDomain()));

        List<OrderDomain> result = orderService.getOrdersByUserId("user-1");

        assertEquals(2, result.size());

        verify(repository).findByUserId("user-1");
        verifyNoMoreInteractions(repository);
    }
}
