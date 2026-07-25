package com.tapp.recordingai.view.recording

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tapp.recordingai.R
import com.tapp.recordingai.model.db.Folder
import kotlinx.coroutines.launch

private sealed class FolderPick {
    data object Unfiled : FolderPick()
    data class Existing(val id: Int) : FolderPick()
}

@Composable
fun RecordingFolderPickerDialog(
    folders: List<Folder>,
    onDismiss: () -> Unit,
    onConfirmed: suspend (existingFolderId: Int?, newFolderName: String?) -> Unit,
    titleResId: Int = R.string.save_note_pick_folder_title
) {
    var pick by remember { mutableStateOf<FolderPick>(FolderPick.Unfiled) }
    var newFolderName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        pick = FolderPick.Unfiled
        newFolderName = ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF5F5F5),
        tonalElevation = 0.dp,
        title = { Text(text = stringResource(titleResId), color = Color.Black) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            pick = FolderPick.Unfiled
                        }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = pick is FolderPick.Unfiled,
                        onClick = { pick = FolderPick.Unfiled }
                    )
                    Text(
                        text = stringResource(R.string.folder_unfiled),
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                folders.forEach { folder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pick = FolderPick.Existing(folder.id)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = pick == FolderPick.Existing(folder.id),
                            onClick = { pick = FolderPick.Existing(folder.id) }
                        )
                        Text(
                            text = folder.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = scheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = {
                        newFolderName = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text(stringResource(R.string.folder_new_name_hint)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        val trimmedNew = newFolderName.trim()
                        when {
                            trimmedNew.isNotEmpty() -> onConfirmed(null, trimmedNew)
                            pick is FolderPick.Unfiled -> onConfirmed(null, null)
                            pick is FolderPick.Existing ->
                                onConfirmed((pick as FolderPick.Existing).id, null)
                        }
                    }
                }
            ) {
                Text(text = stringResource(R.string.save), color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.folder_picker_cancel), color = Color.Black)
            }
        }
    )
}
