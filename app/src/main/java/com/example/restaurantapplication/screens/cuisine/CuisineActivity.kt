package com.example.restaurantapplication.screens.cuisine

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Cuisine
import com.example.restaurantapplication.data.models.Item
import java.net.URL
import kotlin.concurrent.thread

class CuisineActivity : AppCompatActivity() {

    private lateinit var dishRecyclerView: RecyclerView
    private lateinit var cuisineImage: ImageView
    private lateinit var cuisineName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuisine)

        dishRecyclerView = findViewById(R.id.rv_dish_list)
        cuisineImage = findViewById(R.id.iv_cuisine_header)
        cuisineName = findViewById(R.id.tv_cuisine_header)

        dishRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get passed cuisine
        val cuisine = intent.getSerializableExtra("selected_cuisine") as? Cuisine
        if (cuisine != null) {
            cuisineName.text = cuisine.cuisine_name
            loadImage(cuisine.cuisine_image_url, cuisineImage)
            dishRecyclerView.adapter = CuisineDishAdapter(this, cuisine.items)
        } else {
            Toast.makeText(this, "Invalid cuisine", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadImage(url: String, imageView: ImageView) {
        thread {
            try {
                val stream = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(stream)
                imageView.post {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
