package com.yogesh.calculator.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogesh.alltools.calculator.CalculatorViewModel
import com.yogesh.calculator.ui.theme.DarkOperatorTeal
import com.yogesh.calculator.ui.theme.EqualsTeal
import com.yogesh.calculator.ui.theme.InputGray
import com.yogesh.calculator.ui.theme.NumberTeal

@Composable
@Preview(device = Devices.PIXEL_4, backgroundColor = 0xFFFFFFFF, showBackground = true)
fun CalculatorScreen(viewModel: CalculatorViewModel? = null) {
    val currentExpression = viewModel?.getCurrentExpression()?.observeAsState()?.value
    val result = viewModel?.getResult()?.observeAsState()?.value

    Column(modifier = Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.weight(0.33f)) {
            InputAreaUI(currentExpression, result)
        }
        Box(modifier = Modifier.weight(0.66f)) {
            NumPadUI(viewModel)
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, backgroundColor = 0xFFFFFFFF, showBackground = true)
private fun InputAreaUI(
    currentExpression: String? = "20 x 10 + 50",
    result: String? = "250"
) {
    Column(
        modifier = Modifier
            .background(InputGray)
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = currentExpression ?: "",
            modifier = Modifier.background(InputGray),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )

        Text(
            text = result ?: "",
            modifier = Modifier.background(InputGray),
            color = Color.White,
            style = MaterialTheme.typography.displayLarge,
            maxLines = 1
        )
    }
}

@Composable
@Preview
private fun NumPadUI(viewModel: CalculatorViewModel? = null) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .weight(0.2f)

        FourButtonPadRow(
            rowModifier,
            listOf(PadButton.AC, PadButton.PlusMinus, PadButton.Percent, PadButton.Divide),
            viewModel
        )

        FourButtonPadRow(
            rowModifier,
            listOf(PadButton.Seven, PadButton.Eight, PadButton.Nine, PadButton.Multiply),
            viewModel
        )

        FourButtonPadRow(
            rowModifier,
            listOf(PadButton.Four, PadButton.Five, PadButton.Six, PadButton.Plus),
            viewModel
        )

        FourButtonPadRow(
            rowModifier,
            listOf(PadButton.One, PadButton.Two, PadButton.Three, PadButton.Minus),
            viewModel
        )

        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.25f)) {
                PadButtonUI(
                    PadButton.Zero,
                    viewModel
                )
            }

            Column(modifier = Modifier.weight(0.25f)) {
                PadButtonUI(
                    PadButton.Decimal,
                    viewModel
                )
            }

            Column(modifier = Modifier.weight(0.5f)) {
                PadButtonUI(
                    PadButton.Equals,
                    viewModel
                )
            }
        }
    }
}

@Composable
private fun FourButtonPadRow(
    modifier: Modifier = Modifier,
    rowElements: List<PadButton> = listOf(
        PadButton.AC,
        PadButton.PlusMinus,
        PadButton.Percent,
        PadButton.Divide
    ),
    viewModel: CalculatorViewModel?
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (rowElement in rowElements) {
            Column(modifier = Modifier.weight(0.25f)) {
                PadButtonUI(
                    rowElement,
                    viewModel
                )
            }
        }
    }
}

enum class PadButton(
    val buttonText: String,
    val backgroundColor: Color,
    val expressionValue: String = buttonText
) {
    AC("AC", EqualsTeal),
    PlusMinus("+/-", EqualsTeal),
    Percent("%", EqualsTeal),
    Divide("÷", DarkOperatorTeal, "/"),
    Seven("7", NumberTeal),
    Eight("8", NumberTeal),
    Nine("9", NumberTeal),
    Multiply("x", DarkOperatorTeal, "*"),
    Four("4", NumberTeal),
    Five("5", NumberTeal),
    Six("6", NumberTeal),
    Minus("-", DarkOperatorTeal),
    One("1", NumberTeal),
    Two("2", NumberTeal),
    Three("3", NumberTeal),
    Plus("+", DarkOperatorTeal),
    Zero("0", NumberTeal),
    Decimal(",", NumberTeal, "."),
    Equals("=", EqualsTeal)
}

@Composable
@Preview(heightDp = 80, widthDp = 80)
fun PadButtonUI(button: PadButton = PadButton.AC, viewModel: CalculatorViewModel? = null) {
    Box(
        modifier = Modifier
            .background(button.backgroundColor)
            .fillMaxSize()
            .clickable(onClick = {
                when (button) {
                    PadButton.AC -> viewModel?.onClearExpression()
                    PadButton.PlusMinus -> viewModel?.onExpressionSignChange()
                    PadButton.Equals -> viewModel?.onCalculateResult()
                    else -> viewModel?.onOperatorAdd(button.expressionValue)
                }
            }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = button.buttonText,
            color = Color.White,
            style = MaterialTheme.typography.displayMedium
        )
    }
}