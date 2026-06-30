package com.sam.service.Impl;

import com.sam.dao.*;
import com.sam.dto.CartDTO;
import com.sam.dto.CartItemDTO;
import com.sam.entity.Cart;
import com.sam.entity.CartItem;
import com.sam.entity.Product;
import com.sam.entity.User;
import com.sam.exception.*;
import com.sam.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service("cartService")
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository itemRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public CartDTO addTOCart(CartRequestDTO cartRequestDTO) {

        log.info("Starting to add products to the cart");

        Product product = productRepository.findById(cartRequestDTO.getProductId())
                .orElseThrow(()-> {
                    log.info("Fetched product {}", cartRequestDTO.getProductId());
                    return new ProductNotFoundException("Product Not Found");
                });

        if(cartRequestDTO.getQuantity()>product.getStockQuantity())
            throw new InsufficientStockException("Less stock as per the request");

        User user = getCurrentUser();

        Cart cart = user.getCart();

        if(cart==null)
        {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
            cart.setCartItems(new ArrayList<>());
        }

        Optional<CartItem> existingItem =
                cart.getCartItems()
                        .stream()
                        .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                        .findFirst();

        if(existingItem.isPresent())
        {
            CartItem cartItem = existingItem.get();
            int updatedQuantity = cartItem.getQuantity()+ cartRequestDTO.getQuantity();
            if(updatedQuantity> product.getStockQuantity())
                throw new InsufficientStockException("Insufficient Stock");
            cartItem.setQuantity(updatedQuantity);
        }
        else
        {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(cartRequestDTO.getQuantity());
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        }

        BigDecimal subTotal = BigDecimal.ZERO;

        for(CartItem cartItem: cart.getCartItems())
        {
            BigDecimal itemTotal =
                    cartItem.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(itemTotal);
        }

        cart.setSubTotal(subTotal);

        Cart addedCart = cartRepository.save(cart);

        log.info("Product {} added to the cart for user {}",
                product.getId(),
                user.getUsername()
        );

        return modelMapper.map(addedCart, CartDTO.class);
    }

    @Override
    public CartDTO getCart() {
        User user = getCurrentUser();

        if (user.getCart() == null) {
            throw new RuntimeException("Your cart is empty");
        }

        Cart cart = user.getCart();

        log.info("Fetched cart {}",user.getCart());

        return modelMapper.map(cart, CartDTO.class);
    }

    @Transactional
    @Override
    public CartItemDTO updateCart(Long cartItemId,CartItemDTO dto) {

        CartItem cartItem = itemRepository.findById(cartItemId)
                .orElseThrow(()->{
                    log.info("Fetched CartItem {} ",cartItemId);
                    return new CartItemNotFoundException("CartItem you want to update not found");});

        if(dto.getQuantity()>=0)
            throw new InvalidActionException("You can't set quantity equal to zero or non-negative number");

        cartItem.setQuantity(dto.getQuantity());

        Cart cart = cartItem.getCart();

        BigDecimal subtotal = BigDecimal.ZERO;

        for(CartItem item:cart.getCartItems())
        {
            BigDecimal itemTotal =
                    item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        cart.setSubTotal(subtotal);
        //cart.getCartItems().add(cartItem);
        CartItem updatedCartItem = itemRepository.save(cartItem);
        log.info("Updated cartItem {}",updatedCartItem.getId());
        return modelMapper.map(updatedCartItem,CartItemDTO.class);
    }

    @Transactional
    @Override
    public void deleteCartItem(Long cartItemId) {

        CartItem cartItem = itemRepository.findById(cartItemId)
                .orElseThrow(()-> new CartItemNotFoundException("CartItem Not Found"));

        User user = getCurrentUser();

        if(!cartItem.getCart().getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("You can't delete others cart");

        itemRepository.delete(cartItem);
    }

    @Modifying
    @Transactional
    @Override
    public void deleteCart() {

        User user = getCurrentUser();

        if(user.getCart()==null)
            throw new InvalidActionException("User is yet to create a cart or no cart associated with user");

        Cart cart = user.getCart();
        cart.setUser(null);
        user.setCart(null);

        cartRepository.delete(cart);
    }

    private User getCurrentUser()
    {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || !authentication.isAuthenticated())
            throw new AccessDeniedException("Not Authenticated ");

        String username = authentication.getName();

        User user;
        user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));

        return user;
    }
}

