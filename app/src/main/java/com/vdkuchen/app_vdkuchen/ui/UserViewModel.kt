package com.vdkuchen.app_vdkuchen.ui

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
class UserViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val userId = mutableStateOf("")
    val userName = mutableStateOf("")
    val selectedCustomerId = mutableStateOf("")
    val selectedServiceId = mutableStateOf("")
    val newStatus = mutableStateOf("")
    val clickedStatus = mutableStateOf("")
    val dateService = mutableStateOf("")
    val timeService = mutableStateOf("")

    // Listas de servicios
    val confirmedServices = mutableStateOf<List<DocumentSnapshot>>(emptyList())
    val pendingServices = mutableStateOf<List<DocumentSnapshot>>(emptyList())

    // Método para cargar los servicios desde Firestore
    suspend fun loadServices() {
        try {
            //val userId = userId.value
            val userRef = db.collection("users").document(userId.value)
            val confirmedServicesQuery = FirebaseFirestore.getInstance().collection("services")
                .whereEqualTo("status", "Confirmado")
                .whereEqualTo("ref_installer", userRef)

            val pendingServicesQuery = db.collection("services")
                .whereEqualTo("status", "Por Confirmar")
                .whereEqualTo("ref_installer", userRef)

            // Ejecutar ambas consultas de forma asíncrona
            val confirmedServicesTask = confirmedServicesQuery.get()
            val pendingServicesTask = pendingServicesQuery.get()

            // Esperar a que ambas consultas se completen
            val confirmedServices = confirmedServicesTask.await().documents
            val pendingServices = pendingServicesTask.await().documents

            // Fusionar los resultados en una sola lista
            val allServices = confirmedServices + pendingServices

            // Actualizar el estado del ViewModel con la lista combinada
            setConfirmedServices(confirmedServices)
            setPendingServices(pendingServices)
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar los servicios: ${e.message}", e)
        }
    }

    fun onLoadServices() {
        viewModelScope.launch { loadServices() }
    }

    // Función para actualizar la lista de servicios confirmados
    private fun setConfirmedServices(services: List<DocumentSnapshot>) {
        confirmedServices.value = services
    }

    // Función para actualizar la lista de servicios por confirmar
    private fun setPendingServices(services: List<DocumentSnapshot>) {
        pendingServices.value = services
    }
}
