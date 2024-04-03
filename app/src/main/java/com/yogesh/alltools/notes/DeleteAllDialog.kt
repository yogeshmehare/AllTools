package com.yogesh.alltools.notes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.yogesh.alltools.R

@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: Painter,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Icon", tint = Color.Blue)
        },
        title = {
            Text(text = dialogTitle, color = Color.Red)
        },
        text = {
            Text(text = dialogText, textAlign = TextAlign.Center, color = Color.Red)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
                modifier = Modifier
            ) {
                Text("Confirm",textAlign = TextAlign.Center, color = Color.Blue)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss",textAlign = TextAlign.Center, color = Color.Blue)
            }
        },
        containerColor = Color.Cyan
    )
}

@Preview
@Composable
fun DialogExamples() {
    val openAlertDialog = remember { mutableStateOf(true) }
    when {
        openAlertDialog.value -> {
            MyAlertDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Confirmation registered")
                },
                dialogTitle = "Delete Selected items",
                dialogText = "Selected notes will be deleted permanently",
                icon = painterResource(id = R.drawable.ic_delete_forever)
            )
        }
    }
}