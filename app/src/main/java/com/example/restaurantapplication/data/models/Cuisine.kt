package com.example.restaurantapplication.data.models

import java.io.Serializable

data class Cuisine(
    val cuisine_id: String,
    val cuisine_name: String,
    val cuisine_image_url: String,
    val items: MutableList<Item>

) : Serializable
