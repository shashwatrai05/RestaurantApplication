package com.example.restaurantapplication.screens.cart

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.CartItem
import java.net.URL
import kotlin.concurrent.thread

class CartAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.name.text = item.item_name
        holder.quantity.text = "Qty: ${item.item_quantity}"
        holder.price.text = "₹${item.item_price * item.item_quantity}"

        thread {
            try {
                val input = URL(item.item_image_url).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                holder.image.post {
                    holder.image.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cart_item_image)
        val name: TextView = view.findViewById(R.id.cart_item_name)
        val quantity: TextView = view.findViewById(R.id.cart_item_quantity)
        val price: TextView = view.findViewById(R.id.cart_item_price)
    }
}
