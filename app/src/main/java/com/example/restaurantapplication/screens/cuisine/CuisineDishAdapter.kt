package com.example.restaurantapplication.screens.cuisine
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.Item
import java.net.URL
import kotlin.concurrent.thread

class CuisineDishAdapter(
    private val context: Context,
    private val dishList: List<Item>
) : RecyclerView.Adapter<CuisineDishAdapter.DishViewHolder>() {

    private val quantities = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cuisine_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishList[position]
        holder.name.text = dish.name
        holder.price.text = "₹${dish.price}"

        holder.addBtn.setOnClickListener {
            val updated = (quantities[dish.id] ?: 0) + 1
            quantities[dish.id] = updated
            holder.qty.text = "Qty: $updated"
        }

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
    }

    override fun getItemCount(): Int = dishList.size

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_dish_image)
        val name: TextView = itemView.findViewById(R.id.tv_dish_name)
        val price: TextView = itemView.findViewById(R.id.tv_dish_price)
        val qty: TextView = itemView.findViewById(R.id.tv_qty)
        val addBtn: Button = itemView.findViewById(R.id.btn_add_dish)
    }
}
