package com.andre.pedidosservice.order.adapters.in;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.dtos.OrderItemRequestDTO;
import com.andre.pedidosservice.order.dtos.OrderMapper;
import com.andre.pedidosservice.order.dtos.OrderResponseDTO;
import com.andre.pedidosservice.order.gateways.in.OrderGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Orders")
public class OrderController {

    private final OrderGatewayService service;
    private final OrderMapper mapper;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Order created successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<OrderResponseDTO> createOrder() {
        // Pega o ID do Usuario pelo SecurityContextHolder, que vem do UserDetails
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(mapper.domainToResponse(service.createOrder(userId)));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add order items", description = "Add one or more items to an existing order")
    @ApiResponse(responseCode = "200", description = "Items added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderResponseDTO> addOrderItem(@PathVariable String id,
                                                         @Valid @RequestBody List<OrderItemRequestDTO> items) {
        return ResponseEntity.ok(mapper.domainToResponse(
                service.addOrderItem(id, items.stream().map(mapper::requestToItemDomain).toList())));
    }

    @DeleteMapping("/{idOrder}/items/{idOrderItem}")
    @Operation(summary = "Delete order item", description = "Remove a specific item from an order")
    @ApiResponse(responseCode = "200", description = "Item removed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Order or item not found")
    public ResponseEntity<OrderResponseDTO> deleteOrderItem(@PathVariable String idOrder,
                                                            @PathVariable String idOrderItem) {

        // ID do pedido , e ID do Item
        return ResponseEntity.ok(mapper.domainToResponse(service.deleteOrderItem(idOrder, idOrderItem)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable String id,
                                                              @RequestParam OrderStatus status) {
        return ResponseEntity.ok(mapper.domainToResponse(service.updateOrderStatus(id, status)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id", description = "Retrieve an order by its identifier")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.domainToResponse(service.getOrderById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Delete an order by its identifier")
    @ApiResponse(responseCode = "200", description = "Order deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        service.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
