package com.example.restaurantapplication.data.models

data class CartItem(
    val cuisine_id: String,
    val item_id: String,
    val item_name: String,
    val item_price: Int,
    var item_quantity: Int,
    val item_image_url: String,
    val item_rating: String
)

