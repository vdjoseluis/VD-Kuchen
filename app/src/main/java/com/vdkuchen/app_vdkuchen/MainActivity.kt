package com.vdkuchen.app_vdkuchen

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdkuchen.app_vdkuchen.ui.AuthActivity
import com.vdkuchen.app_vdkuchen.ui.ClientDataActivity
import com.vdkuchen.app_vdkuchen.ui.ProposedNewDate
import com.vdkuchen.app_vdkuchen.ui.ServicesActivity
import com.vdkuchen.app_vdkuchen.ui.UserViewModel
import com.vdkuchen.app_vdkuchen.ui.theme.VDKuchenTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setContent {
            val navController= rememberNavController()
            VDKuchenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "splash_screen") {
                        composable("splash_screen") {
                            SplashScreen(navController = navController)
                        }
                        composable("login") {
                            AuthActivity(navController = navController, viewModel = viewModel)
                        }
                        composable("main") {
                            //MainScreen(navController = navController, viewModel= viewModel)
                            ServicesActivity(navController = navController, viewModel= viewModel)
                        }
                        composable("clientData") {
                            ClientDataActivity(context = LocalContext.current, navController = navController, viewModel= viewModel)
                        }
                        composable("newDate") {
                            ProposedNewDate(viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

