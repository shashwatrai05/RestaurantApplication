package com.example.restaurantapplication.screens.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Cuisine
import java.net.URL
import android.graphics.BitmapFactory
import com.example.restaurantapplication.screens.cuisine.CuisineActivity
import kotlin.concurrent.thread

class CuisineAdapter(
    private val context: Context,
    private val cuisineList: List<Cuisine>
) : RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cuisine, parent, false)
        return CuisineViewHolder(view)
    }

    override fun getItemCount(): Int = cuisineList.size

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        val cuisine = cuisineList[position]
        holder.name.text = cuisine.cuisine_name

        // Load image from URL (native)
        thread {
            try {
                val input = URL(cuisine.cuisine_image_url).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                holder.image.post {
                    holder.image.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CuisineActivity::class.java)
            intent.putExtra("selected_cuisine", cuisine)
            context.startActivity(intent)
        }

    }

    class CuisineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.cuisine_name)
        val image: ImageView = itemView.findViewById(R.id.cuisine_image)
    }
}
