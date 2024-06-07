package com.vdkuchen.app_vdkuchen.network

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.vdkuchen.app_vdkuchen.ui.UserViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object FirestoreRepo {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun addIncident(userId: String, customerId: String, description: String) {
        val currentDate = Timestamp.now()
        val userRef = db.collection("users").document(userId)
        val customerRef = db.collection("customers").document(customerId)
        val data = hashMapOf(
            "userId" to userRef,
            "customerId" to customerRef,
            "description" to description,
            "date" to currentDate,
            "status" to "Pendiente")
        val incidentId = db.collection("incidents").document().id
        try {
            db.collection("incidents").document(incidentId)
                .set(data).addOnSuccessListener {
                    println("Incidencia registrada correctamente")
                }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error adding document", e)
        }
    }

    fun updateStatus (serviceId: String, newStatus: String) {
        val data = hashMapOf<String, Any>("status" to newStatus)
        try {
            db.collection("services").document(serviceId)
                .update(data).addOnSuccessListener {
                    println("Estado actualizado correctamente")
                }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating document", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun proposeNewDate (serviceId: String, proposedDateTime: LocalDateTime) {
        val instant = proposedDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val newDateTime = Timestamp(Date.from(instant))
        val data = hashMapOf<String, Any>("date" to newDateTime)
        try {
            db.collection("services").document(serviceId)
                .update(data).addOnSuccessListener {
                    println("Propuesta nueva fecha: $newDateTime")
                }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating document", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun eventLog(userId: String, serviceId: String, action: String, viewModel: UserViewModel) {
        val currentDate = Timestamp.now()
        val userRef = db.collection("users").document(userId)
        val userName = viewModel.userName.value
        val serviceRef = db.collection("services").document(serviceId)
        val data = hashMapOf(
            "userId" to userRef,
            "serviceId" to serviceRef,
            //"description" to "User: $userName ($userId) - $action - Service: $serviceId",
            "action_performed" to action,
            "date" to currentDate)
        val eventId = db.collection("events_log").document().id
        try {
            db.collection("events_log").document(eventId)
                .set(data).addOnSuccessListener {
                    println("Evento registrado correctamente")
                }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error adding document", e)
        }
    }
}