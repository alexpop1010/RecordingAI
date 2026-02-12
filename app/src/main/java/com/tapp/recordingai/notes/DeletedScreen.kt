package com.tapp.recordingai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tapp.recordingai.db.Note
import com.tapp.recordingai.notes.DeletedNotesViewModel
import com.tapp.recordingai.notes.ItemNotes

@Composable
fun DeletedScreen(
    navController: NavController,
    viewModel: DeletedNotesViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.back),
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = stringResource(R.string.recently_deleted),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            if (viewModel.notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_deleted_notes),
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(viewModel.notes) { deletedNote ->
                        ItemNotes(
                            note = Note(
                                id = deletedNote.id,
                                noteName = deletedNote.noteName,
                                text = deletedNote.text
                            ),
                            onClick = {},
                            onDelete = {
                                viewModel.deleteForever(deletedNote)
                            }
                        )
                    }
                }
            }
        }
    }
}




