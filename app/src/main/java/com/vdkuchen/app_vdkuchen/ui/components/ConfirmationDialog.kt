package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    text: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onCancel() },
            title = {
                Text(
                    text = text,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Text("¿Estás seguro?")
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm() }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onCancel() }
                ) {
                    Text("No")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        text = "Modificar estado",
        showDialog = true,
        onConfirm = {
            println("Boton OK se va a ApiClient")
        },
        onCancel = {
            println("Botón de Cancelar")
        }
    )
}

