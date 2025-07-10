package com.example.restaurantapplication.screens.cart

import com.example.restaurantapplication.data.models.CartItem
import com.example.restaurantapplication.data.models.Item

object CartManager {
    private val cartItems = mutableMapOf<String, CartItem>()

    fun addToCart(item: Item, cuisineId: String) {
        val key = item.id
        val existing = cartItems[key]
        if (existing != null) {
            existing.item_quantity++
        } else {
            cartItems[key] = CartItem(
                cuisine_id = cuisineId,
                item_id = item.id,
                item_name = item.name,
                item_price = item.price.toInt(),
                item_quantity = 1,
                item_image_url = item.image_url,
                item_rating = item.rating
            )
        }
    }

    fun removeFromCart(itemId: String) {
        val item = cartItems[itemId]
        if (item != null) {
            if (item.item_quantity > 1) {
                item.item_quantity--
            } else {
                cartItems.remove(itemId)
            }
        }
    }

    fun getCartItems(): List<CartItem> = cartItems.values.toList()

    fun clearCart() = cartItems.clear()
}
