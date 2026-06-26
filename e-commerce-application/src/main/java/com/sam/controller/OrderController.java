package com.sam.controller;

import com.sam.constant.AddressType;
import com.sam.constant.OrderStatus;
import com.sam.dto.OrderDTO;
import com.sam.dto.RevenueDTO;
import com.sam.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/users/{userId}/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderDTO> placeOrder(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody OrderDTO orderDTO,
            @RequestParam("addressType") AddressType addressType)
    {
        return new ResponseEntity<>(orderService.placeOrder(userId,orderDTO,addressType), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDTO>> getOrder(@PathVariable Long userId)
    {
        return new ResponseEntity<>(orderService.getOrder(userId),HttpStatus.OK);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> get(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(orderService.get(id),HttpStatus.OK);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAll(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "4") int pageSize,
           @RequestParam(defaultValue = "id") String sortField)
    {
        return new ResponseEntity<>(orderService.getAll(pageNumber,pageSize,sortField),HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/orders/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> cancelOrder(
           @PathVariable Long userId,
           @PathVariable Long orderId)
    {
        return new ResponseEntity<>(orderService.cancelOrder(userId,orderId),HttpStatus.OK);
    }

    @PatchMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<OrderDTO> changeOrderStatus(@PathVariable Long orderId,
                                                     @RequestParam("status") OrderStatus newStatus)
    {
        return new ResponseEntity<>(orderService.changeOrderStatus(orderId,newStatus),HttpStatus.OK);
    }

    @GetMapping("/orders/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getByStatus(
           @RequestParam(defaultValue = "CREATED") OrderStatus status)
    {
        return new ResponseEntity<>(orderService.getByStatus(status),HttpStatus.OK);
    }

    @GetMapping("/orders/range")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrderInRange(
           @RequestParam(defaultValue = "2026-06-12T16:20:37.609937") LocalDateTime start,
           @RequestParam(defaultValue = "2026-06-15T16:20:37.609937") LocalDateTime end)
    {
       return new ResponseEntity<>(orderService.findByDateInRange(start,end),HttpStatus.OK);
    }

    @GetMapping("/orders/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RevenueDTO> fetchByDeliveredStatus()
    {
        return new ResponseEntity<>(orderService.getTotalRevenue(),HttpStatus.OK);
    }
}
