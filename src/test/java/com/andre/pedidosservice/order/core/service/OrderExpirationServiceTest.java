package com.andre.pedidosservice.order.core.service;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderExpirationServiceTest {

    @Mock
    private OrderRepositoryGateway repository;

    @InjectMocks
    private OrderExpirationService service;

    private OrderDomain orderWithId(String id) {
        OrderDomain order = new OrderDomain();
        order.setId(id);
        order.setStatus(OrderStatus.PENDENTE);
        return order;
    }

    @Test
    @DisplayName("expireOrders should mark each expired order as EXPIRADO")
    void should_MarkEachOrderAsExpired_when_ExpireOrders() {
        when(repository.findExpiredOrders())
                .thenReturn(List.of(orderWithId("1"), orderWithId("2")));

        service.expireOrders();

        verify(repository).updateStatus("1", OrderStatus.EXPIRADO);
        verify(repository).updateStatus("2", OrderStatus.EXPIRADO);
    }

    @Test
    @DisplayName("expireOrders should not update status when there are no expired orders")
    void should_NotUpdateAnything_when_NoOrders() {
        when(repository.findExpiredOrders()).thenReturn(List.of());

        service.expireOrders();

        verify(repository, never()).updateStatus(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("deleteExpiredOrders should delete orders by EXPIRADO status")
    void should_DeleteOrders_when_StatusIsExpired() {
        service.deleteExpiredOrders();

        verify(repository).deleteByStatus(OrderStatus.EXPIRADO);
    }
}
