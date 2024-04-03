package com.yogesh.alltools.smalltools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.yogesh.alltools.R
import com.yogesh.alltools.notes.NotesApp
import com.yogesh.alltools.notes.TodoListApp
import com.yogesh.alltools.stopwatch.StopWatch
import com.yogesh.alltools.stopwatch.ui.theme.AllToolsTheme
import com.yogesh.alltools.utils.Constant

class SharedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolName = intent.getStringExtra("toolName")
        setContent {
            AllToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (toolName){
                        Constant.Age_Calculator -> {
                            AgeCalculator()
                        }
                        Constant.QR_Scanner -> {
                            QRScanner()
                        }
                        Constant.Currency_Converter -> {
                            CurrencyConverter()
                        }
                        Constant.Notes -> {
                            NotesApp()
                        }
                        Constant.TODO_List -> {
                            TodoListApp()
                        }
                        Constant.SOS_FlashLight -> {
                            SOSFlashLight()
                        }
                    }
                }
            }


        }
    }
}

