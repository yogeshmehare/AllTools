package com.yogesh.alltools.notes

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.yogesh.alltools.notes.room.Note

//Append image in TV
//val annotatedString = buildAnnotatedString {
//    append("This is text ")
//    appendInlineContent(id = "imageId")
//    append(" with a call icon")
//}
//val inlineContentMap = mapOf(
//    "imageId" to InlineTextContent(
//        Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
//    ) {
//        Image(
//            imageVector = Icons.Default.Call,
//            modifier = Modifier.fillMaxSize(),
//            contentDescription = ""
//        )
//    }
//)
//
//Text(annotatedString, inlineContent = inlineContentMap)
//@Composable
//fun EditNote(navController: NavHostController, string: String?, string1: String?) {
//    MyEditNote(navController)
//}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditNote(
    navController: NavHostController,
    notesViewModel: NotesViewModel,
    mTitle: String?,
    mContent: String?,
    mNewNote: Boolean?,
    note: String?
) {
    var title by remember { mutableStateOf(mTitle) }
    var content by remember { mutableStateOf(mContent) }
//    Toast.makeText(LocalContext.current, mNewNote.toString(), Toast.LENGTH_SHORT).show()
    val (focusRequester) = FocusRequester.createRefs()

    BackHandler {
        goBack(navController,mNewNote,title,content,notesViewModel,note)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    )
    Column(Modifier.fillMaxWidth()) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button",
                modifier = Modifier
                    .padding(15.dp)
                    .size(45.dp)
                    .clickable {
//                        navController.previousBackStackEntry
//                            ?.savedStateHandle
//                            ?.set("note", Note(0,title.toString(), content.toString()))
                        goBack(navController,mNewNote,title,content,notesViewModel,note)
                    }
            )
            Text(
                text = "22 Sep 2023 \n 09:00",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        TextField(
            value = title ?: "",
            onValueChange = { title = it },
            label = { Text("Enter title for note") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusRequester.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        focusRequester.requestFocus()
                        true
                    }
                    false
                }
        )

        TextField(
            value = content ?: "",
            onValueChange = { content = it },
            label = { Text("Enter content for your note") },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .focusRequester(focusRequester)
        )
    }
}

fun goBack(
    navController: NavHostController,
    mNewNote: Boolean?,
    title: String?,
    content: String?,
    notesViewModel: NotesViewModel,
    note: String?
) {
    if (mNewNote == true && title?.isEmpty() == true && content?.isEmpty() == true) {
    } else if (mNewNote == true) {
        notesViewModel.addNote(
            Note(
                null,
                title.toString(),
                content.toString(),
                NotesConstant.All
            )
        )
    } else {
        val newNote = Gson().fromJson(note, Note::class.java)
        newNote.title = title.toString()
        newNote.content = content.toString()
        notesViewModel.updateNote(newNote)
    }
    navController.popBackStack()
}

@Preview
@Composable
private fun EditNotePreview() {
    val navController = rememberNavController()
    EditNote(
        navController = navController,
        viewModel(),
        "",
        "",
        false,
        ""
    )
}