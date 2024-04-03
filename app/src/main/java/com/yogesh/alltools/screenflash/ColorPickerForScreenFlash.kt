package com.yogesh.alltools.screenflash

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.yogesh.alltools.screenflash.ui.theme.AllToolsTheme

class ColorPickerForScreenFlash : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AllToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HideSystemBars()
                    FlashScreen()
                }
            }
        }
    }
}

@Composable
private fun FlashScreen() {
    val controller = rememberColorPickerController()
    var flashOff by remember{ mutableStateOf(true)}
    var flashColor by remember{ mutableStateOf( Color(0xFF951FFF) )}

    val changeFlashColor = fun(color: Color) {
        flashColor = color
    }
    Box {
            if (flashOff) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ColorPicker(controller, changeFlashColor)
                    Button(
                        onClick = { flashOff = !flashOff },
                        modifier = Modifier
                            .padding(top = 10.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
                    ) {
                        Text(text = "Flash On", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(flashColor),
                    ){
                    Button(
                        onClick = { flashOff = !flashOff },
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.BottomCenter),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
                    ) {
                        Text(text = "Flash Off", fontWeight = FontWeight.Bold)
                    }
                }
        }
    }
}

@Composable
private fun ColorPicker(controller: ColorPickerController, changeFlashColor: (Color) -> Unit) {

    var hexColor:Color

    controller.apply {
        setWheelAlpha(0.1f)
        setWheelRadius(12.dp)
        setWheelColor(Color.White)
        setEnabled(true)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally,) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                hexColor = colorEnvelope.color // ARGB color value.
                val hexCode = colorEnvelope.hexCode // Color hex code, which represents color value.
//                val fromUser: Boolean =
//                    colorEnvelope.fromUser // Represents this event is triggered by user or not.
                // do something
                changeFlashColor(hexColor)
            },
            initialColor = Color(0xFF951FFF)
        )
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
            borderRadius = 6.dp,
            borderSize = 5.dp,
            borderColor = Color.LightGray,

            wheelRadius = 10.dp,
            wheelColor = Color.White,
            wheelPaint = Paint().apply { color = Color.White },
//            wheelImageBitmap = ImageBitmap.imageResource(R.drawable.wheel),

            tileOddColor = Color.White,
            tileEvenColor = Color.LightGray,
            tileSize = 5.dp,
        )

        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            controller = controller,
            wheelRadius = 10.dp,
            wheelColor = Color.White,
            wheelPaint = Paint().apply { color = Color.White },
//            wheelImageBitmap = ImageBitmap.imageResource(R.drawable.wheel),

            )

        AlphaTile(
            modifier = Modifier
                .size(80.dp)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller,
            tileSize = 5.dp
        )
        Spacer(modifier = Modifier.size(1.dp))
    }


}

@Preview
@Composable
fun Greeting() {
    FlashScreen()
}

@Composable
fun HideSystemBars() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
//            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            insetsController.apply {
                show(WindowInsetsCompat.Type.statusBars())
//                show(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}