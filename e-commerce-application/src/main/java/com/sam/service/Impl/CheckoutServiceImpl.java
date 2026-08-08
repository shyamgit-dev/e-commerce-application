package com.sam.service.Impl;

import com.sam.constant.OrderStatus;
import com.sam.constant.PaymentStatus;
import com.sam.dao.*;
import com.sam.dto.CheckoutRequest;
import com.sam.dto.OrderDTO;
import com.sam.entity.*;
import com.sam.exception.*;
import com.sam.service.CheckoutService;
import com.sam.utility.SecurityIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service("checkoutService")
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    private final CouponRepository couponRepository;

    private final CouponUsageRepository couponUsageRepository;

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

        if(checkoutRequest.getCouponCode()!=null && !checkoutRequest.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(checkoutRequest.getCouponCode())
                    .orElseThrow(() -> new CouponNotFoundException("Coupon does not exists"));
            applyCoupon(order, user, coupon);
        }

        Order createdOrder = orderRepository.save(order);

        return modelMapper.map(createdOrder, OrderDTO.class);
    }

    private void applyCoupon(Order order,User user,Coupon coupon)
    {
        if(coupon!=null)
        {
            long count = couponUsageRepository.countByUserAndCoupon(user,coupon);

            if(count>=coupon.getMaxUsesPerUser())
                throw new InvalidActionException("Coupon is already used");

            if(!coupon.isActive())
                throw new InvalidActionException("Coupon is Inactive");

            if(coupon.getExpiryDate().isBefore(LocalDateTime.now()))
                throw new InvalidActionException("Coupon is expired");

            if(coupon.getUsedCount()>=coupon.getUsageLimit())
                throw new InvalidActionException("Coupon usage Limit Exceeds");

            if(order.getTotalAmount().compareTo(coupon.getMinimumOrderAmount())<0)
                throw new InvalidActionException("Minimum order amount is not satisfied");

            switch (coupon.getDiscountType())
            {
                case FIXED:
                    order.setDiscountAmount(coupon.getDiscountValue());
                    order.setTotalAmount(order.getTotalAmount().subtract(coupon.getDiscountValue()));
                    break;
                case PERCENTAGE:
                    BigDecimal discount = order.getTotalAmount().multiply(coupon.getDiscountValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    discount = discount.min(coupon.getMaximumDiscount());
                    order.setDiscountAmount(discount);
                    order.setTotalAmount(order.getTotalAmount().subtract(discount));
                    break;
            }
            order.setCoupon(coupon);

            log.info("Coupon Code {} is applied for order {} by User {}",
                    coupon.getCode(),
                    order.getId(),
                    user.getUsername()
                    );
        }
    }

    private String generateTracingNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int random = 1000 + secureRandom.nextInt(9000);
        return "IPON"+random;
    }
}
