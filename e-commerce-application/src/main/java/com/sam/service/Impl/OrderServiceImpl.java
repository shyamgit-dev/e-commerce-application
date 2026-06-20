package com.sam.service.Impl;

import com.sam.constant.AddressType;
import com.sam.constant.OrderStatus;
import com.sam.dao.OrderItemRepository;
import com.sam.dao.OrderRepository;
import com.sam.dao.ProductRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.OrderDTO;
import com.sam.dto.OrderItemDTO;
import com.sam.dto.ProductDTO;
import com.sam.dto.RevenueDTO;
import com.sam.entity.*;
import com.sam.exception.*;
import com.sam.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service("orderService")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(Long id, OrderDTO orderDTO, AddressType addressType) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User Not Found with Id "+id+" or it's Inactive"));

        Order order = new Order();
        order.setUser(user);//Setting User Id Over Here
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus("PENDING");
        order.setTrackingNumber(generateTracingNumber());

        //Fetching address of user and placing order on specific address by using addressType
        Address address = user.getAddresses()
                .stream()
                .filter(a -> a.getAddressType().equals(addressType))
                .findFirst()
                .orElseThrow(() ->
                        new AddressNotFoundException("No address with the type "+addressType+", create a new address"));

        String shippingAdd = address.getStreet()+","+address.getCity()+","+address.getCountry()+","+address.getZipCode();

        order.setShippingAddress(shippingAdd);
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemDTO dto:orderDTO.getOrderItems())
        {
            Product product = productRepository.findbyProductAndQuantity(dto.getProductId(),dto.getQuantity())
                    .orElseThrow(()->new InsufficientStockException("Either Selected Product Is out-of-stock or Does not exists "));

            product.setStockQuantity(product.getStockQuantity()-dto.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(dto.getQuantity());
            BigDecimal unitPrice = product.getPrice();
            orderItem.setUnitPrice(unitPrice);
            orderItem.setProduct(product);

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(dto.getQuantity()));
            orderItem.setSubTotal(subtotal);
            //itemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder,OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getOrder(Long userId) {
        List<Order> orders =orderRepository.findByUserId(userId);
        if(orders.isEmpty()) throw new OrderNotFoundException("User has not placed order yet");
        List<OrderDTO> orderDTOS = new ArrayList<>();
        orders.forEach(order -> {
            OrderDTO orderDTO = modelMapper.map(order,OrderDTO.class);
            orderDTOS.add(orderDTO);
        });
        return orderDTOS;
    }

    @Override
    public OrderDTO get(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()->new OrderNotFoundException("No Order Found With Id "+id));
        return modelMapper.map(order,OrderDTO.class);
    }

    @Override
    public Page<OrderDTO> getAll(int pageNumber, int pageSize, String sortField) {
        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC,sortField)
        );
        Page<Order> orders = orderRepository.findAll(pageRequest);
        return orders.map(order -> modelMapper.map(order,OrderDTO.class));
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(Long userId,Long orderId) {
        Order order = orderRepository.findByUserAndOrder(userId,orderId)
                .orElseThrow(()->new OrderNotFoundException("UserId/OrderId didn't matched"));

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new InvalidActionException("Order already cancelled");

        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new InvalidActionException("Delivered orders cannot be cancelled");

        if (order.getStatus() == OrderStatus.SHIPPED)
            throw new InvalidActionException("Shipped orders cannot be cancelled");

        for(OrderItem orderItem:order.getOrderItems())
        {
            Product product = productRepository.findById(orderItem.getProduct().getId())
                    .orElseThrow(()->new ProductNotFoundException("Product Not Found"));
            product.setStockQuantity(
                    product.getStockQuantity()+orderItem.getQuantity()
            );
        }
        com.sam.entity.Order cancelledOrder =orderRepository.save(order);
        return modelMapper.map(cancelledOrder,OrderDTO.class);
    }

    @Transactional
    @Override
    public OrderDTO changeOrderStatus(Long orderId,OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new OrderNotFoundException("Invalid OrderId "+orderId+"or Order Does not exists"));

        OrderStatus currentStatus = order.getStatus();

        switch (currentStatus) {

            case CREATED -> {
                if(newStatus==OrderStatus.CONFIRMED ||
                        newStatus==OrderStatus.CANCELLED)
                {
                    order.setStatus(newStatus);
                }
                else
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
            }
            case CONFIRMED -> {
                if(newStatus==OrderStatus.PROCESSING ||
                        newStatus==OrderStatus.CANCELLED)
                {
                    order.setStatus(newStatus);
                }
                else
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
            }
            case PROCESSING -> {
                if(newStatus==OrderStatus.SHIPPED)
                    order.setStatus(newStatus);
                else
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
            }
            case SHIPPED -> {
                if(newStatus==OrderStatus.OUT_FOR_DELIVERY)
                    order.setStatus(newStatus);
                else
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
            }
            case OUT_FOR_DELIVERY -> {
                if(newStatus==OrderStatus.DELIVERED)
                    order.setStatus(newStatus);
                else
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
            }
            case DELIVERED, CANCELLED ->
                    throw new InvalidActionException("Invalid order status transition from "+order.getStatus()+" => "+newStatus);
        }
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder,OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getByStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findByStatus(orderStatus);
        if(orders.isEmpty()) throw new OrderNotFoundException("No orders based on applied status "+orderStatus);
        return orders.stream()
                .map(order -> modelMapper.map(order,OrderDTO.class)).toList();
    }

    @Override
    public List<OrderDTO> findByDateInRange(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByOrderDateBetween(start,end);
        if(orders.isEmpty()) throw new OrderNotFoundException("No Order found in this range of "+start+" and "+end);
        return orders.stream()
                .map(order -> modelMapper.map(order,OrderDTO.class)).toList();
    }

    @Override
    public RevenueDTO getTotalRevenue() {
        BigDecimal totalRevenue = orderRepository.findByDeliveredStatus()
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        RevenueDTO revenueDTO = new RevenueDTO();
        revenueDTO.setTotalRevenue(totalRevenue);
        return revenueDTO;
    }

    private String generateTracingNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int random = 1000 + secureRandom.nextInt(9000);
        return "IPON"+random;

    }
}
