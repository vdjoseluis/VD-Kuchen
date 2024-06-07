package com.vdkuchen.app_vdkuchen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ChangeStatusDialog(
    textOption1: String, textOption2: String,
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
                    OptionListItem(textOption1, Icons.Default.PlayArrow, onOptionSelected)
                    OptionListItem(textOption2, Icons.Default.PlayArrow, onOptionSelected)
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
        textOption1 = "Servicio Realizado",
        textOption2 = "Abrir Incidencia",
        showDialog = true,
        onOptionSelected = {option ->
            println("Opción seleccionada: $option")
        },
        onBackClicked = {
            println("Botón de Volver")
        }
    )
}
