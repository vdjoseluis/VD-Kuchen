package com.vdkuchen.app_vdkuchen.ui

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.vdkuchen.app_vdkuchen.R
import com.vdkuchen.app_vdkuchen.network.FirestoreRepo
import com.vdkuchen.app_vdkuchen.network.openGoogleMaps
import com.vdkuchen.app_vdkuchen.network.openPhoneDialer
import com.vdkuchen.app_vdkuchen.network.openUrlInBrowser
import com.vdkuchen.app_vdkuchen.ui.components.ChangeStatusDialog
import com.vdkuchen.app_vdkuchen.ui.components.ConfirmationDialog
import com.vdkuchen.app_vdkuchen.ui.components.GetCustomContents
import com.vdkuchen.app_vdkuchen.ui.components.IncidentDialog
import kotlinx.coroutines.tasks.await
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDataActivity(
    context: Context,
    viewModel: UserViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var customerDetails by remember { mutableStateOf<Map<String, Any>?>(null) }
    var serviceDetails by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val db = FirebaseFirestore.getInstance()
    val fullnameState = remember { mutableStateOf("") }

    // Obtener datos seleccionados del ViewModel
    val customerId = viewModel.selectedCustomerId.value
    val serviceId = viewModel.selectedServiceId.value
    val userId = viewModel.userId.value
    val userName = viewModel.userName.value
    val newStatus = viewModel.newStatus.value
    val dateService = viewModel.dateService.value
    val timeService = viewModel.timeService.value

    val photoPicker = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { uris ->
            uris.forEach { uri ->
                val file = getTempFileFromUri(uri, context)
                val modifiedFileName = "User_${userName}/Service_${serviceId}/${file.name}"
                val storageRef = Firebase.storage.reference.child("uploads/$modifiedFileName")
                val uploadTask = storageRef.putFile(uri)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(context, "Archivos subidos correctamente", Toast.LENGTH_SHORT).show()
                    taskSnapshot.metadata?.reference?.downloadUrl
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al subir archivos: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    var showStatusDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showIncidentDialog by remember { mutableStateOf(false) }

    // Obtener detalles del cliente del servidor
    LaunchedEffect(customerId) {
        try {
            val customerSnapshot = db.collection("customers").document(customerId).get().await()
            customerDetails = customerSnapshot.data
            val firstName = customerSnapshot.getString("firstname") ?: ""
            val lastName = customerSnapshot.getString("lastname") ?: ""
            fullnameState.value = "$firstName $lastName"
            val serviceInfo = db.collection("services").document(serviceId).get().await()
            serviceDetails = serviceInfo.data
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun toggleDialog() { showStatusDialog = !showStatusDialog }
    fun toggleConfirmationDialog() { showConfirmationDialog = !showConfirmationDialog }
    fun toggleIncidentDialog() { showIncidentDialog = !showIncidentDialog }

    val textOption1 = when (viewModel.clickedStatus.value) {
        "Confirmado" -> {
            "Servicio Realizado"
        }
        "Por Confirmar" -> {
            "Confirmar Cita Servicio"
        } else -> {""}
    }

    val textOption2 = when (viewModel.clickedStatus.value) {
        "Confirmado" -> {
            "Reportar incidencia"
        }
        "Por Confirmar" -> {
            "Proponer Nueva Cita"
        } else -> {""}
    }

    if (showStatusDialog) {
        ChangeStatusDialog(
            textOption1 = textOption1,
            textOption2 = textOption2,
            showDialog = true,
            onOptionSelected = { option ->
                when (option) {
                    "Servicio Realizado" -> {
                        viewModel.newStatus.value = "Finalizado"
                        toggleDialog()
                        toggleConfirmationDialog()
                    }
                    "Reportar incidencia" -> {
                        viewModel.newStatus.value = "Incidencia"
                        toggleDialog()
                        toggleIncidentDialog()
                    }
                    "Confirmar Cita Servicio" -> {
                        viewModel.newStatus.value = "Confirmado"
                        toggleDialog()
                        toggleConfirmationDialog()
                    }
                    "Proponer Nueva Cita" -> {
                        viewModel.newStatus.value = "Propuesta Nueva Cita"
                        navController.navigate("newDate")
                    }
                }
            },
            onBackClicked = { toggleDialog() }
        )
    }
    if (showConfirmationDialog) {   //jlvasquez80@gmail.com
        ConfirmationDialog(
            text = "Modificar estado",
            showDialog = true,
            onConfirm = {
                FirestoreRepo.updateStatus(serviceId, newStatus)
                FirestoreRepo.eventLog(userId, serviceId, newStatus, viewModel)
                Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                viewModel.onLoadServices()
                navController.popBackStack()
            },
            onCancel = { toggleConfirmationDialog() }
        )
    }
    if (showIncidentDialog) {
        IncidentDialog(
            showDialog = true,
            onConfirm = { description ->
                FirestoreRepo.addIncident(userId, customerId, description)
                FirestoreRepo.updateStatus(serviceId, newStatus)
                FirestoreRepo.eventLog(userId, serviceId, newStatus, viewModel)
                Toast.makeText(context, "Incidencia registrada", Toast.LENGTH_SHORT).show()
                viewModel.onLoadServices()
                navController.popBackStack()
            },
            onCancel = { toggleIncidentDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = CenterVertically
                    ) {
                        customerDetails?.let {
                            db.collection("customers").document(customerId).get().addOnSuccessListener {
                                val fullname = "${it.get("firstname") as String} ${it.get("lastname") as String}"
                                fullnameState.value = fullname
                            }
                            Text(
                                text = fullnameState.value,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(onClick = { toggleDialog() }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Opciones")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
            color = Color.LightGray,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(id = R.drawable.logo1),
                contentDescription = "Logo", modifier = Modifier.alpha(0.1f))
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                customerDetails?.let { details ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            val address = (details["address"] as? String ?: "") + ", " + (details["addInfoAddress"] as? String ?: "")
                            val zipCode = details["zip_code"] as? String ?: ""
                            val city = details["city"] as? String ?: ""
                            val phone = details["phone"] as? String ?: ""
                            val email = details["email"] as? String ?: ""
                            val fullAddress = "$address, $zipCode, $city"

                            RowDetail(title = "Fecha:", fullAddress, "$dateService  >>>  $timeService",null,null, context)
                            RowDetail(title = "Dirección:", fullAddress, address,null,null, context)
                            RowDetail(title = "C. Postal:", fullAddress, zipCode,true,null, context)
                            RowDetail(title = "Población:", fullAddress, city,null,null, context)
                            RowDetail(title = "Email:", fullAddress, email,null,null, context)
                            RowDetail(title = "Teléfono:", fullAddress, phone,null,true, context)
                            Spacer(modifier = Modifier.height(20.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Descripción:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = serviceDetails?.get("description") as? String?:"",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Justify,
                                        maxLines = Int.MAX_VALUE,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            val serviceResourceRef = serviceDetails?.get ("resource") as? String?:""
                            Row (verticalAlignment = CenterVertically,
                                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                OutlinedButton(
                                    onClick = {
                                        if (serviceResourceRef!= "")
                                            openUrlInBrowser(serviceResourceRef, context)
                                        else
                                            Toast.makeText(context, "No tiene ningún recurso", Toast.LENGTH_SHORT).show()
                                    }, modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(text = "Abrir recursos")
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.List,
                                        contentDescription = "Adjuntar archivo",
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                                FileChooserButton {
                                    photoPicker.launch("*/*")
                                }
                            }
                        }
                    }
                }

                errorMessage?.let { message ->
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

@Composable
fun RowDetail (title: String, fullAddress: String, content: String, location: Boolean?, call: Boolean?, context: Context) {
    Row(
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(min= 80.dp))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                text = content,
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                maxLines = Int.MAX_VALUE,
            )
        }
        if (location==true) {
            Surface(
                shape = CircleShape,
                color = Color.Blue,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            ) {
                IconButton(onClick = {
                    openGoogleMaps(fullAddress, context)
                }) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Ir a", tint = Color.White)
                }
            }
        } else if (call==true) {
            Surface(
                shape = CircleShape,
                color = Color.Green,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            ){
                IconButton(onClick = {
                    openPhoneDialer(content, context)
                }) {
                    Icon(Icons.Filled.Call, contentDescription = "Llamar", tint = Color.White)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
fun FileChooserButton( photoPicker: () -> Unit) {
    Button(onClick = { photoPicker() }, modifier = Modifier.padding(8.dp)) {
        Row {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Adjuntar archivo",
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = "Adjuntar")
        }
    }
}

private fun getTempFileFromUri(uri: Uri, context: Context): File {
    val resultFile : File = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extensionFile = mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))
        val file = File.createTempFile("File_", ".$extensionFile", context.cacheDir)
        val input = cr.openInputStream(uri)
        file.outputStream().use { stream ->
            input?.copyTo(stream)
        }
        input?.close()
        file
    }else {
        File(uri.path!!)
    }
    return resultFile
}