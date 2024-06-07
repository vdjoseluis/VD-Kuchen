package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
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
                OutlinedButton(
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

