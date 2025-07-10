package com.example.restaurantapplication

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.data.models.Cuisine
import com.example.restaurantapplication.data.models.Item
import com.example.restaurantapplication.screens.cart.CartActivity
import com.example.restaurantapplication.screens.cuisine.CuisineActivity
//import com.example.restaurantapplication.screens.filter.FilterActivity
import com.example.restaurantapplication.screens.home.CuisineAdapter
import com.example.restaurantapplication.screens.home.TopDishAdapter
//import com.example.restaurantapplication.data.network.ApiClient
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var cuisineRecyclerView: RecyclerView
    private lateinit var topDishRecyclerView: RecyclerView
    private lateinit var cuisineAdapter: CuisineAdapter
    private lateinit var topDishAdapter: TopDishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lang = getSharedPreferences("settings", MODE_PRIVATE).getString("lang", "en")
        val locale = Locale(lang!!)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        setContentView(R.layout.activity_main)

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        cuisineRecyclerView = findViewById(R.id.rv_cuisine)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(cuisineRecyclerView)

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

//        findViewById<Button>(R.id.btn_filter).setOnClickListener {
//            val intent = Intent(this, FilterActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun fetchCuisineData() {
        val mergedCuisineMap = mutableMapOf<String, Cuisine>()
        val topDishes = mutableListOf<Item>()
        val topDishCuisineMap = mutableMapOf<String, String>()

        for (page in 1..7) {
            val jsonBody = """
            {
                "page": $page,
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
                val cuisinesArray = json.getJSONArray("cuisines")

                for (i in 0 until cuisinesArray.length()) {
                    val cuisineObj = cuisinesArray.getJSONObject(i)
                    val cuisineId = cuisineObj.getString("cuisine_id")
                    val cuisineName = cuisineObj.getString("cuisine_name")
                    val cuisineImageUrl = cuisineObj.getString("cuisine_image_url")
                    val itemsArray = cuisineObj.getJSONArray("items")

                    val itemList = mutableListOf<Item>()
                    for (j in 0 until itemsArray.length()) {
                        val itemObj = itemsArray.getJSONObject(j)
                        val item = Item(
                            id = itemObj.getString("id"),
                            name = itemObj.getString("name"),
                            image_url = itemObj.getString("image_url"),
                            price = itemObj.getString("price"),
                            rating = itemObj.getString("rating")
                        )
                        itemList.add(item)

                        if (topDishes.size < 3) {
                            topDishes.add(item)
                            topDishCuisineMap[item.id] = cuisineId
                        }
                    }

                    if (mergedCuisineMap.containsKey(cuisineId)) {
                        mergedCuisineMap[cuisineId]?.items?.addAll(itemList)
                    } else {
                        mergedCuisineMap[cuisineId] = Cuisine(
                            cuisine_id = cuisineId,
                            cuisine_name = cuisineName,
                            cuisine_image_url = cuisineImageUrl,
                            items = itemList
                        )
                    }
                }
            }
        }

        val cuisineList = mergedCuisineMap.values.toList()
        cuisineAdapter = CuisineAdapter(this, cuisineList)
        cuisineRecyclerView.adapter = cuisineAdapter

        topDishAdapter = TopDishAdapter(this, topDishes, topDishCuisineMap)
        topDishRecyclerView.adapter = topDishAdapter
    }

    private fun toggleLanguage() {
        val currentLang = resources.configuration.locales.get(0).language
        val newLang = if (currentLang == "en") "hi" else "en"

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putString("lang", newLang).apply()

        val locale = Locale(newLang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}
