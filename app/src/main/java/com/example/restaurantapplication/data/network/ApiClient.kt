package com.example.restaurantapplication.data.network
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {

    private const val BASE_URL = "https://uat.onebanc.ai"

    private const val API_KEY = "uonebancservceemultrS3cg8RaL30"

    fun postRequest(endpoint: String, jsonBody: String, proxyAction: String): String? {
        val fullUrl = "$BASE_URL$endpoint"
        var connection: HttpURLConnection? = null

        try {
            val url = URL(fullUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-Partner-API-Key", API_KEY)
            connection.setRequestProperty("X-Forward-Proxy-Action", proxyAction)
            connection.doInput = true
            connection.doOutput = true

            // Write request body
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(jsonBody)
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    reader.readText()
                }
            } else {
                BufferedReader(InputStreamReader(connection.errorStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            connection?.disconnect()
        }
    }
}
