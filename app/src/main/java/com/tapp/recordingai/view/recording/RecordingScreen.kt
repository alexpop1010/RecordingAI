package com.tapp.recordingai.view.recording

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tapp.recordingai.R
import com.tapp.recordingai.model.db.Note
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import com.tapp.recordingai.viewmodel.recording.RecordingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Recording(
    viewModel: RecordingViewModel,
    viewModelNote: NoteViewModel
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current



    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.startRecording()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.startRecording()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSavedTextIfNeeded()
    }

    LaunchedEffect(viewModel.text) {
        delay(1)
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    var showSaveOptionsDialog by remember { mutableStateOf(false) }
    var showFolderPicker by remember { mutableStateOf(false) }
    var pendingStructureWithAi by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    when {
                        viewModel.isRecording -> Color(0xFFE53935)
                        !viewModel.isRecognizerReady ->
                            scheme.onSurface.copy(alpha = 0.22f)
                        else -> scheme.primary
                    }
                )
                .clickable {
                    if (viewModel.isRecording) {
                        viewModel.stopRecording()
                    } else {
                        if (!viewModel.isRecognizerReady) return@clickable
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else {
                                viewModel.startRecording()
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.microsvg),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = when {
                viewModel.isRecording -> stringResource(R.string.stopRecording)
                !viewModel.isRecognizerReady -> stringResource(R.string.recognizer_preparing)
                else -> stringResource(R.string.startRecording)
            },
            fontSize = 14.sp,
            color = scheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.ai),
            fontSize = 18.sp,
            color = scheme.onSurface,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            TextField(
                value = viewModel.text,
                onValueChange = viewModel::onTextChanged,
                placeholder = {
                    Text(
                        stringResource(R.string.notes),
                        color = scheme.onSurfaceVariant
                    )
                },
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 22.sp),
                enabled = viewModel.isEditable,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = scheme.background,
                    unfocusedContainerColor = scheme.background,
                    disabledContainerColor = scheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (viewModel.text.isNotBlank()) {
                    scope.launch {
                        viewModelNote.loadAllNotes()
                        showSaveOptionsDialog = true
                    }
                }
            },
            enabled = !viewModel.isRecording,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                contentColor = scheme.primary,
                disabledContentColor = scheme.outline.copy(alpha = 0.8f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (!viewModel.isRecording) {
                    scheme.primary
                } else {
                    scheme.outline.copy(alpha = 0.6f)
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.save), fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showSaveOptionsDialog) {
        SaveNoteDialog(
            isPreSave = true,
            onDismiss = { showSaveOptionsDialog = false },
            onKeep = {
                pendingStructureWithAi = false
                showSaveOptionsDialog = false
                showFolderPicker = true
            },
            onStructure = {
                pendingStructureWithAi = true
                showSaveOptionsDialog = false
                showFolderPicker = true
            }
        )
    }

    if (showFolderPicker) {
        RecordingFolderPickerDialog(
            folders = viewModelNote.folders,
            onDismiss = {
                showFolderPicker = false
                pendingStructureWithAi = false
            },
            onConfirmed = { existingFolderId, newFolderName ->
                val folderId = if (!newFolderName.isNullOrBlank()) {
                    viewModelNote.createFolderAndGetId(newFolderName.trim())
                } else {
                    existingFolderId
                }
                val saved = viewModelNote.addNoteAndReturn(
                    Note(
                        noteName = "",
                        text = viewModel.text,
                        folderId = folderId
                    )
                )
                showFolderPicker = false
                if (pendingStructureWithAi) {
                    viewModelNote.structureNoteWithAi(saved.id)
                }
                pendingStructureWithAi = false
                viewModel.onTextChanged("")
                viewModel.clearSavedText()
            }
        )
    }
}
