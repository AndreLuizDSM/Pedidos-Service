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
    @DisplayName("expireOrders deve marcar cada pedido expirado como EXPIRADO")
    void expireOrders_marcaCadaPedidoComoExpirado() {
        when(repository.findExpiredOrders())
                .thenReturn(List.of(orderWithId("1"), orderWithId("2")));

        service.expireOrders();

        verify(repository).updateStatus("1", OrderStatus.EXPIRADO);
        verify(repository).updateStatus("2", OrderStatus.EXPIRADO);
    }

    @Test
    @DisplayName("expireOrders não deve atualizar status quando não houver pedidos expirados")
    void expireOrders_semPedidos_naoAtualizaNada() {
        when(repository.findExpiredOrders()).thenReturn(List.of());

        service.expireOrders();

        verify(repository, never()).updateStatus(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("deleteExpiredOrders deve deletar pedidos pelo status EXPIRADO")
    void deleteExpiredOrders_deletaPeloStatusExpirado() {
        service.deleteExpiredOrders();

        verify(repository).deleteByStatus(OrderStatus.EXPIRADO);
    }
}
