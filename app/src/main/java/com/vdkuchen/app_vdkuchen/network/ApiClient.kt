package com.vdkuchen.app_vdkuchen.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vdkuchen.app_vdkuchen.ui.UserViewModel
import org.json.JSONException
import org.json.JSONObject

object ApiClient {
    private const val BASE_URL = "http://192.168.1.94/vdkuchen/api.php"
    private lateinit var requestQueue: RequestQueue

    fun initialize(context: Context) {
        requestQueue = Volley.newRequestQueue(context.applicationContext)
    }

    fun login(
        username: String,
        password: String,
        listener: (Boolean, Int?, String?) -> Unit,
        errorListener: (String) -> Unit
    ) {
        val url = "$BASE_URL?username=$username&password=$password"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.has("success")) {
                    val success = response.getBoolean("success")
                    val userId = if (response.has("userId")) response.getInt("userId") else null
                    val installerName = if (response.has("name")) response.getString("name") else null
                    listener.invoke(success, userId, installerName)
                } else {
                    listener.invoke(false, null, null)
                }
            },
            { error ->
                errorListener.invoke(error.message ?: "Error desconocido")
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

    fun getServicesWithCustomers(
        userId: Int,
        successListener: (List<JSONObject>) -> Unit,
        errorListener: (String) -> Unit
    ) {
        val url = "$BASE_URL?get_services_with_customers&userId=$userId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val servicesArray = response.optJSONArray("services")
                val servicesList = mutableListOf<JSONObject>()

                servicesArray?.let {
                    for (i in 0 until it.length()) {
                        servicesList.add(it.getJSONObject(i))
                    }
                }

                successListener.invoke(servicesList) // Llama al listener con la lista de servicios
            },
            { error ->
                errorListener.invoke(error.message ?: "Error desconocido")
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}
