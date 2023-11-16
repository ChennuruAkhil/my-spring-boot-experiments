package com.example.custom.sequence.web.controllers;

import com.example.custom.sequence.entities.Order;
import com.example.custom.sequence.model.response.OrderResponse;
import com.example.custom.sequence.model.response.PagedResult;
import com.example.custom.sequence.services.OrderService;
import com.example.custom.sequence.utils.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PagedResult<OrderResponse> getAllOrders(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
                    int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
                    int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
                    String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
                    String sortDir) {
        return orderService.findAllOrders(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return orderService
                .findOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody @Validated Order order) {
        return orderService.saveOrder(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable String id, @RequestBody Order order) {
        return orderService
                .findOrderById(id)
                .map(
                        orderObj -> {
                            order.setId(id);
                            return ResponseEntity.ok(orderService.saveOrder(order));
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable String id) {
        return orderService
                .findOrderById(id)
                .map(
                        order -> {
                            orderService.deleteOrderById(id);
                            return ResponseEntity.ok(order);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
