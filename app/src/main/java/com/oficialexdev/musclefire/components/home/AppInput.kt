package com.oficialexdev.musclefire.components.home

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
@Composable
fun AppInput(
    value: String,
    onChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    number: Boolean = false
) {

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (number && Regex("^[0-9]*$").matches(it)) {
                onChange(it)
            } else if (!number) {
                onChange(it)
            }
        },
        modifier,
        shape = RoundedCornerShape(16),
        textStyle = textStyle,
        maxLines = 3,
        keyboardOptions = if (number) KeyboardOptions(
            keyboardType = KeyboardType.Number
        ) else KeyboardOptions.Default

    )
}