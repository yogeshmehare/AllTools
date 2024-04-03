package com.yogesh.alltools.smalltools

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.yogesh.alltools.utils.CustomDialog
import com.yogesh.alltools.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

@Composable
fun CurrencyConverter(){
    MyCurrencyConverter()
}
fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCurrencyConverter() {
    val context = LocalContext.current
    val activity = context.getActivity()
    val openDialog = remember {
        mutableStateOf(false)
    }
    val currencyCodeList = remember {
        mutableStateListOf<String>()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


//        if (openDialog.value) {
//            CustomDialog(openDialogCustom = openDialog)
//        }
    }

    currencyCodeList.toList().mapIndexed { index, s ->

    }


//    ExposedDropdownMenuBox(expanded = true, onExpandedChange = {} ) {
//
//    }

    LaunchedEffect(key1 = "") {
        val url = "https://www.x-rates.com/table/?from=INR&amount=1"
        if (Utils.isNetworkAvailable(context)) {
//            val doc = withContext(Dispatchers.IO) {
//                Jsoup.connect(url).get()
//            }
//            val ratesTable = doc.getElementsByClass("tablesorter ratesTable")
//            for (element in ratesTable){
//                for(childNode in element.childNodes()){
//                    if(childNode.nodeName() == "tbody"){
//                        for (element in childNode.childNodes()) {
//                            if(element is Element && element.nodeName() == "tr") {
//                                println("${element.childNode(1).childNode(0)}")
//                            }
//                            if(element is Element && element.nodeName() == "tr") {
//                                println("${element.childNode(5).childNode(0).childNode(0)}")
//                            }
//                        }
//                    }
//                }
//            }

            val url1 = "https://taxsummaries.pwc.com/glossary/currency-codes"
            val doc = withContext(Dispatchers.IO) {
                Jsoup.connect(url1).get()
            }
            val ratesTable = doc.getElementsByClass("section__content")
            val list = mutableListOf<String>()
            for (element in ratesTable) {
                for (childNode in element.childNodes()[3].childNodes()[1].childNodes()) {
                    if(childNode.nodeName()=="tr" && childNode.childNodes().size>5) {
//                        println(childNode.childNode(1).childNode(0))
                        list.add(childNode.childNode(3).childNode(0).toString())
//                        println(childNode.childNode(5).childNode(0))
                    }
                }
            }
            list.removeAt(0)
            currencyCodeList.addAll(list)

        }else{
            openDialog.value = true
        }
    }
}


@Preview
@Composable
fun CurrencyConverterPreview(){
    MyCurrencyConverter()
}