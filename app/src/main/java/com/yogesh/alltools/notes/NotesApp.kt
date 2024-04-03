package com.yogesh.alltools.notes

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.yogesh.alltools.R
import com.yogesh.alltools.notes.NotesConstant.All
import com.yogesh.alltools.notes.NotesConstant.Important
import com.yogesh.alltools.notes.NotesConstant.filterList
import com.yogesh.alltools.notes.room.Note
import com.yogesh.alltools.utils.Screen
import kotlin.random.Random

@Composable
fun NotesApp() {
    val navController = rememberNavController()
    val notesViewModel = viewModel<NotesViewModel>()
    var selectedItem by remember { mutableStateOf(filterList.first()) }
    val li = listOf<Note>()
    val _notesList = notesViewModel.notes.observeAsState(initial = li).value
    var notesList = remember {
        mutableStateListOf<Note>().apply { addAll(_notesList) }
    }
    val _impNotesList: List<Note> = notesViewModel.impNotes.observeAsState(initial = listOf()).value
    var impNotesList = remember {
        mutableStateListOf<Note>().apply { addAll(_impNotesList) }
    }
    LaunchedEffect(_notesList) {
        notesList = _notesList.toMutableStateList()
        impNotesList = _impNotesList.toMutableStateList()
    }
    val name = "Yogesh"

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Note.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Note.route) {
//                val data = it
//                    .savedStateHandle
//                    .get<Note>("note")
//                mlog(data?:"")
                MyNotesApp(
                    navController,
                    notesList,
                    impNotesList,
                    notesViewModel,
                    name,
                    _notesList,
                    _impNotesList,
                    selectedItem,
                    updateSelectedItem ={selectedItem = it}
                ) { list, q ->
                    if (list.isEmpty() && q == "") {
                        if (selectedItem.filterName == All){
                            notesList.clear()
                            notesList.addAll(_notesList)
                        }else{
                            impNotesList.clear()
                            impNotesList.addAll(_impNotesList)
                        }
                    } else {
                        if (selectedItem.filterName == All) {
                            notesList.clear()
                            notesList.addAll(list)
                        }else{
                            impNotesList.clear()
                            impNotesList.addAll(list)
                        }
                    }
                }
            }
            composable(
                arguments = listOf(
                    navArgument("title") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("content") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("new") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("uniqueNote") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                ),
                route = "${Screen.EditNote.route}?title={title}&content={content}&new={new}&uniqueNote={uniqueNote}"
            ) {
                EditNote(
                    navController,
                    notesViewModel,
                    it.arguments?.getString("title"),
                    it.arguments?.getString("content"),
                    it.arguments?.getBoolean("new"),
                    it.arguments?.getString("uniqueNote")
                )
            }
        }
    }
}

@Composable
fun MyNotesApp(
    navController: NavHostController,
    notesList1: List<Note>,
    impNotesList: List<Note>,
    viewModel: NotesViewModel?,
    name: String,
    defaultNoteList:List<Note>,
    defaultImpNoteList:List<Note>,
    selectedItem: NoteFilter,
    updateSelectedItem:(NoteFilter)->Unit,
    updateSearchedNotes: (List<Note>, String) -> Unit
) {
    var deleteButtonVisible by remember { mutableStateOf(false) }
    var checkAll by remember { mutableStateOf(false) }
    val notesList: List<Note> = if (selectedItem.filterName == All)
        notesList1
    else
        impNotesList
    if (viewModel?.selectedItems?.size == 0)
        deleteButtonVisible = false
    val openAlertDialog = remember { mutableStateOf(false) }
    var deleteAllClicked by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column {
            Header(name = name)
            SearchBar(
                onQueryChange = { query ->
                    val tempList = ArrayList<Note>()
                    if(query!="") {
                        if (selectedItem.filterName == All) {
                            defaultNoteList.forEach {
                                if ((it.title.contains(query) || it.content.contains(query)))
                                    tempList.add(it)
                            }
                        }else{
                            defaultImpNoteList.forEach {
                                if ((it.title.contains(query) || it.content.contains(query)))
                                    tempList.add(it)
                            }
                        }
                    }
                    updateSearchedNotes(tempList,query)
                },
                isSearchActive = false,
                onActiveChanged = {}
            )

            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val count: Int = if (selectedItem.filterName == All)
                        notesList.size
                    else
                        notesList.count { note -> note.type == selectedItem.filterName }
                    items(filterList) { noteFilter ->
                        FilterChip(
                            noteFilter,
                            count = count,
                            isSelected = noteFilter == selectedItem,
                            onClick = {
                                updateSelectedItem(it)
                            }
                        )
                    }
                }
                if (deleteButtonVisible) {
                    Row {
                        Row {
                            Text(
                                text = "Select All",
                                color = Color.White,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Checkbox(
                                checked = checkAll,
                                onCheckedChange = {
                                    checkAll = it
                                    if (checkAll) {
                                        viewModel?.selectedItems = notesList.toMutableList()
                                    } else
                                        viewModel?.selectedItems = mutableListOf()
                                },
                                colors = CheckboxDefaults.colors(uncheckedColor = Color.Black),
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.delete_all),
                            contentDescription = "delete_all",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .clickable {
                                    openAlertDialog.value = true
                                }
                        )
                    }
                    when {
                        openAlertDialog.value -> {
                            MyAlertDialog(
                                onDismissRequest = { openAlertDialog.value = false },
                                onConfirmation = {
                                    openAlertDialog.value = false
                                    checkAll = false
                                    deleteAllClicked = true
                                    viewModel?.deleteSelectedItems()
                                },
                                dialogTitle = "Delete Selected items",
                                dialogText = "Selected notes will be deleted permanently",
                                icon = painterResource(id = R.drawable.ic_delete_forever)
                            )
                        }
                    }
                }
            }
            LazyVerticalGrid(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notesList) { note ->
                    MyNote(
                        note,
                        viewModel,
                        checkAll = checkAll,
                        setCheckAll = { checkAll = it },
                        setDeleteButtonVisible = {
                            deleteButtonVisible = true
                            if (viewModel?.selectedItems?.size == notesList.size) {
                                checkAll = true
                            }
                        },
                        editNote = {
                            val booleanValue = false
                            val route =
                                Screen.EditNote.route + "?title=${it.title}&content=${it.content}&new={$booleanValue}&uniqueNote=${
                                    Uri.encode(
                                        Gson().toJson(it)
                                    )
                                }"
                            navController.navigate(route)
                        })
                }
            }
        }

        FloatingActionButton(
            onClick = {
                val booleanValue = true
                val route = Screen.EditNote.route + "?new=${booleanValue}"
                navController.navigate(route)
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 50.dp, end = 20.dp),
        ) {
            Icon(Icons.Filled.Add, "Add new note button")
        }
    }
}

@Composable
fun DeleteAllDialog() {
}

data class NoteFilter(
    val filterName: String = "",
    var isFilterSelected: Boolean = false,
    val count: Int = 0
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyNote(
    note: Note,
    viewModel: NotesViewModel?,
    setDeleteButtonVisible: (Boolean) -> Unit,
    editNote: (Note) -> Unit,
    checkAll: Boolean,
    setCheckAll: (Boolean) -> Unit
) {
    var id by remember {
        mutableIntStateOf(
            if (note.type == Important) {
                R.drawable.ic_star_fill
            } else {
                R.drawable.ic_star_border
            }
        )
    }
    val checkedState = remember { mutableStateOf(false) }
    val checkBoxVisible = remember { mutableStateOf(false) }
    var cardColor by remember { mutableStateOf(Color.LightGray) }
    if (checkAll) {
        checkBoxVisible.value = true
        checkedState.value = true
    }
    cardColor = if (checkedState.value)
        Color.DarkGray
    else
        Color.LightGray

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
        ),
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    editNote(note)
                },
                onLongClick = {
                    checkedState.value = true
                    checkBoxVisible.value = true
                    setDeleteButtonVisible(true)
                    viewModel?.selectedItems?.add(note)
                }
            )
            .heightIn(0.dp, 450.dp)
            .widthIn(0.dp, 350.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = note.title,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.Black
            )
            Icon(
                painter = painterResource(id = id),
                contentDescription = "Star",
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp)
                    .clickable {
                        if (note.type == Important) {
                            note.type = All
                            id = R.drawable.ic_star_border
                        } else {
                            note.type = Important
                            id = R.drawable.ic_star_fill
                        }
                        viewModel?.updateNote(note)
                    },
                tint = if (note.type == Important) Color.Yellow else Color.DarkGray
            )

        }
        Text(
            text = note.content,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            if (checkBoxVisible.value) {
                Checkbox(
                    checked = checkedState.value,
                    onCheckedChange = {
                        checkedState.value = it
                        if (!checkedState.value) {
                            if (checkAll)
                                setCheckAll(false)
                            checkBoxVisible.value = false
                            cardColor = Color.LightGray
                            viewModel?.selectedItems?.remove(note)
                        }
                    },
                    colors = CheckboxDefaults.colors(uncheckedColor = Color.Black),
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    noteFilter: NoteFilter,
    count: Int,
    isSelected: Boolean,
    onClick: (NoteFilter) -> Unit
) {
    var filterName = noteFilter.filterName
    if (isSelected) {
        filterName += " (${count})"
    }
    FilterChip(
        onClick = { onClick(noteFilter) },
        label = {
            Text(filterName)
        },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Composable
fun Header(name: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 30.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Notes",
            color = Color.White,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 5.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                color = Color.White,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                modifier = Modifier
                    .padding(12.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.Cyan,
                            radius = this.size.maxDimension
                        )
                    },
                text = "${name[0]}",
                style = TextStyle(color = Color.White, fontSize = 12.sp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    // 1
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }
    SearchBar(
        query = searchQuery,
        // 2
        onQueryChange = { query ->
            searchQuery = query
            onQueryChange(query)
        },
        // 3
        onSearch = { },
        active = isSearchActive,
        onActiveChange = activeChanged,
        // 4
        modifier = modifier
            .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
            .fillMaxWidth(),
        placeholder = { Text("Search", color = Color.Black) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = Color.Black,
            )
        },
        // 5
        colors = SearchBarDefaults.colors(
            containerColor = Color.White,
        ),
        tonalElevation = 0.dp,
    ) {
        // Search suggestions or results
    }
}

val colors = arrayOf(
    Color.Cyan,
    Color.Blue,
    Color.Gray,
    Color.Green,
    Color.Magenta,
    Color.Yellow,
    Color.LightGray
)

fun Color.Companion.random(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

@Preview
@Composable
private fun NotesAppPreview() {
    val navController = rememberNavController()
    val name = "Yogesh"
    MyNotesApp(
        navController, listOf(
            Note(0, "hi", "whatsUp", All),
            Note(1, "hello", "jaguar", All),
            Note(2, "kia", "mercedes", Important),
            Note(3, "bmw", "audi", Important),
            Note(4, "bugati", "lambo", Important),
            Note(5, "mustang", "ferrari", Important),
        ), listOf(), viewModel = null, name, listOf(), listOf(),
        filterList.first(), updateSelectedItem = {}, updateSearchedNotes = { _, _ -> run {} })
}