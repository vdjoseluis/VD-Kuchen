package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun IncidentDialog(
    showDialog: Boolean,
    onConfirm: (description: String) -> Unit,
    onCancel: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onCancel() },
            title = {
                Text(
                    text = "Crear nueva incidencia",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    Text("Descripción:")
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(FocusRequester.Default),
                        maxLines = Int.MAX_VALUE,
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(description) }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onCancel() }
                ) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}

@Preview
@Composable
fun IncidentDialogPreview(){
    IncidentDialog(
        showDialog = true,
        onConfirm = {
            println("Boton OK se va a ApiClient")
        },
        onCancel = {
            println("Botón de Cancelar")
        }
    )
}
