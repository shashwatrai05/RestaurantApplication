import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {

    private const val BASE_URL = "https://uat.onebanc.ai"
    private const val API_KEY = "uonebancservceemultrS3cg8RaL30"
    private const val TAG = "API_CLIENT"

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

            // Log request info
            Log.d(TAG, "Request URL: $fullUrl")
            Log.d(TAG, "Headers: X-Partner-API-Key=$API_KEY, X-Forward-Proxy-Action=$proxyAction")
            Log.d(TAG, "Request Body: $jsonBody")

            // Write request body
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(jsonBody)
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            Log.d(TAG, "Response Code: $responseCode")

            val reader = if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream))
            } else {
                BufferedReader(InputStreamReader(connection.errorStream))
            }

            val responseText = reader.readText()
            Log.d(TAG, "Response Body: $responseText")

            reader.close()
            return responseText

        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            return null
        } finally {
            connection?.disconnect()
        }
    }
}
