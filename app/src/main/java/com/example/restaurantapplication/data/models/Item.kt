package com.example.restaurantapplication.data.models

import java.io.Serializable

data class Item(
    val id: String,
    val name: String,
    val image_url: String,
    val price: String,
    val rating: String
) : Serializable
