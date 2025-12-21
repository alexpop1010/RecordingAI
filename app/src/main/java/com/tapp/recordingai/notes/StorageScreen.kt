package com.tapp.recordingai.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tapp.recordingai.notes.NoteViewModel


@Composable
fun Storage(navController: NavController, viewModel: NoteViewModel = viewModel()){
    LaunchedEffect(Unit) {
        viewModel.loadAllNotes()
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxWidth()
            .background(Color.White)
    ){
        items(viewModel.notes){note ->
            ItemNotes(
                note = note,
                onClick = {
                    navController.navigate("note/${note.id}")
                },
                onDelete = {
                    viewModel.deleteNote(note)
                }
            )
        }
    }
}

