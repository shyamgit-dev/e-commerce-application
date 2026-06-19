package com.sam.service;

import com.sam.constant.AddressType;
import com.sam.constant.OrderStatus;
import com.sam.dto.OrderDTO;
import com.sam.dto.OrderItemDTO;
import com.sam.dto.RevenueDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    public OrderDTO placeOrder(Long id, OrderDTO orderDTO, AddressType addressType);

    public List<OrderDTO> getOrder(Long userId);

    public OrderDTO get(Long id);

    public Page<OrderDTO> getAll(int pageNumber,int pageSize,String sortField);

    public OrderDTO cancelOrder(Long userId,Long orderId);

    public OrderDTO changeOrderStatus(Long orderId, OrderStatus newStatus);

    public List<OrderDTO> getByStatus(OrderStatus orderStatus);

    public List<OrderDTO> findByDateInRange(LocalDateTime start,LocalDateTime end);

    public RevenueDTO getTotalRevenue();
}
