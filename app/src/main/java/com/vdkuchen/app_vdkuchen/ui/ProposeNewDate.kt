package com.vdkuchen.app_vdkuchen.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vdkuchen.app_vdkuchen.R
import com.vdkuchen.app_vdkuchen.network.FirestoreRepo
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposedNewDate(
    viewModel: UserViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = { TopAppBar(title = {

            Text("Proponer Nueva Cita")
        }) },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(BottomAppBarHeight)
            ) {
                Text(
                    text = "Copyright ® 2024 - VDKuchen",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        },
        content = { MyContent(viewModel, navController) }
    )
}
private val BottomAppBarHeight = 40.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyContent(viewModel: UserViewModel, navController: NavController){
    val serviceId = viewModel.selectedServiceId.value
    val userId = viewModel.userId.value
    val newStatus = viewModel.newStatus.value
    val dateService = viewModel.dateService.value
    val timeService = viewModel.timeService.value

    val context = LocalContext.current
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDate = remember { mutableStateOf(dateService) }
    val mTime = remember { mutableStateOf(timeService) }
    val selectedDateTime = remember { mutableStateOf(LocalDateTime.now()) }

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            updateDateTime(mDate.value, mTime.value, selectedDateTime)
        }, mYear, mMonth, mDay+1
    )
    val mTimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = String.format("%d:%02d", mHour, mMinute)
            updateDateTime(mDate.value, mTime.value, selectedDateTime)
        }, 8, 30, true
    )
    LaunchedEffect(Unit) {
        mDatePickerDialog.show()
    }

    Image(painter = painterResource(id = R.drawable.logo1),
        contentDescription = "Logo", modifier = Modifier.alpha(0.2f))
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button(
            onClick = {
                mDatePickerDialog.show()
            }, modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Calendario",
                style = MaterialTheme.typography.bodyLarge,
            )
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Abrir calendario",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Fecha seleccionada: ${mDate.value}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.size(30.dp))

        OutlinedButton(
            onClick = {
                mTimePickerDialog.show()
            }, modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Horario",
                style = MaterialTheme.typography.bodyLarge,
            )
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Abrir horario",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Hora seleccionada: ${mTime.value}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.size(30.dp))

        HorizontalDivider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp))
        Spacer(modifier = Modifier.size(30.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                if (mDate.value != dateService || mTime.value != timeService) {
                    FirestoreRepo.proposeNewDate(serviceId, selectedDateTime.value)
                    FirestoreRepo.updateStatus(serviceId, newStatus)
                    FirestoreRepo.eventLog(userId, serviceId, newStatus, viewModel)
                    Toast.makeText(context, "Propuesta Nueva Cita", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se ha realizado ningún cambio", Toast.LENGTH_SHORT).show()
                }
                navController.navigate("main")
            }) {
                Text(text = "Confirmar")
            }

            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text(text = "Cancelar")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun updateDateTime(date: String, time: String, selectedDateTime: MutableState<LocalDateTime>) {
    val (hour, minute) = time.split(":").map { it.toInt() }
    val (day, month, year) = date.split("/").map { it.toInt() }
    selectedDateTime.value = LocalDateTime.of(year, month, day, hour, minute)
}