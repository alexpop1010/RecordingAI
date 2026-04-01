package com.tapp.recordingai.view.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tapp.recordingai.R
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Storage(
    navController: NavController,
    viewModel: NoteViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllNotes()
    }

    val filtered = viewModel.filteredStorageNotes()
    val scheme = MaterialTheme.colorScheme

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
                .padding(top = 24.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (viewModel.notes.isNotEmpty()) {
            OutlinedTextField(
                value = viewModel.notesSearchQuery,
                onValueChange = viewModel::onNotesSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                placeholder = if (viewModel.notesSearchQuery.isEmpty()) {
                    {
                        Text(
                            text = stringResource(R.string.notes_search_hint),
                            color = scheme.onSurfaceVariant
                        )
                    }
                } else {
                    null
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = scheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (viewModel.notesSearchQuery.isNotEmpty()) {
                        val clearLabel = stringResource(R.string.notes_search_clear)
                        IconButton(
                            onClick = { viewModel.onNotesSearchQueryChange("") },
                            modifier = Modifier.semantics { contentDescription = clearLabel }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = clearLabel,
                                tint = scheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = scheme.surface,
                    unfocusedContainerColor = scheme.surface,
                    disabledContainerColor = scheme.surface,
                    focusedBorderColor = scheme.outline,
                    unfocusedBorderColor = scheme.outline.copy(alpha = 0.45f),
                    cursorColor = scheme.primary,
                    focusedTextColor = scheme.onSurface,
                    unfocusedTextColor = scheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
        }

        when {
            viewModel.notes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.noTitle),
                        fontSize = 22.sp,
                        color = Color.Gray
                    )
                }
            }

            filtered.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.notes_search_no_results),
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filtered, key = { it.id }) { note ->
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
}
