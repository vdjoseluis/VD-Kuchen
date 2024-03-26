package com.vdkuchen.app_vdkuchen.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ClientDataScreen(viewModel: UserViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Aquí puedes colocar los componentes de tu pantalla ClientDataScreen
            // Por ejemplo:
            viewModel.selectedCustomerId.value?.let { customerId ->
                Text(text = "Cliente: $customerId")
            }
        }
    }
}
