package com.example.restaurantapplication.screens.cart

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.models.CartItem
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalAmountView: TextView
    private lateinit var cgstView: TextView
    private lateinit var sgstView: TextView
    private lateinit var grandTotalView: TextView
    private lateinit var placeOrderBtn: Button

    // Empty cart layout (Image + Text)
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var emptyCartImage: ImageView
    private lateinit var emptyCartText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Bind views
        cartRecyclerView = findViewById(R.id.rv_cart_items)
        totalAmountView = findViewById(R.id.tv_total_amount)
        cgstView = findViewById(R.id.tv_cgst)
        sgstView = findViewById(R.id.tv_sgst)
        grandTotalView = findViewById(R.id.tv_grand_total)
        placeOrderBtn = findViewById(R.id.btn_place_order)

        emptyCartLayout = findViewById(R.id.empty_cart_layout)
        emptyCartImage = findViewById(R.id.iv_empty_cart)
        emptyCartText = findViewById(R.id.tv_empty_cart)

        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        populateCart()

        placeOrderBtn.setOnClickListener {
            makePayment()
        }
    }

    private fun populateCart() {
        val cartItems = CartManager.getCartItems()

        if (cartItems.isEmpty()) {
            // Show empty view
            cartRecyclerView.visibility = View.GONE
            totalAmountView.visibility = View.GONE
            cgstView.visibility = View.GONE
            sgstView.visibility = View.GONE
            grandTotalView.visibility = View.GONE
            placeOrderBtn.visibility = View.GONE
            emptyCartLayout.visibility = View.VISIBLE
            return
        }

        // Show cart and hide empty view
        cartRecyclerView.visibility = View.VISIBLE
        totalAmountView.visibility = View.VISIBLE
        cgstView.visibility = View.VISIBLE
        sgstView.visibility = View.VISIBLE
        grandTotalView.visibility = View.VISIBLE
        placeOrderBtn.visibility = View.VISIBLE
        emptyCartLayout.visibility = View.GONE

        cartRecyclerView.adapter = CartAdapter(cartItems)

        var total = 0
        for (item in cartItems) {
            total += item.item_price * item.item_quantity
        }

        val tax = total * 0.025
        val grandTotal = total + (2 * tax)

        totalAmountView.text = "Net Total: ₹$total"
        cgstView.text = "CGST (2.5%): ₹${"%.2f".format(tax)}"
        sgstView.text = "SGST (2.5%): ₹${"%.2f".format(tax)}"
        grandTotalView.text = "Grand Total: ₹${"%.2f".format(grandTotal)}"
    }

    private fun makePayment() {
        val cartItems = CartManager.getCartItems()

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val dataArray = JSONArray()
        var totalAmount = 0
        var totalItems = 0

        for (item in cartItems) {
            val obj = JSONObject().apply {
                put("cuisine_id", item.cuisine_id)
                put("item_id", item.item_id)
                put("item_price", item.item_price)
                put("item_quantity", item.item_quantity)
            }
            dataArray.put(obj)
            totalAmount += item.item_price * item.item_quantity
            totalItems += item.item_quantity
        }

        val body = JSONObject().apply {
            put("total_amount", totalAmount.toString())
            put("total_items", totalItems)
            put("data", dataArray)
        }

        Log.d("PAYMENT_REQUEST_BODY", body.toString(2))

        val response = ApiClient.postRequest(
            endpoint = "/emulator/interview/make_payment",
            jsonBody = body.toString(),
            proxyAction = "make_payment",
        )

        if (response != null) {
            Log.d("PAYMENT_RESPONSE", response)
            val json = JSONObject(response)
            val message = json.optString("response_message", "Success")
            val txn = json.optString("txn_ref_no", "-")
            Toast.makeText(this, "$message\nRef: $txn", Toast.LENGTH_LONG).show()
            CartManager.clearCart()
            populateCart()
        } else {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
        }
    }
}
