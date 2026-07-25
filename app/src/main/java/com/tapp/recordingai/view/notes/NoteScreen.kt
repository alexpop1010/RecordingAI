package com.tapp.recordingai.view.notes

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tapp.recordingai.R
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteScreen(
    idNote: Int,
    onBack: () -> Unit = {},
    viewModel: NoteViewModel = koinViewModel()
) {
    var isEditing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val openImageDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val tag = "![image]($uri)"
        val nextText = if (viewModel.text.isBlank()) tag else "${viewModel.text}\n\n$tag"
        viewModel.changeText(nextText)
    }

    LaunchedEffect(idNote) {
        val note = viewModel.getNoteById(idNote)
        viewModel.showNote(note)
    }

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
                    tint = if (isEditing) scheme.primary else scheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            BasicTextField(
                value = viewModel.title,
                onValueChange = viewModel::changeTitle,
                textStyle = TextStyle(fontSize = 30.sp, color = scheme.onSurface),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = viewModel.title,
                fontSize = 30.sp,
                color = scheme.onSurface
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { openImageDocument.launch(arrayOf("image/*")) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = stringResource(R.string.content_desc_add_photo),
                            tint = scheme.onSurface
                        )
                    }
                }
                BasicTextField(
                    value = viewModel.text,
                    onValueChange = viewModel::changeText,
                    textStyle = TextStyle(fontSize = 18.sp, color = scheme.onSurface),
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                NoteContent(
                    text = viewModel.text,
                    textColor = scheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun NoteContent(
    text: String,
    textColor: Color
) {
    val blocks = remember(text) { parseNoteBlocks(text) }
    blocks.forEach { block ->
        when (block) {
            is NoteBlock.Text -> {
                if (block.value.isNotBlank()) {
                    Text(
                        text = block.value,
                        fontSize = 18.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            is NoteBlock.Image -> {
                AsyncImage(
                    model = block.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private sealed interface NoteBlock {
    data class Text(val value: String) : NoteBlock
    data class Image(val uri: String) : NoteBlock
}

private fun parseNoteBlocks(text: String): List<NoteBlock> {
    val regex = Regex("!\\[image\\]\\((content://[^)]+)\\)")
    val result = mutableListOf<NoteBlock>()
    var cursor = 0
    regex.findAll(text).forEach { match ->
        if (match.range.first > cursor) {
            result += NoteBlock.Text(text.substring(cursor, match.range.first).trim('\n'))
        }
        result += NoteBlock.Image(match.groupValues[1])
        cursor = match.range.last + 1
    }
    if (cursor < text.length) {
        result += NoteBlock.Text(text.substring(cursor).trim('\n'))
    }
    if (result.isEmpty()) {
        result += NoteBlock.Text(text)
    }
    return result
}
