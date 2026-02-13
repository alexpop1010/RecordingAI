package com.tapp.recordingai.view.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.tapp.recordingai.R
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import androidx.compose.foundation.lazy.items
import org.koin.androidx.compose.koinViewModel

@Composable
fun Storage(
    navController: NavController,
    viewModel: NoteViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllNotes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Text(
            text = stringResource(R.string.yourNote),
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (viewModel.notes.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.noTitle),
                    fontSize = 22.sp,
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(viewModel.notes) { note ->
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
    }
}
