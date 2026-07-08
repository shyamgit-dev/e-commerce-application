package com.sam.service;

import com.sam.dto.CheckoutRequest;
import com.sam.dto.OrderDTO;

public interface CheckoutService {

    OrderDTO checkout(CheckoutRequest checkoutRequest);
}
