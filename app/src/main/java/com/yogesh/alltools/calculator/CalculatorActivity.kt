package com.yogesh.alltools.calculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.yogesh.alltools.R
import com.yogesh.calculator.composables.CalculatorScreen
import com.yogesh.calculator.ui.theme.SimpleCalcTheme

class CalculatorActivity : ComponentActivity() {

    private var viewModel: CalculatorViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = viewModels<CalculatorViewModel>().value

        viewModel.apply {
            this?.getInvalidExpressionMessageEvent()?.observe(this@CalculatorActivity) { shouldShow ->
                if (shouldShow != null && shouldShow) {
                    this@CalculatorActivity.showInvalidExpressionMessage()
                }
            }
        }

        setContent {
            SimpleCalcTheme {
                CalculatorScreen(viewModel)
            }
        }
    }

    private fun showInvalidExpressionMessage(): Unit =
        Toast.makeText(this, getString(R.string.invalid_expression_message), Toast.LENGTH_SHORT)
            .show()
}