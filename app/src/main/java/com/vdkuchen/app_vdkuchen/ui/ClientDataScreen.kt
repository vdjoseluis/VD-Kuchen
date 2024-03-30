package com.vdkuchen.app_vdkuchen.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vdkuchen.app_vdkuchen.network.ApiClient
import com.vdkuchen.app_vdkuchen.ui.components.ChangeStatusDialog
import com.vdkuchen.app_vdkuchen.ui.components.ConfirmationDialog
import com.vdkuchen.app_vdkuchen.ui.components.IncidentDialog
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDataScreen(
    context: Context,
    viewModel: UserViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var customerDetails by remember { mutableStateOf<JSONObject?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showIncidentDialog by remember { mutableStateOf(false) }

    // Obtener datos seleccionados del ViewModel
    val customerId = viewModel.selectedCustomerId.value ?: return
    val serviceId = viewModel.selectedServiceId.value ?: return
    val userId = viewModel.userId.value

    // Obtener detalles del cliente del servidor
    LaunchedEffect(customerId) {
        try {
            ApiClient.getCustomerDetails(
                customerId = customerId,
                serviceId = serviceId,
                successListener = { customerDetailsResponse ->
                    customerDetails = customerDetailsResponse
                },
                errorListener = { error ->
                    errorMessage = error
                }
            )
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun toggleDialog() { showDialog = !showDialog }
    fun toggleConfirmationDialog() { showConfirmationDialog = !showConfirmationDialog }
    fun toggleIncidentDialog() { showIncidentDialog = !showIncidentDialog }

    if (showDialog) {
        ChangeStatusDialog(
            showDialog = true,
            onOptionSelected = { option ->
                if (option=="Servicio Realizado") {
                    viewModel.newStatus.value = "Finalizado"
                    toggleDialog()
                    toggleConfirmationDialog()
                } else if (option=="Abrir incidencia") {
                    viewModel.newStatus.value = "Incidencia"
                    toggleDialog()
                    toggleIncidentDialog()
                }
            },
            onBackClicked = { toggleDialog() }
        )
    }
    if (showConfirmationDialog) {
        ConfirmationDialog(
            text = "Modificar estado",
            showDialog = true,
            onConfirm = { ApiClient.changeStatusService(userId,serviceId,viewModel.newStatus.value,
                successListener = { Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show() },
                errorListener = { Toast.makeText(context, "Error desconocido", Toast.LENGTH_SHORT).show() })
                toggleConfirmationDialog()
                navController.popBackStack() },
            onCancel = { toggleConfirmationDialog() }
        )
    }
    if (showIncidentDialog) {
        IncidentDialog(
            showDialog = true,
            onConfirm = { description ->
                ApiClient.createIncident(userId, customerId, description,
                successListener = { Toast.makeText(context, "Incidencia ha sido registrada", Toast.LENGTH_SHORT).show() },
                errorListener = { Toast.makeText(context, "Error desconocido", Toast.LENGTH_SHORT).show() })
                ApiClient.changeStatusService(userId,serviceId,viewModel.newStatus.value,
                    successListener = {  },
                    errorListener = {  })
                toggleIncidentDialog()
                navController.popBackStack() },
            onCancel = { toggleIncidentDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        customerDetails?.let {details ->
                            Text(
                                text = "${details.getString("name")} ${details.getString("surname")}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(onClick = { toggleDialog() }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Modificar estado")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(BottomAppBarHeight)
            ) {
                Text(
                    text = "Copyright ® 2024 - VDKuchen",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    ) { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Muestra los detalles del cliente si están disponibles
                customerDetails?.let { details ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                            //.padding(start = 15.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Dirección: ${details.getString("address")}",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = "CP: ${details.getString("zip_code")}",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = "Población: ${details.getString("city")}",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = "Teléfono: ${details.getString("phone")}",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Descripción:",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = details.getString("description") ?: "Sin comentarios",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Justify,
                                    maxLines = Int.MAX_VALUE,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

private val BottomAppBarHeight = 40.dp
