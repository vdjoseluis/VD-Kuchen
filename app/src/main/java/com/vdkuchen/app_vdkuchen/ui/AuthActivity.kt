package com.vdkuchen.app_vdkuchen.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vdkuchen.app_vdkuchen.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthActivity (
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("")   }
    var password by remember { mutableStateOf("") }
    val focusRequester= remember { FocusRequester()  }
    val context= LocalContext.current
    val db = FirebaseFirestore.getInstance()

    if (viewModel.userId.value=="") {
        Image(painter = painterResource(id = R.drawable.logo1),
            contentDescription = "Logo", modifier = Modifier.alpha(0.2f))
        Column(
            modifier= Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "VD KUCHEN",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email= it },
                label = { Text(text = "Email") },
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
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                                db.collection("users").document(userId).get().addOnSuccessListener {user ->
                                    //viewModel.userName.value = user.get("firstname") as String
                                    viewModel.userName.value = user.get("firstname") as String + " " + user.get("lastname") as String
                                }
                                Toast.makeText(context, "Bienvenid@", Toast.LENGTH_SHORT).show()
                                navController.navigate("main")
                                viewModel.userId.value = userId
                            } else {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier= Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else { navController.navigate("main") }
}
