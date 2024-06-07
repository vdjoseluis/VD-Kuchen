package com.vdkuchen.app_vdkuchen.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.vdkuchen.app_vdkuchen.R
import com.vdkuchen.app_vdkuchen.ui.components.ServiceCustomerInfo
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesActivity(
    viewModel: UserViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val confirmedErrorMessage by remember { mutableStateOf<String?>(null) }
    val pendingErrorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.loadServices()
    }
    val confirmedServices by viewModel.confirmedServices
    val pendingServices by viewModel.pendingServices

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.userName.value.let { installerName ->
                            Text(text = "Instalador: $installerName", style = MaterialTheme.typography.titleMedium)
                        }
                        IconButton(onClick = {
                            viewModel.userId.value=""
                            navController.navigate("login")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(BottomAppBarHeight)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Copyright ® 2024 - VDKuchen",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    IconButton(onClick = {
                        viewModel.onLoadServices()
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refrescar servicios")
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            color = Color.LightGray,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(id = R.drawable.logo1),
                contentDescription = "Logo", modifier = Modifier.alpha(0.1f))
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Servicios Confirmados",
                            Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                confirmedServices.let { services ->
                    if (services.isNotEmpty()) {
                        item {
                            ListServicesContent("Confirmado", services, navController, viewModel)
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    } else {
                        item {
                            Text(
                                text = "No tienes servicios",
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
                confirmedErrorMessage?.let { message ->
                    item {
                        Text(
                            text = message,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Servicios Por Confirmar",
                            Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                pendingServices.let { services ->
                    if (services.isNotEmpty()) {
                        item {
                            ListServicesContent("Por Confirmar", services, navController, viewModel)
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    } else {
                        item {
                            Text(
                                text = "No tienes servicios",
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
                pendingErrorMessage?.let { message ->
                    item {
                        Text(
                            text = message,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

private val BottomAppBarHeight = 40.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListServicesContent(
    status: String,
    services: List<DocumentSnapshot>,
    navController: NavController,
    viewModel: UserViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val customerInfoList = remember { mutableStateListOf<ServiceCustomerInfo>() }
    LaunchedEffect(viewModel, services) {
        services.forEach { service ->
            val customerRef = service.getDocumentReference("ref_customer")?.path?.substringAfterLast("/")
            customerRef?.let { customerId ->
                try {
                    val customerSnapshot = db.collection("customers").document(customerId).get().await()
                    val firstName = customerSnapshot.getString("firstname")
                    val lastName = customerSnapshot.getString("lastname")
                    val fullName = "$firstName $lastName"
                    val city = customerSnapshot.getString("city").toString()
                    customerInfoList.add(
                        ServiceCustomerInfo(
                            serviceId = service.id,
                            customerId = customerId,
                            fullName = fullName,
                            customerCity = city
                        )
                    )
                } catch (e: Exception) {
                    // Manejar errores si es necesario
                    Log.e("MainScreenContent", "Error fetching customer data: ${e.message}")
                }
            }
        }
    }

    Column {
        services.forEach { service ->
            val customerInfo = customerInfoList.find { it.serviceId == service.id }
            val dateDocument = service.getTimestamp("date")
            lateinit var formattedDate : String
            lateinit var formattedTime : String

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Icono servicio",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                TableTitle(service.getString("type") + ":")
                if (dateDocument != null) {
                    val dateService = dateDocument.toDate()
                    formattedDate = formatDate(dateService)
                    formattedTime = formatTime(dateService)
                    TableTitle(formattedDate)
                    TableTitle(formattedTime)
                }
            }
            LazyRow(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clickable {
                        customerInfo?.let { info ->
                            viewModel.selectedCustomerId.value = info.customerId
                            viewModel.selectedServiceId.value = info.serviceId
                            navController.navigate("clientData")
                            viewModel.clickedStatus.value = status
                            viewModel.dateService.value = formattedDate
                            viewModel.timeService.value = formattedTime
                        }
                    }
            ) {
                customerInfo?.let { info ->
                    item { TableCell(info.fullName) }
                    item { TableCell("(${info.customerCity})") }
                }
            }
        }
    }
}

@Composable
fun TableTitle(text: String) {
    Text(text, Modifier.padding(12.dp), color = MaterialTheme.colorScheme.primary)
}

@Composable
fun TableCell(text: String) {
    Text(text, Modifier.padding(12.dp))
}

fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun formatTime(time: Date): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(time)
}