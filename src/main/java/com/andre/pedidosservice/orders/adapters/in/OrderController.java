package com.andre.pedidosservice.orders.adapters.in;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.gateways.in.IOrderGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderGatewayService service;

    @PostMapping
    public ResponseEntity<OrderDomain> createOrder(@RequestBody OrderDomain domain) {
        return ResponseEntity.ok(service.createOrder(domain));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderDomain> addOrderItem(@PathVariable("id") String id,
                                                    @RequestBody List<OrderItemDomain> items) {
        return ResponseEntity.ok(service.addOrderItem(id, items));
    }

    @DeleteMapping("/{idOrder}/items/{idOrderItem}")
    public ResponseEntity<OrderDomain> deleteOrderItem(@PathVariable("idOrder") String idOrder,
                                                       @PathVariable("idOrderItem") String idOrderItem) {
        return ResponseEntity.ok(service.deleteOrderItem(idOrder, idOrderItem));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDomain> updateStatusOrder(@PathVariable("id") String id,
                                                         @RequestParam("status") OrderStatus status) {
        return ResponseEntity.ok(service.updateStatusOrder(status, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDomain> getOrderById(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getOrderByID(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") String id) {
        service.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
