package com.vdkuchen.app_vdkuchen.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {
    val userId = mutableStateOf(-1)
    val userName = mutableStateOf("")
    val selectedCustomerId = mutableStateOf<Int?>(null)

    val selectedServiceId = mutableStateOf<Int?>(null)
    val newStatus = mutableStateOf("")
}