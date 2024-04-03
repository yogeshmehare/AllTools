package com.yogesh.alltools.smalltools

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogesh.alltools.R
import com.yogesh.alltools.utils.FlashlightHelper
import com.yogesh.alltools.utils.Utils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SOSFlashLight() {

    val context = LocalContext.current
    val helper = FlashlightHelper(context)

    var flashlightDrawable by remember {
        mutableIntStateOf(R.drawable.ic_flashlight_off)
    }

    var checkState by remember {
        mutableStateOf(false)
    }

    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            if (flashlightDrawable == R.drawable.ic_flashlight_on) {
                flashlightDrawable = R.drawable.ic_flashlight_off
                helper.turnOffFlashlight()
            } else {
                flashlightDrawable = R.drawable.ic_flashlight_on
                helper.turnOnFlashlight()
            }
            if(!checkState) {
                handler.removeCallbacks(this)
                handler.removeCallbacksAndMessages(null)
                flashlightDrawable = R.drawable.ic_flashlight_off
                helper.turnOffFlashlight()
            }
            else
                handler.postDelayed(this, 500)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = flashlightDrawable),
                contentDescription = "flashlight_off",
                modifier = Modifier.combinedClickable(
                    onClick = {
                        if (flashlightDrawable == R.drawable.ic_flashlight_on) {
                            flashlightDrawable = R.drawable.ic_flashlight_off
                            helper.turnOffFlashlight()
                        } else {
                            flashlightDrawable = R.drawable.ic_flashlight_on
                            helper.turnOnFlashlight()
                        }
                    }
                ),
            )
            Spacer(modifier = Modifier.height(120.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(text = "SOS", fontSize = 35.sp, modifier = Modifier.padding(10.dp))
                Switch(checked = checkState, onCheckedChange = {
                    checkState = it
                    if (checkState) {
                        handler.post(runnable)
                        Toast.makeText(context, "SOS ON", Toast.LENGTH_SHORT).show()
                    } else {
                        handler.removeCallbacks(runnable)
                        handler.removeCallbacksAndMessages(null)
                        Toast.makeText(context, "SOS OFF", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.scale(1.5f).padding(20.dp))
            }

        }
    }

}

@Preview
@Composable
fun Sos() {
    SOSFlashLight()
}