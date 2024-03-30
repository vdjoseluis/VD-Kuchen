package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ChangeStatusDialog(
    showDialog: Boolean,
    onOptionSelected: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {  },
            title = {
                Text(
                    text = "Modificar estado",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    OptionListItem("Servicio Realizado", Icons.Default.PlayArrow, onOptionSelected)
                    OptionListItem("Abrir incidencia", Icons.Default.PlayArrow, onOptionSelected)
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    OptionListItem("Volver", Icons.AutoMirrored.Filled.ArrowBack) {
                        onBackClicked()
                    }
                }
            },
            dismissButton = null,
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}

@Composable
fun OptionListItem(
    text: String,
    icon: ImageVector,
    onOptionSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            modifier = Modifier.clickable { onOptionSelected(text) }
        )
    }
}

@Preview
@Composable
fun PreviewCustomAlertDialog() {
    ChangeStatusDialog(
        showDialog = true,
        onOptionSelected = {option ->
            println("Opción seleccionada: $option")
        },
        onBackClicked = {
            println("Botón de Volver")
        }
    )
}
