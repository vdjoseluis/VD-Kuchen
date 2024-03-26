package com.vdkuchen.app_vdkuchen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdkuchen.app_vdkuchen.ui.LoginScreen
import com.vdkuchen.app_vdkuchen.ui.MainScreen
import com.vdkuchen.app_vdkuchen.ui.UserViewModel
import com.vdkuchen.app_vdkuchen.ui.theme.VDKuchenTheme

class MainActivity : ComponentActivity() {
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
                    //LoginScreen(navController= navController)
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController = navController, viewModel= viewModel)
                        }
                        composable("main") {
                            MainScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

