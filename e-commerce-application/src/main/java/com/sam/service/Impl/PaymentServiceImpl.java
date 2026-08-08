package com.sam.service.Impl;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sam.constant.NotificationType;
import com.sam.constant.OrderStatus;
import com.sam.constant.PaymentMethod;
import com.sam.constant.PaymentStatus;
import com.sam.dao.CouponUsageRepository;
import com.sam.dao.OrderRepository;
import com.sam.dao.PaymentRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.PaymentOrderRequest;
import com.sam.dto.PaymentOrderResponse;
import com.sam.dto.PaymentSuccessResponse;
import com.sam.dto.PaymentVerificationRequest;
import com.sam.entity.*;
import com.sam.exception.InsufficientStockException;
import com.sam.exception.InvalidActionException;
import com.sam.exception.OrderNotFoundException;
import com.sam.service.EmailService;
import com.sam.service.NotificationService;
import com.sam.service.PaymentService;
import com.sam.utility.SecurityIntegration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service("paymentService")
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay_key_secret}")
    private String secret;

    private final RazorpayClient razorpayClient;

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final CouponUsageRepository couponUsageRepository;

    private final NotificationService notificationService;

    private final SecurityIntegration securityIntegration;

    private final EmailService emailService;

    @Transactional
    @Override
    public PaymentOrderResponse createPaymentOrder(PaymentOrderRequest orderRequest) throws RazorpayException {

        log.info("Starting to create a new razorpay payment order");

        Order order = orderRepository.findById(orderRequest.getOrderId())
                .orElseThrow(()-> new OrderNotFoundException("Order Not Found"));

        User user = securityIntegration.getAuthenticatedUser();

        if(!(order.getUser().getUserId().equals(user.getUserId())))
              throw new AccessDeniedException("You don't have access");

        if(order.getStatus() != OrderStatus.PAYMENT_PENDING)
            throw new InvalidActionException("Order Is Already Paid");

        Payment existingPayment = order.getPayment();

        if(existingPayment!=null && existingPayment.getStatus()==PaymentStatus.PENDING)
        {
            PaymentOrderResponse response = new PaymentOrderResponse();
            response.setId(existingPayment.getRazorpayOrderId());
            response.setAmount(existingPayment.getAmount());
            response.setStatus("created");
            response.setCreatedAt(existingPayment.getCreatedAt());
            response.setCurrency("INR");
            return response;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",
                order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue());
        jsonObject.put("currency","INR");
        jsonObject.put("receipt","ORDER_"+order.getId());

        com.razorpay.Order razorPayOrder = razorpayClient.orders.create(jsonObject);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.RAZORPAY);
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setRazorpayOrderId(razorPayOrder.get("id"));

        paymentRepository.save(payment);

        log.info("Created Payment order {} with due amount {}",
                razorPayOrder.get("id"),
                razorPayOrder.get("amount")
                );

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setId(razorPayOrder.get("id"));
        Integer amount = razorPayOrder.get("amount");
        response.setAmount(BigDecimal.valueOf(amount*0.01));
        response.setStatus(razorPayOrder.get("status"));
        response.setCreatedAt(LocalDateTime.now());
        response.setCurrency(razorPayOrder.get("currency"));
        response.setEntity(razorPayOrder.get("entity"));

        return response;
    }

    @Transactional
    @Override
    public PaymentSuccessResponse verifyPayment(PaymentVerificationRequest request) throws RazorpayException {

        log.info("Starting to verify the payment");

        //Authenticated User
        User user = securityIntegration.getAuthenticatedUser();

        //Fetched Payment Using Razorpay OrderId
        Payment payment =
                paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                        .orElseThrow(()->new RuntimeException("Razorpay Order Id Not Found"));

        if(!(payment.getOrder().getUser().getUserId().equals(user.getUserId())))
            throw new AccessDeniedException("Access Denied");

        if(payment.getStatus()!=PaymentStatus.PENDING)
            throw new RuntimeException("Illegal Payment State");

        String signature = request.getRazorpayOrderId()+"|"+request.getRazorpayPaymentId();

        //Verified Signatur Here
        boolean isValid = Utils.verifySignature(signature,request.getRazorpaySignature(),secret);

        PaymentSuccessResponse response = new PaymentSuccessResponse();

        if(isValid)
        {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setPaymentStatus(payment.getStatus());
            order.setStatus(OrderStatus.CONFIRMED);

            for(OrderItem orderItem: order.getOrderItems())
            {
                //Reduced stock quantity from product
                Product product = orderItem.getProduct();

                if(product.getStockQuantity()<orderItem.getQuantity())
                     throw new InsufficientStockException("Insufficient Stock");

                product.setStockQuantity(product.getStockQuantity()-orderItem.getQuantity());
            }
            //set cart to null
            if(user.getCart()==null)
                throw new IllegalStateException("Empty Cart");

            List<CartItem> cartItems = user.getCart().getCartItems();
            cartItems.clear();

            user.getCart().setSubTotal(BigDecimal.ZERO);

            log.info("Payment verified successfully having Razorpay Payment Id {}",
                    request.getRazorpayPaymentId()
                    );

            response.setPaymentStatus(payment.getStatus());
            response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
            response.setRazorpayOrderId(payment.getRazorpayOrderId());
            response.setAmount(payment.getAmount());
        }
        else
        {
            log.warn("Payment Signature not verified for order {}",
                    payment.getOrder().getId()
                    );

            payment.setStatus(PaymentStatus.FAILED);
            payment.getOrder().setPaymentStatus(PaymentStatus.FAILED);
            response.setPaymentStatus(PaymentStatus.FAILED);
            response.setRazorpayPaymentId(null);
            response.setRazorpayOrderId(payment.getRazorpayOrderId());
            response.setAmount(payment.getAmount());

            return response;
        }

        if(payment.getStatus()==PaymentStatus.SUCCESS)
        {
            //SENDING NOTIFICATION AFTER SUCCESSFULL PAYMENT
            notificationService.createNotification(
                    user,
                    NotificationType.PAYMENT_SUCCESS,
                    "Payment Successful",
                    "Your Payment was Successful",
                    payment.getOrder()
            );

            try {
                emailService.sendEmail(
                        user,
                        "Payment Paid Successfully",
                        "Payment of Rupees "+payment.getAmount() +" for order #"+payment.getOrder().getId()+" has been paid successfully"
                );

                emailService.sendEmailSendGrid(
                        user,
                        "Payment Paid Successfully",
                        "Payment of Rupees "+payment.getAmount() +" for order #"+payment.getOrder().getId()+" has been paid successfully"

                );
            } catch (Exception e) {
                log.error("Failed to send email",e);
            }

            CouponUsage couponUsage = new CouponUsage();

            Coupon coupon = payment.getOrder().getCoupon();

            if(coupon!=null && !couponUsageRepository.existsByOrder(payment.getOrder())) {
                couponUsage.setUser(user);
                couponUsage.setCoupon(coupon);
                couponUsage.setUsedAt(LocalDate.now());
                couponUsage.setOrder(payment.getOrder());

                coupon.setUsedCount(coupon.getUsedCount() + 1);

                couponUsageRepository.save(couponUsage);

                notificationService.createNotification(
                        user,
                        NotificationType.COUPON_RECEIVED,
                        "Coupon Redeemed Successfully",
                        "Lucky!! Fellow",
                        payment.getOrder()
                );

                log.info("Coupon {} is used in order {} for user {} ",
                        coupon.getCode(),
                        payment.getOrder().getId(),
                        user.getUsername()
                );
            }
        }
        else if (payment.getStatus()==PaymentStatus.FAILED)
        {
            notificationService.createNotification(
                    user,
                    NotificationType.PAYMENT_FAILED,
                    "PAYMENT FAILED",
                    "Please try again later!!",
                    payment.getOrder()
            );
        }
        return response;
    }
}
