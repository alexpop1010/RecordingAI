package com.tapp.recordingai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tapp.recordingai.R
import com.tapp.recordingai.model.db.Note
import com.tapp.recordingai.viewmodel.notes.DeletedNotesViewModel
import com.tapp.recordingai.view.notes.ItemNotes
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeletedScreen(
    navController: NavController,
    viewModel: DeletedNotesViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.recently_deleted),
                    fontSize = 20.sp,
                    color = scheme.onSurface
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(scheme.outline.copy(alpha = 0.45f))
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            if (viewModel.notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_deleted_notes),
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(viewModel.notes) { deletedNote ->
                        ItemNotes(
                            note = Note(
                                id = deletedNote.id,
                                noteName = deletedNote.noteName,
                                text = deletedNote.text
                            ),
                            onClick = {},
                            onRequestMoveToFolder = null,
                            onDelete = {
                                viewModel.deleteForever(deletedNote)
                            },
                            deleteConfirmTitleRes = R.string.note_delete_forever_confirm_title,
                            deleteConfirmMessageRes = R.string.note_delete_forever_confirm_message
                        )
                    }
                }
            }
        }
    }
}
