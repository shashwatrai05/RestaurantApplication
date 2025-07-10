package com.example.restaurantapplication.screens.filter

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Item
import org.json.JSONArray
import org.json.JSONObject

class FilterActivity : AppCompatActivity() {

    private val cuisines = listOf("North Indian", "South Indian", "Chinese", "Mexican", "Italian")
    private val selectedCuisines = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)

        val cuisineLayout = findViewById<LinearLayout>(R.id.layout_cuisine_checkboxes)
        cuisines.forEach { cuisine ->
            val checkBox = CheckBox(this)
            checkBox.text = cuisine
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedCuisines.add(cuisine)
                else selectedCuisines.remove(cuisine)
            }
            cuisineLayout.addView(checkBox)
        }

        val spinner = findViewById<Spinner>(R.id.spinner_rating)
        val ratings = listOf("Any", "1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratings)
        spinner.adapter = adapter

        findViewById<Button>(R.id.btn_apply_filter).setOnClickListener {
            applyFilters()
        }
    }

    private fun applyFilters() {
        val minPrice = findViewById<EditText>(R.id.et_min_price).text.toString().toIntOrNull()
        val maxPrice = findViewById<EditText>(R.id.et_max_price).text.toString().toIntOrNull()
        val ratingStr = findViewById<Spinner>(R.id.spinner_rating).selectedItem.toString()
        val minRating = ratingStr.toIntOrNull()

        if (selectedCuisines.isEmpty() && minPrice == null && maxPrice == null && minRating == null) {
            Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show()
            return
        }

        val priceRange = if (minPrice != null && maxPrice != null) {
            """
            "price_range": {
                "min_amount": $minPrice,
                "max_amount": $maxPrice
            },
            """
        } else ""

        val cuisineFilter = if (selectedCuisines.isNotEmpty()) {
            "\"cuisine_type\": ${JSONArray(selectedCuisines)},"
        } else ""

        val ratingFilter = if (minRating != null) {
            "\"min_rating\": $minRating"
        } else ""

        val finalJson = """
        {
            $cuisineFilter
            $priceRange
            $ratingFilter
        }
        """.trimIndent()

        val response = ApiClient.postRequest(
            endpoint = "/emulator/interview/get_item_by_filter",
            jsonBody = finalJson,
            proxyAction = "get_item_by_filter"
        )

        if (response != null) {
            val json = JSONObject(response)
            val cuisines = json.getJSONArray("cuisines")
            val resultItems = mutableListOf<Item>()

            for (i in 0 until cuisines.length()) {
                val cuisine = cuisines.getJSONObject(i)
                val items = cuisine.getJSONArray("items")

                for (j in 0 until items.length()) {
                    val itemObj = items.getJSONObject(j)
                    resultItems.add(
                        Item(
                            id = itemObj.getString("id"),
                            name = itemObj.getString("name"),
                            image_url = itemObj.getString("image_url"),
                            price = itemObj.optString("price", "0"),
                            rating = itemObj.optString("rating", "N/A")
                        )
                    )
                }
            }

            // TODO: Pass resultItems to a result screen OR show in a dialog/list
            Toast.makeText(this, "Found ${resultItems.size} dishes", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to fetch filtered items", Toast.LENGTH_SHORT).show()
        }
    }
}
