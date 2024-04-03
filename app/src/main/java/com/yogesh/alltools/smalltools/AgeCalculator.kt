package com.yogesh.alltools.smalltools

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogesh.alltools.R
import com.yogesh.alltools.utils.Utils
import java.time.LocalDate
import java.time.Period


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgeCalculator() {
    MyAgeCalculator()
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MyAgeCalculator() {

    var birthDate by remember {
        mutableStateOf("Click to select Birth Date")
    }
    var birthDateInMillis by remember { mutableLongStateOf(0) }
    var currentDate by remember {
        mutableStateOf(Utils.getDate(System.currentTimeMillis()))
    }
    var currentDateInMillis by remember { mutableLongStateOf(0) }


    var openDialog by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("") }
    var showAge by remember { mutableStateOf(false) }
    var clickedButtonNumber by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {

        Column {

            Text(
                text = "Age Calculator", modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                fontSize = 30.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )

            ExtendedFloatingActionButton(
                onClick = {
                    openDialog = true
                    clickedButtonNumber = 1
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_birthday),
                        "Birthday Icon",
                        Modifier.size(23.dp),
                        tint = Color.Unspecified
                    )
                },
                text = { Text(text = birthDate, color = Color.White) },
                containerColor = Color.Red,
                modifier = Modifier
                    .padding(start = 50.dp, end = 50.dp, bottom = 30.dp, top = 50.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "To", modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )

            ExtendedFloatingActionButton(
                onClick = {
                    openDialog = true
                    clickedButtonNumber = 2
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_birthday),
                        "Birthday Icon",
                        Modifier.size(23.dp),
                        tint = Color.Unspecified
                    )
                },
                text = { Text(text = currentDate, color = Color.White) },
                containerColor = Color.Red,
                modifier = Modifier
                    .padding(horizontal = 50.dp, vertical = 30.dp)
                    .fillMaxWidth()
            )


            ExtendedFloatingActionButton(
                onClick = {
                    if(birthDate == "Click to select Birth Date") {
                        Toast.makeText(context, "Please select birth date", Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }
                    val dateMonthYear = birthDate.split("/")
                    val dateMonthYear1 = currentDate.split("/")
                    val period = Period.between(
                        LocalDate.of(
                            dateMonthYear[2].toInt(),
                            dateMonthYear[1].toInt(),
                            dateMonthYear[0].toInt()
                        ),
                        LocalDate.of(
                            dateMonthYear1[2].toInt(),
                            dateMonthYear1[1].toInt(),
                            dateMonthYear1[0].toInt()
                        )
                    )
                    age = "${period.years} Years ${period.months} Months ${period.days} Days"
                    showAge = true
                },
                icon = {},
                text = { Text(text = "Calculate", color = Color.White) },
                containerColor = Color.Blue,
                modifier = Modifier
                    .padding(horizontal = 100.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            if (showAge) {
                Text(
                    text = "Your Age is : $age", modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
                )
            }
        }

    }
    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier)
    if (openDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = 946665000000)
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        if (clickedButtonNumber == 1) {
                            birthDateInMillis = datePickerState.selectedDateMillis!!
                            birthDate = Utils.getDate(datePickerState.selectedDateMillis!!)
                        } else {
                            currentDateInMillis = datePickerState.selectedDateMillis!!
                            currentDate = Utils.getDate(datePickerState.selectedDateMillis!!)
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


}