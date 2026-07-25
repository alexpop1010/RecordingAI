package com.tapp.recordingai.view.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tapp.recordingai.R
import com.tapp.recordingai.model.db.Folder
import com.tapp.recordingai.view.recording.RecordingFolderPickerDialog
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import com.tapp.recordingai.viewmodel.notes.StorageBrowse
import com.tapp.recordingai.viewmodel.notes.StorageRootTab
import kotlinx.coroutines.launch
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
    val browse = viewModel.storageBrowse
    val isRoot = browse is StorageBrowse.Root
    val rootTab = viewModel.storageRootTab
    val queryBlank = viewModel.notesSearchQuery.isBlank()

    val screenTitle = when (browse) {
        is StorageBrowse.Root -> stringResource(R.string.yourNote)
        is StorageBrowse.NotesIn -> {
            viewModel.folders.find { it.id == browse.folderId }?.name ?: ""
        }
    }

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderField by remember { mutableStateOf("") }
    var folderPendingDelete by remember { mutableStateOf<Folder?>(null) }
    var moveFolderNoteId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(moveFolderNoteId) {
        if (moveFolderNoteId != null) {
            viewModel.loadAllNotes()
        }
    }

    BackHandler(enabled = !isRoot) {
        viewModel.openStorageBrowse(StorageBrowse.Root)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
    ) {
        if (!isRoot) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 4.dp, end = 16.dp)
            ) {
                val backLabel = stringResource(R.string.back)
                IconButton(onClick = { viewModel.openStorageBrowse(StorageBrowse.Root) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = backLabel,
                        tint = Color.Black
                    )
                }
                Text(
                    text = screenTitle,
                    fontSize = 20.sp,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        } else {
            Text(
                text = screenTitle,
                fontSize = 24.sp,
                color = scheme.onSurface,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )

            StorageModeToggle(
                mode = rootTab,
                onSelect = viewModel::selectStorageRootTab,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        val showSearchBar =
            viewModel.notes.isNotEmpty() ||
                viewModel.folders.isNotEmpty() ||
                !isRoot ||
                viewModel.notesSearchQuery.isNotEmpty()

        if (showSearchBar) {
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
                    focusedBorderColor = Color(0xFF9E9E9E),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = scheme.onSurface,
                    focusedTextColor = scheme.onSurface,
                    unfocusedTextColor = scheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
        }

        if (isRoot && rootTab == StorageRootTab.Folders && queryBlank) {
            OutlinedButton(
                onClick = {
                    newFolderField = ""
                    showCreateFolderDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, scheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = scheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.CreateNewFolder,
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.storage_create_folder),
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = scheme.primary
                )
            }
        }

        when {
            !isRoot -> {
                when {
                    filtered.isEmpty() && queryBlank -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.storage_folder_empty),
                                fontSize = 18.sp,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 32.dp)
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
                                color = scheme.onSurfaceVariant,
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
                            items(filtered, key = { "in_${it.id}" }) { note ->
                                ItemNotes(
                                    note = note,
                                    onClick = {
                                        navController.navigate("note/${note.id}")
                                    },
                                    onRequestMoveToFolder = { moveFolderNoteId = note.id },
                                    onDelete = {
                                        viewModel.deleteNote(note)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            !queryBlank -> {
                when {
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
                                color = scheme.onSurfaceVariant,
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
                            items(filtered, key = { "s_${it.id}" }) { note ->
                                ItemNotes(
                                    note = note,
                                    onClick = {
                                        navController.navigate("note/${note.id}")
                                    },
                                    onRequestMoveToFolder = { moveFolderNoteId = note.id },
                                    onDelete = {
                                        viewModel.deleteNote(note)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            rootTab == StorageRootTab.AllNotes && viewModel.notes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.noTitle),
                        fontSize = 22.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
            }

            rootTab == StorageRootTab.AllNotes -> {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filtered, key = { "a_${it.id}" }) { note ->
                        ItemNotes(
                            note = note,
                            onClick = {
                                navController.navigate("note/${note.id}")
                            },
                            onRequestMoveToFolder = { moveFolderNoteId = note.id },
                            onDelete = {
                                viewModel.deleteNote(note)
                            }
                        )
                    }
                }
            }

            rootTab == StorageRootTab.Folders && viewModel.folders.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.storage_no_folders),
                        fontSize = 18.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(viewModel.folders, key = { "f_${it.id}" }) { folder ->
                        val count = viewModel.noteCountInFolder(folder.id)
                        StorageFolderRow(
                            folder = folder,
                            noteCount = count,
                            onOpen = {
                                viewModel.openStorageBrowse(
                                    StorageBrowse.NotesIn(folderId = folder.id)
                                )
                            },
                            onRequestDelete = { folderPendingDelete = folder }
                        )
                    }
                }
            }
        }
    }

    if (moveFolderNoteId != null) {
        RecordingFolderPickerDialog(
            folders = viewModel.folders,
            titleResId = R.string.note_action_add_to_folder,
            onDismiss = { moveFolderNoteId = null },
            onConfirmed = { existingFolderId, newFolderName ->
                val id = moveFolderNoteId
                if (id != null) {
                    val folderId = if (!newFolderName.isNullOrBlank()) {
                        viewModel.createFolderAndGetId(newFolderName.trim())
                    } else {
                        existingFolderId
                    }
                    viewModel.moveNoteToFolder(id, folderId)
                }
                moveFolderNoteId = null
            }
        )
    }

    folderPendingDelete?.let { folder ->
        AlertDialog(
            onDismissRequest = { folderPendingDelete = null },
            containerColor = Color(0xFFF5F5F5),
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = stringResource(R.string.folder_delete_confirm_title),
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.folder_delete_confirm_message, folder.name),
                    color = Color(0xFF616161)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = folder.id
                        folderPendingDelete = null
                        scope.launch {
                            viewModel.deleteFolder(id)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.delete_note), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { folderPendingDelete = null }) {
                    Text(text = stringResource(R.string.folder_picker_cancel), color = Color.Black)
                }
            }
        )
    }

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            containerColor = Color(0xFFF5F5F5),
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = stringResource(R.string.storage_create_folder),
                    color = Color.Black
                )
            },
            text = {
                OutlinedTextField(
                    value = newFolderField,
                    onValueChange = { newFolderField = it },
                    label = { Text(stringResource(R.string.folder_new_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.createFolder(newFolderField)
                            showCreateFolderDialog = false
                            newFolderField = ""
                        }
                    },
                    enabled = newFolderField.trim().isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.folder_create), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text(text = stringResource(R.string.folder_picker_cancel), color = Color.Black)
                }
            }
        )
    }
}

@Composable
private fun StorageModeToggle(
    mode: StorageRootTab,
    onSelect: (StorageRootTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8E8E8))
            .padding(4.dp)
    ) {
        val notesLabel = stringResource(R.string.storage_tab_notes)
        val foldersLabel = stringResource(R.string.storage_tab_folders_short)
        StorageToggleChip(
            text = notesLabel,
            selected = mode == StorageRootTab.AllNotes,
            onClick = { onSelect(StorageRootTab.AllNotes) },
            modifier = Modifier.weight(1f)
        )
        StorageToggleChip(
            text = foldersLabel,
            selected = mode == StorageRootTab.Folders,
            onClick = { onSelect(StorageRootTab.Folders) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StorageToggleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.Black else Color(0xFF666666)
        )
    }
}

@Composable
private fun StorageFolderRow(
    folder: Folder,
    noteCount: Int,
    onOpen: () -> Unit,
    onRequestDelete: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val deleteLabel = stringResource(R.string.folder_delete)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onOpen)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    fontSize = 18.sp,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.folder_notes_count, noteCount),
                    fontSize = 14.sp,
                    color = scheme.onSurfaceVariant
                )
            }
        }
        IconButton(
            onClick = onRequestDelete,
            modifier = Modifier.semantics { contentDescription = deleteLabel }
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = deleteLabel,
                tint = scheme.onSurfaceVariant
            )
        }
    }
}
