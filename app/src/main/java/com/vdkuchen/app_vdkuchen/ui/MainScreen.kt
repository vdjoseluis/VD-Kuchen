package com.vdkuchen.app_vdkuchen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vdkuchen.app_vdkuchen.network.ApiClient
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: UserViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var services by remember { mutableStateOf<List<JSONObject>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null)  }

    LaunchedEffect(key1 = Unit) {
        try {
            val userId= viewModel.userId.value?: -1
            if (userId!= -1) {
                ApiClient.getServicesWithCustomers(
                    userId= userId,
                    successListener = { servicesList ->
                        services = servicesList
                    },
                    errorListener = { error ->
                        errorMessage= error
                    }
                )
            } else { errorMessage= "No se pudo obtener Id del usuario" }
        } catch (e: Exception) {
            errorMessage= e.message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Agenda de Servicios")
                        viewModel.userName.value?.let { installerName ->
                            Text(text = "Instalador: $installerName")
                        }                    }
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
                // Muestra la tabla de servicios si se han obtenido
                services?.let { services ->
                    if (services.isNotEmpty()) {
                        MainScreenContent(services, navController, viewModel)
                    } else {
                        Text(
                            text= "No tienes servicios",
                            modifier= Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        modifier= Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}
private val BottomAppBarHeight= 40.dp

@Composable
fun MainScreenContent(services: List<JSONObject>, navController: NavController, viewModel: UserViewModel) {
    LazyColumn {
        // Registros de la tabla
        items(services) { service ->
            val customerId = service.getInt("id_customer")

            Surface(
                color = MaterialTheme.colorScheme.secondary, // Color de fondo diferente
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    TableCell(formatDate(service.getString("date")))
                    TableCell(formatTime(service.getString("time")))
                }
            }
            Row (
                modifier = Modifier.clickable {
                    viewModel.selectedCustomerId.value = customerId
                    navController.navigate("clientData")
                }
            ) {
                TableCell(formatFullName(service.getString("customer_name"), service.getString("customer_surname")))
                TableCell(service.getString("customer_city"))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TableCell(text: String) {
    Text(text, Modifier.padding(12.dp))
}

fun formatDate(date: String): String {
    val parts = date.split("-")
    val day = parts[2]
    val month = parts[1]
    val year = parts[0]
    return "$day-$month-$year"
}

fun formatTime(time: String): String {
    val parts = time.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    return "%02d:%02d".format(hour, minute)
}

fun formatFullName(firstName: String, lastName: String): String {
    return "$firstName $lastName"
}

