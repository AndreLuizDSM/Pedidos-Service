package com.andre.pedidosservice.order.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryGateway repository;
    @Mock
    private ProductRepositoryGateway productRepository;
    @Mock
    private UserRepositoryGateway userRepository;

    @InjectMocks
    private OrderService service;

    // ---------- createOrder ----------

    @Test
    @DisplayName("createOrder: usuário existente -> salva e retorna o pedido")
    void createOrder_usuarioExistente_salvaPedido() {
        UserDomain user = new UserDomain();
        OrderDomain saved = new OrderDomain();
        saved.setId("order-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(repository.save(any(OrderDomain.class), any(UserDomain.class))).thenReturn(saved);

        OrderDomain result = service.createOrder("user-1");

        assertSame(saved, result);
        verify(repository).save(any(OrderDomain.class), any(UserDomain.class));
    }

    @Test
    @DisplayName("createOrder: usuário inexistente -> ResourceNotFoundException")
    void createOrder_usuarioInexistente_lancaResourceNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createOrder("missing"));
    }

    // ---------- getOrderById ----------

    @Test
    @DisplayName("getOrderById: pedido inexistente -> ResourceNotFoundException")
    void getOrderById_inexistente_lancaResourceNotFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getOrderById("x"));
    }

    // ---------- addOrderItem ----------

    @Test
    @DisplayName("addOrderItem: pedido não PENDENTE -> InvalidRequestException")
    void addOrderItem_pedidoNaoPendente_lancaInvalidRequest() {
        OrderDomain order = new OrderDomain();
        order.setStatus(OrderStatus.CONFIRMADO);
        when(repository.findById("order-1")).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestException.class,
                () -> service.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 1))));
    }

    @Test
    @DisplayName("addOrderItem: estoque insuficiente -> InvalidRequestException")
    void addOrderItem_estoqueInsuficiente_lancaInvalidRequest() {
        OrderDomain order = new OrderDomain();
        order.setStatus(OrderStatus.PENDENTE);
        when(repository.findById("order-1")).thenReturn(Optional.of(order));

        ProductDomain product = new ProductDomain("prod-1", 10.0, "Café", 2);
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));

        assertThrows(InvalidRequestException.class,
                () -> service.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 5))));
    }

    @Test
    @DisplayName("addOrderItem: itens válidos -> recalcula total e persiste")
    void addOrderItem_itensValidos_recalculaTotalEPersiste() {
        OrderDomain order = new OrderDomain();
        order.setStatus(OrderStatus.PENDENTE);
        order.setTotalAmount(0);
        when(repository.findById("order-1")).thenReturn(Optional.of(order));

        ProductDomain product = new ProductDomain("prod-1", 10.0, "Café", 100);
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(repository.saveItems(any(), any(), org.mockito.ArgumentMatchers.eq(30.0)))
                .thenReturn(order);

        service.addOrderItem("order-1", List.of(new OrderItemDomain("prod-1", 3)));

        // 3 x 10.0 = 30.0
        verify(repository).saveItems(any(), any(), org.mockito.ArgumentMatchers.eq(30.0));
    }

    // ---------- deleteOrder ----------

    @Test
    @DisplayName("deleteOrder: pedido existente -> deleta pelo id")
    void deleteOrder_existente_deletaPeloId() {
        when(repository.findById("order-1")).thenReturn(Optional.of(new OrderDomain()));

        service.deleteOrder("order-1");

        verify(repository).deleteById("order-1");
    }

    // ---------- updateOrderStatus ----------

    @Test
    @DisplayName("updateOrderStatus: pedido existente -> atualiza status")
    void updateOrderStatus_existente_atualizaStatus() {
        OrderDomain updated = new OrderDomain();
        when(repository.findById("order-1")).thenReturn(Optional.of(new OrderDomain()));
        when(repository.updateStatus("order-1", OrderStatus.CONFIRMADO)).thenReturn(updated);

        OrderDomain result = service.updateOrderStatus("order-1", OrderStatus.CONFIRMADO);

        assertSame(updated, result);
    }

    // ---------- getOrdersByUserId ----------

    @Test
    @DisplayName("getOrdersByUserId: delega ao repositório")
    void getOrdersByUserId_delegaAoRepositorio() {
        List<OrderDomain> orders = List.of(new OrderDomain(), new OrderDomain());
        when(repository.findByUserId("user-1")).thenReturn(orders);

        assertEquals(2, service.getOrdersByUserId("user-1").size());
    }

    // TODO: deleteOrderItem
    //   - item inexistente -> ResourceNotFoundException
    //   - item não pertence ao pedido -> ResourceNotFoundException
    //   - caminho feliz -> recalcula total e remove item da lista
}
