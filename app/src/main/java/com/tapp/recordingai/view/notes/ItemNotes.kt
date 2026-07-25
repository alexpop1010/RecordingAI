package com.tapp.recordingai.view.notes

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapp.recordingai.R
import com.tapp.recordingai.model.db.Note
import com.tapp.recordingai.utils.NoteShareHelper
import com.tapp.recordingai.viewmodel.notes.NoteStatus

@Composable
fun ItemNotes(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRequestMoveToFolder: (() -> Unit)? = null,
    @StringRes deleteConfirmTitleRes: Int = R.string.note_delete_confirm_title,
    @StringRes deleteConfirmMessageRes: Int = R.string.note_delete_confirm_message
) {
    val context = LocalContext.current
    val isProcessing = note.status == NoteStatus.AI_PROCESSING
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val hasContent = note.noteName.isNotBlank() || note.text.isNotBlank()

    fun noteSubject(): String =
        note.noteName.ifBlank {
            context.getString(R.string.note_default_title, note.id)
        }

    fun noteBodyForShare(): String {
        val t = note.noteName.trim()
        val b = note.text.trim()
        return when {
            t.isNotBlank() && b.isNotBlank() -> "$t\n\n$b"
            t.isNotBlank() -> t
            else -> b
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isProcessing) Color(0xFFE0E0E0) else Color(0xFFF5F5F5)
            )
            .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = !isProcessing, onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = if (note.noteName.isBlank())
                    stringResource(R.string.note_default_title, note.id)
                else
                    note.noteName,
                color = Color.Black
            )

            if (isProcessing) {
                Text(
                    text = stringResource(R.string.note_ai_processing),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = note.text,
                    maxLines = 1,
                    color = Color.Black
                )
            }
        }

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                enabled = !isProcessing
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.content_desc_note_more),
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                containerColor = Color(0xFFF5F5F5)
            ) {
                if (onRequestMoveToFolder != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.note_action_add_to_folder), color = Color.Black) },
                        colors = MenuDefaults.itemColors(textColor = Color.Black),
                        onClick = {
                            menuExpanded = false
                            onRequestMoveToFolder()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_action_share), color = Color.Black) },
                    colors = MenuDefaults.itemColors(textColor = Color.Black),
                    onClick = {
                        menuExpanded = false
                        val body = noteBodyForShare()
                        if (body.isNotBlank()) {
                            NoteShareHelper.sharePlainText(context, noteSubject(), body)
                        }
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_action_copy), color = Color.Black) },
                    colors = MenuDefaults.itemColors(textColor = Color.Black),
                    onClick = {
                        menuExpanded = false
                        val body = noteBodyForShare()
                        if (body.isNotBlank()) {
                            NoteShareHelper.copyToClipboard(context, noteSubject(), body)
                        }
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_action_export_txt), color = Color.Black) },
                    colors = MenuDefaults.itemColors(textColor = Color.Black),
                    onClick = {
                        menuExpanded = false
                        NoteShareHelper.exportTxtAndShare(
                            context,
                            note.noteName.trim(),
                            note.text.trim()
                        )
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_action_export_pdf), color = Color.Black) },
                    colors = MenuDefaults.itemColors(textColor = Color.Black),
                    onClick = {
                        menuExpanded = false
                        NoteShareHelper.exportPdfAndShare(
                            context,
                            note.noteName.trim(),
                            note.text.trim()
                        )
                    },
                    enabled = hasContent
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete_note), color = Color.Black) },
                    colors = MenuDefaults.itemColors(textColor = Color.Black),
                    onClick = {
                        menuExpanded = false
                        showDeleteConfirm = true
                    }
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFFF5F5F5),
            tonalElevation = 0.dp,
            title = { Text(stringResource(deleteConfirmTitleRes), color = Color.Black) },
            text = { Text(stringResource(deleteConfirmMessageRes), color = Color(0xFF616161)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text(stringResource(R.string.delete_note), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.folder_picker_cancel), color = Color.Black)
                }
            }
        )
    }
}

@Preview
@Composable
fun ItemNotesPreview() {
    ItemNotes(
        note = Note(1, "title", "text"),
        onClick = {},
        onDelete = {}
    )
}
