package com.vdkuchen.app_vdkuchen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vdkuchen.app_vdkuchen.network.ApiClient
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDataScreen(
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    var customerDetails by remember { mutableStateOf<JSONObject?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Obtener customerId seleccionado del ViewModel
    val customerId = viewModel.selectedCustomerId.value ?: return

    // Obtener detalles del cliente del servidor
    LaunchedEffect(customerId) {
        try {
            ApiClient.getCustomerDetails(
                customerId = customerId,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    customerDetails?.let {details ->
                        Text(
                            text = "${details.getString("name")} ${details.getString("surname")}"
                        )
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
                            .padding(start = 15.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Dirección: ${details.getString("address")}")
                        Text(text = "CP: ${details.getString("zip_code")}")
                        Text(text = "Población: ${details.getString("city")}")
                        Text(text = "Teléfono: ${details.getString("phone")}")
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
