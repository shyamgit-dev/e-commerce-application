package com.sam.controller;

import com.sam.dao.CartRequestDTO;
import com.sam.dto.CartDTO;
import com.sam.dto.CartItemDTO;
import com.sam.service.CartService;
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

    @PostMapping("/cart/items")
    public ResponseEntity<CartDTO> addTOCart(@RequestBody CartRequestDTO cartRequestDTO)
    {
       return new ResponseEntity<>(cartService.addTOCart(cartRequestDTO), HttpStatus.CREATED);
    }

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


}
