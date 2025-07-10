package com.example.restaurantapplication.screens.cuisine

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Cuisine
import com.example.restaurantapplication.screens.home.TopDishAdapter
import java.net.URL
import kotlin.concurrent.thread

class CuisineActivity : AppCompatActivity() {

    private lateinit var dishRecyclerView: RecyclerView
    private lateinit var cuisineImage: ImageView
    private lateinit var cuisineName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuisine)

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        dishRecyclerView = findViewById(R.id.rv_dish_list)
        cuisineImage = findViewById(R.id.iv_cuisine_header)
        cuisineName = findViewById(R.id.tv_cuisine_header)

        dishRecyclerView.layoutManager = LinearLayoutManager(this)

        val cuisine = intent.getSerializableExtra("selected_cuisine") as? Cuisine

        if (cuisine != null) {
            cuisineName.text = cuisine.cuisine_name

            // Load cuisine image
            thread {
                try {
                    val input = URL(cuisine.cuisine_image_url).openStream()
                    val bitmap = BitmapFactory.decodeStream(input)
                    cuisineImage.post {
                        cuisineImage.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Construct cuisine map
            val cuisineMap = mutableMapOf<String, String>()
            cuisine.items.forEach { dish ->
                cuisineMap[dish.id] = cuisine.cuisine_id
            }

            // Pass to adapter
            val adapter = TopDishAdapter(this, cuisine.items, cuisineMap)
            dishRecyclerView.adapter = adapter
        }
    }
}
