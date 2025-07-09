package com.example.restaurantapplication.screens.cart

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.CartItem
import com.example.restaurantapplication.data.network.ApiClient
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var cartListLayout: LinearLayout
    private lateinit var totalAmountView: TextView
    private lateinit var cgstView: TextView
    private lateinit var sgstView: TextView
    private lateinit var grandTotalView: TextView
    private lateinit var placeOrderBtn: Button

    // Mock cart data
    private val cartItems = listOf(
        CartItem("234552", "34234234", "Butter Chicken", 199, 2),
        CartItem("234552", "34234236", "Aloo Gobhi", 120, 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartListLayout = findViewById(R.id.layout_cart_items)
        totalAmountView = findViewById(R.id.tv_total_amount)
        cgstView = findViewById(R.id.tv_cgst)
        sgstView = findViewById(R.id.tv_sgst)
        grandTotalView = findViewById(R.id.tv_grand_total)
        placeOrderBtn = findViewById(R.id.btn_place_order)

        populateCart()
        placeOrderBtn.setOnClickListener {
            makePayment()
        }
    }

    private fun populateCart() {
        var total = 0

        for (item in cartItems) {
            val row = TextView(this)
            row.text = "${item.item_name} x${item.item_quantity} = ₹${item.item_price * item.item_quantity}"
            cartListLayout.addView(row)
            total += item.item_price * item.item_quantity
        }

        val tax = total * 0.025
        val grandTotal = total + (2 * tax)

        totalAmountView.text = "Net Total: ₹$total"
        cgstView.text = "CGST (2.5%): ₹${String.format("%.2f", tax)}"
        sgstView.text = "SGST (2.5%): ₹${String.format("%.2f", tax)}"
        grandTotalView.text = "Grand Total: ₹${String.format("%.2f", grandTotal)}"
    }

    private fun makePayment() {
        val dataArray = JSONArray()
        var totalAmount = 0
        var totalItems = 0

        for (item in cartItems) {
            val obj = JSONObject()
            obj.put("cuisine_id", item.cuisine_id)
            obj.put("item_id", item.item_id)
            obj.put("item_price", item.item_price)
            obj.put("item_quantity", item.item_quantity)
            totalAmount += item.item_price * item.item_quantity
            totalItems += item.item_quantity
            dataArray.put(obj)
        }

        val body = JSONObject()
        body.put("total_amount", totalAmount.toString())
        body.put("total_items", totalItems)
        body.put("data", dataArray)

        val response = ApiClient.postRequest(
            endpoint = "/emulator/interview/make_payment",
            jsonBody = body.toString(),
            proxyAction = "make_payment"
        )

        if (response != null) {
            val json = JSONObject(response)
            val message = json.optString("response_message", "Success")
            val txn = json.optString("txn_ref_no", "-")
            Toast.makeText(this, "$message\nRef: $txn", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
        }
    }
}
