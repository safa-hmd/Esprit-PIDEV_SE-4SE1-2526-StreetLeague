package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.*;

import java.util.List;

public interface PanierService {

    void addToCart(AddToCartDTO dto);

    PanierResponseDTO getUserCart(Long userId);

    void updateQuantity(UpdateCartDTO dto);

    void removeItem(Long lignePanierId);

    void clearCart(Long userId);

    PromoValidationDTO validatePromo(String code, Long userId, List<CartItemDTO> items);
}
