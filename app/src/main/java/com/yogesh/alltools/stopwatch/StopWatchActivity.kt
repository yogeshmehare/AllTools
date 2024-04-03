package com.yogesh.alltools.stopwatch

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogesh.alltools.R
import com.yogesh.alltools.stopwatch.service.StopWatchService
import com.yogesh.alltools.stopwatch.ui.theme.AllToolsTheme
import kotlinx.coroutines.launch


class StopWatchActivity : ComponentActivity() {

    var mService: StopWatchService? = null
    var serviceStarted = false

    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as StopWatchService.LocalBinder
            mService = binder.getService()
            serviceStarted = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceStarted = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var timer by remember {
                mutableStateOf("00:00:00")
            }
            val handler = Handler(Looper.getMainLooper())
            var totalSecs = 0
            var hours = 0
            var minutes = 0
            var seconds = 0

            val runnable = object : Runnable {
                override fun run() {
//                    timer = mService?.randomNumber.toString()
                    totalSecs++
                    hours = totalSecs / 3600;
                    minutes = (totalSecs % 3600) / 60;
                    seconds = totalSecs % 60;

                    timer = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    handler.postDelayed(this, 1000)
                }
            }

            AllToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    StopWatch(runnable, timer, handler, resetTimer = {
                        timer="00:00:00"
                        totalSecs=0
                        hours = 0
                        minutes = 0
                        seconds = 0
                        handler.removeCallbacks(runnable)
                        handler.removeCallbacksAndMessages(null)
                    })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, StopWatchService::class.java)
        startService(intent)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        try {
            unbindService(connection)
            serviceStarted = false
        } catch (_: Exception) {
        }
    }
}

@Composable
fun StopWatch(runnable: Runnable, timer: String, handler: Handler,resetTimer:()->Unit) {

    var startEnabled by remember { mutableStateOf(true) }
    var stopEnabled by remember { mutableStateOf(false) }
    val lapseList = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)) {
                Text(text = "Stop Watch", color = Color.Blue, fontSize = 30.sp)
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextBox(number = timer)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (startEnabled) {
                            handler.postDelayed(runnable, 1000)
                            startEnabled = false
                            stopEnabled = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_start),
                            "Start Timer Icon",
                            Modifier.size(30.dp),
                            tint = Color.Blue
                        )
                    },
                    text = {
                        Text(
                            text = "Start",
                            color = Color.Blue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = if (startEnabled) Color.Green else Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 30.dp, top = 10.dp, end = 10.dp)
                        .width(150.dp)
                )
                Box(modifier=Modifier.padding(bottom = 18.dp)) {
                    Image(painter = painterResource(id = R.drawable.reset),
                        contentDescription = "Reset",
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Color.Cyan, shape = CircleShape
                            )
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(7.dp)
                            .clickable {
                                startEnabled = true
                                stopEnabled = false
                                lapseList.clear()
                                resetTimer()
                            })
                }
                ExtendedFloatingActionButton(
                    onClick = {
                        if (stopEnabled) {
                            stopEnabled = false
                            startEnabled = true
                            handler.removeCallbacksAndMessages(null)
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_stop),
                            "Start Timer Icon",
                            Modifier.size(30.dp),
                            tint = Color.Blue
                        )
                    },
                    text = {
                        Text(
                            text = "Stop",
                            color = Color.Blue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = if (stopEnabled) Color.Red else Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 30.dp, top = 10.dp)
                        .width(150.dp)
                )

            }

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                if (stopEnabled) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (stopEnabled) {
                                lapseList.add(timer)
                                coroutineScope.launch {
                                    // Animate scroll to the 10th item
                                    listState.animateScrollToItem(index = lapseList.size - 1)
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_sprint),
                                "Lapse Time Icon",
                                Modifier.size(30.dp),
                                tint = Color.Blue
                            )
                        },
                        text = {
                            Text(
                                text = "Lapse",
                                color = Color.Blue,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        containerColor = Color.Cyan,
                        modifier = Modifier
                            .padding(bottom = 10.dp, top = 10.dp)
                            .width(150.dp)
                    )
                }
            }


            LazyColumn(state = listState,content = {
                itemsIndexed(lapseList){lapIndex,s ->
                    ListItem(
                        headlineContent = { Text("Lapse $lapIndex : $s") },
                        leadingContent = {
                            Icon(
                                painterResource(id = R.drawable.ic_sprint),
                                contentDescription = "Lapses",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    )
                    HorizontalDivider()
                }
            })

        }

    }
}

@Composable
fun TextBox(number: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
        , contentAlignment = Alignment.Center,
    ) {
        Text(text = number, color = Color.Blue, fontSize = 80.sp,
                modifier = Modifier.padding(top = 40.dp))
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun StopWatchPreview() {
    AllToolsTheme {
        StopWatch(runnable = {}, timer = "00:00:00", handler = Handler(), resetTimer = {})
    }
}