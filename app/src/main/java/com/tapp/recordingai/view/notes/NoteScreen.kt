package com.tapp.recordingai.view.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapp.recordingai.R
import com.tapp.recordingai.utils.NoteShareHelper
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteScreen(
    idNote: Int,
    onBack: () -> Unit = {},
    viewModel: NoteViewModel = koinViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(idNote) {
        val note = viewModel.getNoteById(idNote)
        viewModel.showNote(note)
    }

    var isEditing by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    fun noteSubject(): String =
        viewModel.title.ifBlank {
            context.getString(R.string.note_default_title, idNote)
        }

    fun noteBodyForShare(): String {
        val t = viewModel.title.trim()
        val b = viewModel.text.trim()
        return when {
            t.isNotBlank() && b.isNotBlank() -> "$t\n\n$b"
            t.isNotBlank() -> t
            else -> b
        }
    }

    fun sharePlain() {
        val body = noteBodyForShare()
        if (body.isBlank()) return
        NoteShareHelper.sharePlainText(context, noteSubject(), body)
    }

    fun copyAll() {
        val body = noteBodyForShare()
        if (body.isBlank()) return
        NoteShareHelper.copyToClipboard(context, noteSubject(), body)
    }

    val hasContent =
        viewModel.title.isNotBlank() || viewModel.text.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        enabled = hasContent
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.content_desc_note_more),
                            tint = if (hasContent) Color.Black else Color.LightGray
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.note_action_share)) },
                            onClick = {
                                menuExpanded = false
                                sharePlain()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.note_action_copy)) },
                            onClick = {
                                menuExpanded = false
                                copyAll()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.note_action_export_txt)) },
                            onClick = {
                                menuExpanded = false
                                NoteShareHelper.exportTxtAndShare(
                                    context,
                                    viewModel.title.trim(),
                                    viewModel.text.trim()
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.note_action_export_pdf)) },
                            onClick = {
                                menuExpanded = false
                                NoteShareHelper.exportPdfAndShare(
                                    context,
                                    viewModel.title.trim(),
                                    viewModel.text.trim()
                                )
                            }
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateNote(idNote)
                        }
                        isEditing = !isEditing
                    }
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                        contentDescription = if (isEditing) {
                            stringResource(R.string.content_desc_save_note)
                        } else {
                            stringResource(R.string.edit)
                        },
                        tint = if (isEditing) Color(0xFF4A40FF) else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            BasicTextField(
                value = viewModel.title,
                onValueChange = viewModel::changeTitle,
                textStyle = TextStyle(fontSize = 30.sp, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = viewModel.title,
                fontSize = 30.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LaunchedEffect(viewModel.text) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {

            if (isEditing) {
                BasicTextField(
                    value = viewModel.text,
                    onValueChange = viewModel::changeText,
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = viewModel.text,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
