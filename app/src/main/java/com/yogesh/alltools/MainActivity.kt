package com.yogesh.alltools

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.yogesh.alltools.calculator.CalculatorActivity
import com.yogesh.alltools.imagetopdf.ImageToPdfActivity
import com.yogesh.alltools.screenflash.ColorPickerForScreenFlash
import com.yogesh.alltools.smalltools.SharedActivity
import com.yogesh.alltools.stopwatch.StopWatchActivity
import com.yogesh.alltools.ui.theme.AllToolsTheme
import com.yogesh.alltools.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AllToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenWithDrawerAndToolbar()
                }
            }
        }
    }

    companion object {
        val toolList = listOf(
            Tool(R.drawable.calculator, Constant.Calculator),
            Tool(R.drawable.ic_mic, Constant.Voice_Recorder),
            Tool(R.drawable.ic_pdf, Constant.Image_To_PDF),
            Tool(R.drawable.ic_birthday, Constant.Age_Calculator),
            Tool(R.drawable.ic_currency_exchange, Constant.Currency_Converter),
            Tool(R.drawable.ic_notes, Constant.Notes),
            Tool(R.drawable.ic_todo, Constant.TODO_List),
            Tool(R.drawable.ic_flashlight_off, Constant.SOS_FlashLight),
        )

        val recentlyUsedToolList = listOf(
            Tool(R.drawable.calculator, Constant.Calculator),
            Tool(R.drawable.ic_mic, Constant.QR_Scanner),
            Tool(R.drawable.ic_mic, Constant.StopWatch),
            Tool(R.drawable.ic_mic, Constant.Screen_Flash)
        )
    }
}
@Composable
fun MyText(text:String,modifier: Modifier = Modifier){
    Text(text = text, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold,modifier = modifier)
}

@Composable
fun MainScreenWithDrawerAndToolbar() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backToolbarButtonVisible by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    val updateShowSheet = fun(value: Boolean) {
        showSheet = value
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.requiredWidth(250.dp),
            ) {
                Row {
                    IconButton(
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier
                            .layoutId("backButton")
                            .padding(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                    Text(
                        "Drawer title", modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                // ...other drawer items
            }
        },
        gesturesEnabled = true
    ) {
        // Screen content
        CenterAlignedTopAppBarExample(drawerState, scope, backToolbarButtonVisible)
        Column(Modifier.padding(top = 70.dp)) {
            RecentlyUsedTools(context, updateShowSheet)
            AllOtherTools(context, updateShowSheet)
            if (showSheet) {
                BottomSheet {
                    showSheet = false
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
            val intent = Intent(context,StopWatchActivity::class.java)
            intent.putExtras(bundleOf(Pair("uri",capturedImageUri)))
            context.startActivity(intent)
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
                MyText(text = "Click on below icon to start converting to PDF",
                    modifier = Modifier.padding(top = 20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .height(150.dp)
                        .padding(top = 15.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 50.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "CameraIcon",
                            Modifier
                                .height(80.dp)
                                .width(80.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        captureImageFromCamera(
                                            cameraLauncher,
                                            permissionLauncher,
                                            context,
                                            uri
                                        )
                                        onDismiss()
                                    }
                                }
                        )
                        MyText(text = "Camera")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "GalleryIcon",
                            Modifier
                                .height(80.dp)
                                .width(80.dp)
                                .clickable {

                                    onDismiss()
                                }
                        )
                        MyText(text = "Gallery")
                    }
                }
            }
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

fun captureImageFromCamera(
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    context: Context,
    uri: Uri
) {
    val permissionCheckResult =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        cameraLauncher.launch(uri)
    } else {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}

@Composable
fun RecentlyUsedTools(context: Context, showSheet: (Boolean) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
        contentPadding = PaddingValues(5.dp),
    ) {
        itemsIndexed(MainActivity.recentlyUsedToolList) { index, tool ->
            RecentToolItem(tool, index, context)
        }
    }
}

@Composable
fun AllOtherTools(context: Context, showSheet: (Boolean) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(MainActivity.toolList) { tool ->
            ToolItem(tool, context, showSheet)
        }
    }
}

@Composable
fun RecentToolItem(tool: Tool, index: Int, context: Context) {
    var color = Color.White
    when (index) {
        0 -> color = Color(0xFF327ADC)
        1 -> color = Color(0xFF018949)
        2 -> color = Color(0xFFD34537)
        3 -> color = Color(0xFFCD52B3)
    }

    Card(
        onClick = {
            when (tool.toolName) {
                Constant.Calculator -> {
                    context.startActivity(Intent(context, CalculatorActivity::class.java))
                }

                Constant.Voice_Recorder -> {}
                Constant.StopWatch -> {
                    context.startActivity(Intent(context, StopWatchActivity::class.java))
                }

                Constant.Screen_Flash -> {
                    context.startActivity(
                        Intent(
                            context,
                            ColorPickerForScreenFlash::class.java
                        )
                    )
                }
                Constant.QR_Scanner -> {
                    context.startActivity(
                        Intent(
                            context,
                            SharedActivity::class.java
                        ).putExtra("toolName",Constant.QR_Scanner)
                    )
                }
            }
        },
        colors = CardDefaults.cardColors(color),
        modifier = Modifier
            .width(180.dp)
            .padding(horizontal = 5.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = tool.icon),
                contentDescription = "Calculator",
                modifier = Modifier
                    .size(70.dp)
                    .padding(10.dp)
                    .background(color = Color(0xFF1B191B), shape = CircleShape)
                    .padding(8.dp)
            )
            Text(
                text = tool.toolName, color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 10.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }

}

@Composable
fun ToolItem(tool: Tool, context: Context, showSheet: (Boolean) -> Unit) {

    Card(
        onClick = {
            when (tool.toolName) {
                Constant.Image_To_PDF -> {
                    context.startActivity(Intent(context, ImageToPdfActivity::class.java))
//                    showSheet(true)
                }
                Constant.Age_Calculator -> {
                    val intent = Intent(context, SharedActivity::class.java)
                    intent.putExtra("toolName",Constant.Age_Calculator)
                    context.startActivity(intent)
//                    showSheet(true)
                }
                Constant.Currency_Converter -> {
                    context.startActivity(
                        Intent(
                            context,
                            SharedActivity::class.java
                        ).putExtra("toolName",Constant.Currency_Converter)
                    )
                }
                Constant.Notes -> {
                    context.startActivity(
                        Intent(
                            context,
                            SharedActivity::class.java
                        ).putExtra("toolName",Constant.Notes)
                    )
                }
                Constant.TODO_List -> {
                    context.startActivity(
                        Intent(
                            context,
                            SharedActivity::class.java
                        ).putExtra("toolName",Constant.TODO_List)
                    )
                }
                Constant.SOS_FlashLight -> {
                    if(context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                        context.startActivity(
                            Intent(
                                context,
                                SharedActivity::class.java
                            ).putExtra("toolName", Constant.SOS_FlashLight)
                        )
                    }else{
                        Toast.makeText(context,"No FlashLight found on device", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


            }
        },
        colors = CardDefaults.cardColors(Color(0xFF1A1A1A)),
        modifier = Modifier.size(width = 50.dp, height = 100.dp),
    ) {
        Image(
            painter = painterResource(id = tool.icon), contentDescription = tool.toolName,
            modifier = Modifier
                .size(60.dp)
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = tool.toolName,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.White
        )
    }
}

data class Tool(val icon: Int = R.drawable.ic_launcher_web, val toolName: String = "")


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AllToolsTheme {
        MainScreenWithDrawerAndToolbar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBarExample(
    drawerState: DrawerState,
    scope: CoroutineScope,
    backToolbarButtonVisible: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "All Tools",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (backToolbarButtonVisible) {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            drawerState.apply {
                                scope.launch {
                                    if (isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                actions = {
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        innerPadding
//        ScrollContent(innerPadding)
    }
}