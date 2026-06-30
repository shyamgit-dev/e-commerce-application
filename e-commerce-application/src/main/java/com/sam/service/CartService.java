package  com.sam.service;

import com.sam.dao.CartRequestDTO;
import com.sam.dto.CartDTO;
import com.sam.dto.CartItemDTO;

public interface CartService {
    CartDTO addTOCart(CartRequestDTO cartRequestDTO);
    CartDTO getCart();
    CartItemDTO updateCart(Long cartItemId, CartItemDTO dto);
    void deleteCartItem(Long cartItemId);
    void deleteCart();

}
