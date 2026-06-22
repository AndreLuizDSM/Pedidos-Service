package com.andre.pedidosservice.order.adapters.in;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.dtos.OrderItemRequestDTO;
import com.andre.pedidosservice.order.dtos.OrderMapper;
import com.andre.pedidosservice.order.dtos.OrderResponseDTO;
import com.andre.pedidosservice.order.gateways.in.OrderGatewayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderGatewayService service;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder() {
        // Pega o ID do Usuario pelo SecurityContextHolder, que vem do UserDetails
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(mapper.domainToResponse(service.createOrder(userId)));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDTO> addOrderItem(@PathVariable String id,
                                                         @Valid @RequestBody List<OrderItemRequestDTO> items) {
        return ResponseEntity.ok(mapper.domainToResponse(
                service.addOrderItem(id, items.stream().map(mapper::requestToItemDomain).toList())));
    }

    @DeleteMapping("/{idOrder}/items/{idOrderItem}")
    public ResponseEntity<OrderResponseDTO> deleteOrderItem(@PathVariable String idOrder,
                                                            @PathVariable String idOrderItem) {

        // ID do pedido , e ID do Item
        return ResponseEntity.ok(mapper.domainToResponse(service.deleteOrderItem(idOrder, idOrderItem)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable String id,
                                                              @RequestParam OrderStatus status) {
        return ResponseEntity.ok(mapper.domainToResponse(service.updateOrderStatus(id, status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.domainToResponse(service.getOrderById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        service.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
