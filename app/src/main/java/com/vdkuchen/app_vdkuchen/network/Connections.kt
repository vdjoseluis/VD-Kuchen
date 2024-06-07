package com.vdkuchen.app_vdkuchen.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun openUrlInBrowser(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

fun openPhoneDialer(phoneNumber: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}

@SuppressLint("QueryPermissionsNeeded")
fun openGoogleMaps(fullAddress: String, context: Context) {
    val uri = "geo:0,0?q=${Uri.encode(fullAddress)}"
    println("URI: $uri")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Si Google Maps no está instalado, muestra un mensaje de error
        Toast.makeText(context, "Google Maps no está instalado en tu dispositivo", Toast.LENGTH_SHORT).show()
    }
}