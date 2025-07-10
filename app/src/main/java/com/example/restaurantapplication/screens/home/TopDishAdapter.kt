package com.example.restaurantapplication.screens.home

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Item
import com.example.restaurantapplication.screens.cart.CartManager
import java.net.URL
import kotlin.concurrent.thread

class TopDishAdapter(
    private val context: Context,
    private val dishList: List<Item>,
    private val cuisineMap: Map<String, String> // <itemId, cuisineId>
) : RecyclerView.Adapter<TopDishAdapter.DishViewHolder>() {


    // Track quantity
    private val dishQuantities = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_top_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishList[position]
        holder.name.text = dish.name
        holder.price.text = "₹${dish.price}"
        holder.rating.text = "⭐ ${dish.rating}"

        // Load image
        thread {
            try {
                val input = URL(dish.image_url).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                holder.image.post {
                    holder.image.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Handle quantity
        val currentQty = dishQuantities[dish.id] ?: 0
        holder.quantity.text = "Qty: $currentQty"

        holder.addBtn.setOnClickListener {
            val cuisineId = cuisineMap[dish.id] ?: ""
            CartManager.addToCart(dish, cuisineId)
// ✅ Correct

            val updated = (dishQuantities[dish.id] ?: 0) + 1
            dishQuantities[dish.id] = updated
            holder.quantity.text = "Qty: $updated"
            Toast.makeText(context, "${dish.name} added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = dishList.size

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.dish_image)
        val name: TextView = itemView.findViewById(R.id.dish_name)
        val price: TextView = itemView.findViewById(R.id.dish_price)
        val rating: TextView = itemView.findViewById(R.id.dish_rating)
        val quantity: TextView = itemView.findViewById(R.id.dish_quantity)
        val addBtn: Button = itemView.findViewById(R.id.btn_add)
    }
}
