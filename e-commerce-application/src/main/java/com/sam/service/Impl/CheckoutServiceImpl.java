package com.sam.service.Impl;

import com.sam.constant.OrderStatus;
import com.sam.constant.PaymentStatus;
import com.sam.dao.AddressRepository;
import com.sam.dao.OrderRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.CheckoutRequest;
import com.sam.dto.OrderDTO;
import com.sam.entity.*;
import com.sam.exception.AddressNotFoundException;
import com.sam.exception.CartItemNotFoundException;
import com.sam.exception.InsufficientStockException;
import com.sam.exception.InvalidActionException;
import com.sam.service.CheckoutService;
import com.sam.utility.SecurityIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service("checkoutService")
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    private final SecurityIntegration securityIntegration;

    @Override
    public OrderDTO checkout(CheckoutRequest checkoutRequest) {

        //Authentication
        User user = securityIntegration.getAuthenticatedUser();

        if(user.getCart()==null)
            throw new RuntimeException("User have no cart");

        Order order = new Order();
        order.setUser(user);

        BigDecimal subtotal = BigDecimal.ZERO;

        if(user.getCart().getCartItems().isEmpty())
             throw new CartItemNotFoundException("Empty Cart Item");

        for(CartItem cartItem:user.getCart().getCartItems())
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setProduct(cartItem.getProduct());

            if(cartItem.getQuantity()>cartItem.getProduct().getStockQuantity())
                 throw new InsufficientStockException("Insufficient stock");

            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

            BigDecimal itemtotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            subtotal = subtotal.add(itemtotal);
        }

        order.setTotalAmount(subtotal);

        if(user.getAddresses()==null)
             throw new AddressNotFoundException("No address associated with user add address");

        Address address = addressRepository.findByIdAndUserId(checkoutRequest.getAddressId(), user.getUserId())
                .orElseThrow(()->
                    new AddressNotFoundException("Address not found"));


        order.setShippingAddress(address.getStreet()+" "+address.getCity()+" "+
                address.getState()+" "+address.getCountry()+" "+address.getZipCode());

        order.setStatus(OrderStatus.PAYMENT_PENDING);

        order.setOrderDate(LocalDateTime.now());

        order.setPaymentStatus(PaymentStatus.PENDING);

        order.setPaymentMethod(checkoutRequest.getPaymentMethod());

        order.setTrackingNumber(generateTracingNumber());

        Order createdOrder = orderRepository.save(order);

        return modelMapper.map(createdOrder, OrderDTO.class);
    }

    private String generateTracingNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int random = 1000 + secureRandom.nextInt(9000);
        return "IPON"+random;

    }
}
