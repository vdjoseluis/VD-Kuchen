package com.vdkuchen.app_vdkuchen.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vdkuchen.app_vdkuchen.network.ApiClient

@Composable
fun LoginScreen (
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("")   }
    var password by remember { mutableStateOf("") }
    val focusRequester= remember { FocusRequester()  }
    val context= LocalContext.current

    ApiClient.initialize(context)

    Column(
        modifier= Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "VD KUCHEN",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username= it },
            label = { Text(text = "Usuario") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                autoCorrect = false
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val errorMessage= "No se ha podido iniciar sesión, intenta nuevamente."
                ApiClient.login(
                    username = username,
                    password= password,
                    listener = { success, userId, installerName ->
                        if (success && userId != null && installerName != null) {
                            Toast.makeText(context, "Bienvenid@ $username", Toast.LENGTH_LONG).show()
                            viewModel.userId.value = userId
                            viewModel.userName.value = installerName // Almacena el nombre del instalador en el ViewModel
                            navController.navigate("main")
                        } else {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    },
                    errorListener = { errorMessage ->
                        // Manejar el error de la solicitud
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier= Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
