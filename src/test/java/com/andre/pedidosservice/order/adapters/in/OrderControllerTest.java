package com.andre.pedidosservice.order.adapters.in;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.order.dtos.*;
import com.andre.pedidosservice.order.gateways.in.OrderGatewayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    OrderGatewayService service;
    @Mock
    OrderMapper mapper;

    private MockMvc mockMvc;
    private String url;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrderDomain orderDomain;
    private OrderItemDomain orderItemDomain;
    private OrderResponseDTO orderResponseDTO;

    private String itemsJson;

    private final String userId = "user-1";
    private final String id = "order-1";

    @BeforeEach
    void setup() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).alwaysDo(print()).build();
        url = "/order";

        orderDomain = new OrderDomain();
        orderDomain.setId(id);
        orderDomain.setStatus(OrderStatus.PENDENTE);

        orderItemDomain = new OrderItemDomain("prod-1", 3);

        orderResponseDTO = OrderResponseDTOFixture.build(
                id, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusHours(6),
                30.0, OrderStatus.PENDENTE, userId, List.of());

        OrderItemRequestDTO itemRequest = OrderItemRequestDTOFixture.build("prod-1", 3);
        itemsJson = objectMapper.writeValueAsString(List.of(itemRequest));
    }

    @AfterEach
    void clear() {
        // O createOrder lê o usuário do SecurityContextHolder — limpa para não vazar entre testes
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        // Simula o usuário autenticado que o controller extrai do contexto de segurança
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(service.createOrder(userId)).thenReturn(orderDomain);
        when(mapper.domainToResponse(orderDomain)).thenReturn(orderResponseDTO);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).createOrder(userId);
        verify(mapper).domainToResponse(orderDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveAdicionarItemAoPedidoComSucesso() throws Exception {
        when(mapper.requestToItemDomain(any())).thenReturn(orderItemDomain);
        when(service.addOrderItem(eq(id), anyList())).thenReturn(orderDomain);
        when(mapper.domainToResponse(orderDomain)).thenReturn(orderResponseDTO);

        mockMvc.perform(post(url + "/{id}/items", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(itemsJson)
        ).andExpect(status().isOk());

        verify(service).addOrderItem(eq(id), anyList());
        verify(mapper).domainToResponse(orderDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void naoDeveAdicionarItemSeJsonNull() throws Exception {
        // sem .content
        mockMvc.perform(post(url + "/{id}/items", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void deveDeletarItemDoPedidoComSucesso() throws Exception {
        // quando deleta um item , retorna o pedido
        when(service.deleteOrderItem(id, "item-1")).thenReturn(orderDomain);
        when(mapper.domainToResponse(orderDomain)).thenReturn(orderResponseDTO);

        mockMvc.perform(delete(url + "/{idOrder}/items/{idOrderItem}", id, "item-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).deleteOrderItem(id, "item-1");
        verify(mapper).domainToResponse(orderDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveAtualizarStatusDoPedidoComSucesso() throws Exception {
        when(service.updateOrderStatus(id, OrderStatus.CONFIRMADO)).thenReturn(orderDomain);
        when(mapper.domainToResponse(orderDomain)).thenReturn(orderResponseDTO);

        // status é passado por parametros
        mockMvc.perform(patch(url + "/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("status", "CONFIRMADO")
        ).andExpect(status().isOk());

        verify(service).updateOrderStatus(id, OrderStatus.CONFIRMADO);
        verify(mapper).domainToResponse(orderDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveBuscarPedidoPorIdComSucesso() throws Exception {
        when(service.getOrderById(id)).thenReturn(orderDomain);
        when(mapper.domainToResponse(orderDomain)).thenReturn(orderResponseDTO);

        mockMvc.perform(get(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).getOrderById(id);
        verify(mapper).domainToResponse(orderDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveDeletarPedidoComSucesso() throws Exception {
        mockMvc.perform(delete(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).deleteOrder(id);
        verifyNoMoreInteractions(service);
    }
}
