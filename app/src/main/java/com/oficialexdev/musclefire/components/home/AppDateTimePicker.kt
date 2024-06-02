package com.oficialexdev.musclefire.components.home

import android.icu.util.Calendar
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oficialexdev.musclefire.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateTimePicker(
    dismiss: () -> Unit,
    define: (
        date: Long,
    ) -> Unit
) {
    var timeDefined: Boolean by remember { mutableStateOf(false) }
    val dateNow = LocalDateTime.now()
    val timerState = rememberTimePickerState(
        dateNow.hour,
        dateNow.minute,
        is24Hour = true
    )
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis,
        yearRange = IntRange(dateNow.year, dateNow.year + 10)
    )

    if (!timeDefined) {
        Dialog(onDismissRequest = { dismiss() }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                            Color.Black
                        else
                            colorResource(id = android.R.color.system_accent1_900)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                TimePicker(
                    timerState,
                    colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) TimePickerDefaults.colors(
                        timeSelectorUnselectedContentColor = colorResource(id = android.R.color.system_accent1_500),
                        timeSelectorUnselectedContainerColor = colorResource(id = android.R.color.system_accent1_800),
                        timeSelectorSelectedContentColor = colorResource(id = android.R.color.system_accent1_200),
                        timeSelectorSelectedContainerColor = colorResource(id = android.R.color.system_accent1_600),
                        selectorColor = colorResource(id = android.R.color.system_accent1_300),
                        clockDialColor = colorResource(id = android.R.color.system_accent1_800),
                        clockDialUnselectedContentColor = colorResource(id = android.R.color.system_accent1_300)
                    ) else TimePickerDefaults.colors(),
                    layoutType = TimePickerLayoutType.Vertical,

                    )
                Button(
                    onClick = {
                        timeDefined = true
                    },
                    Modifier.padding(bottom = 4.dp)
                ) {
                    Text(text = stringResource(id = R.string.define_time))
                }
            }
        }
    } else {
        Dialog(onDismissRequest = { dismiss() }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                            Color.Black
                        else
                            colorResource(id = android.R.color.system_accent1_900)
                    ),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                DatePicker(dateState)
                Button(
                    onClick = {
                        if (dateState.selectedDateMillis != null) {
                            val dateTime =
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(dateState.selectedDateMillis!!),
                                    ZoneOffset.UTC
                                )
                                    .plusHours(timerState.hour.toLong())
                                    .plusMinutes(timerState.minute.toLong())
                            define(dateTime.toEpochSecond(ZoneOffset.UTC))
                        }
                        dismiss()
                    },
                    Modifier.padding(bottom = 4.dp)
                ) {
                    Text(text = stringResource(id = R.string.define_time))
                }
            }
        }

    }
}

