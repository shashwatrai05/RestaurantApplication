package com.example.restaurantapplication

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.data.models.Cuisine
import com.example.restaurantapplication.data.models.Item
import com.example.restaurantapplication.data.network.ApiClient
import com.example.restaurantapplication.screens.cart.CartActivity
import com.example.restaurantapplication.screens.home.CuisineAdapter
import com.example.restaurantapplication.screens.home.TopDishAdapter
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var cuisineRecyclerView: RecyclerView
    private lateinit var topDishRecyclerView: RecyclerView
    private lateinit var cuisineAdapter: CuisineAdapter
    private lateinit var topDishAdapter: TopDishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Allow network on main thread (for now)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        cuisineRecyclerView = findViewById(R.id.rv_cuisine)
        topDishRecyclerView = findViewById(R.id.rv_top_dishes)

        cuisineRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        topDishRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchCuisineData()

        findViewById<Button>(R.id.btn_cart).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_language).setOnClickListener {
            toggleLanguage()
        }

    }

    private fun fetchCuisineData() {
        val jsonBody = """
            {
                "page": 1,
                "count": 10
            }
        """.trimIndent()

        val response = ApiClient.postRequest(
            endpoint = "/emulator/interview/get_item_list",
            jsonBody = jsonBody,
            proxyAction = "get_item_list"
        )

        if (response != null) {
            val json = JSONObject(response)
            val cuisineList = mutableListOf<Cuisine>()
            val topDishes = mutableListOf<Item>()

            val cuisinesArray = json.getJSONArray("cuisines")
            for (i in 0 until cuisinesArray.length()) {
                val cuisineObj = cuisinesArray.getJSONObject(i)
                val itemsArray = cuisineObj.getJSONArray("items")
                val itemsList = mutableListOf<Item>()

                for (j in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(j)
                    val item = Item(
                        id = itemObj.getString("id"),
                        name = itemObj.getString("name"),
                        image_url = itemObj.getString("image_url"),
                        price = itemObj.getString("price"),
                        rating = itemObj.getString("rating")
                    )
                    itemsList.add(item)

                    if (topDishes.size < 3) topDishes.add(item)
                }

                val cuisine = Cuisine(
                    cuisine_id = cuisineObj.getString("cuisine_id"),
                    cuisine_name = cuisineObj.getString("cuisine_name"),
                    cuisine_image_url = cuisineObj.getString("cuisine_image_url"),
                    items = itemsList
                )

                cuisineList.add(cuisine)
            }

            cuisineAdapter = CuisineAdapter(this, cuisineList)
            topDishAdapter = TopDishAdapter(this, topDishes)

            cuisineRecyclerView.adapter = cuisineAdapter
            topDishRecyclerView.adapter = topDishAdapter
        } else {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleLanguage() {
        val currentLang = resources.configuration.locales.get(0).language
        val newLang = if (currentLang == "en") "hi" else "en"

        val locale = Locale(newLang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)

        // Update configuration and restart activity
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // restart MainActivity with new language
    }

}
