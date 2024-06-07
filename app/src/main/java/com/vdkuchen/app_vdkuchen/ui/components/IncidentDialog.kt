package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
                OutlinedButton(
                    onClick = { onCancel() }
                ) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}
