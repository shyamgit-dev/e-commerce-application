package com.sam.dto;

import com.sam.constant.OrderStatus;
import com.sam.entity.OrderItem;
import com.sam.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
/*    private Long id;

   private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;*/

    private String shippingAddress;

    private String paymentMethod;

/*    private String paymentStatus;

    private String trackingNumber;

    private UserDTO userDTO;*/

    private List<OrderItemDTO> orderItems = new ArrayList<>();
}
