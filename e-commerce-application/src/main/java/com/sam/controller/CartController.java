package com.sam.controller;

import com.sam.dto.*;
import com.sam.service.CartService;
import com.sam.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    private final CheckoutService checkoutService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/items")
    public ResponseEntity<CartDTO> addTOCart(@RequestBody CartRequestDTO cartRequestDTO)
    {
       return new ResponseEntity<>(cartService.addTOCart(cartRequestDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart")
    public ResponseEntity<CartDTO> getCart()
    {
        return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
    }

    @PatchMapping("/cart/items/{id}")
    public ResponseEntity<CartItemDTO> updateCart(@PathVariable("id") Long cartItemId, @RequestBody CartItemDTO dto)
    {
        return new ResponseEntity<>(cartService.updateCart(cartItemId,dto),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("id") Long cartItemId)
    {
        String result = "Selected Item with Id "+cartItemId+" has been deleted";
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteCart()
    {
        String result = "Entire cart has been deleted";
        cartService.deleteCart();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/checkout")
    public ResponseEntity<OrderDTO> checkout(@RequestBody CheckoutRequest checkoutRequest)
    {
        return new ResponseEntity<>(checkoutService.checkout(checkoutRequest), HttpStatus.CREATED);
    }


}
