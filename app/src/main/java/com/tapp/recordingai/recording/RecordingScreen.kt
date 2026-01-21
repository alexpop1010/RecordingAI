package com.tapp.recordingai.recording

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tapp.recordingai.R
import com.tapp.recordingai.db.Note
import com.tapp.recordingai.notes.NoteViewModel
import androidx.compose.ui.platform.LocalContext

//to do сбрасывать порядковый номер заметок

@Composable
fun Recording(viewModel: RecordingViewModel, viewModelNote:NoteViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.getEditText()
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initRecognizer(context)
    }

    LaunchedEffect(viewModel.text) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    if (viewModel.isRecording) Color(0xFFE53935)
                    else Color(0xFF42A5F5)
                )
                .clickable {
                    if (viewModel.isRecording) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
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
            text = if (viewModel.isRecording)
                stringResource(R.string.stopRecording)
            else
                stringResource(R.string.startRecording),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.ai),
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )


        Spacer(Modifier.height(8.dp))

        TextField(                        //закинуть с записи it
            value = viewModel.text,
            onValueChange = { viewModel.textChanged(it)},
            placeholder = { Text(stringResource(R.string.notes)) },
            maxLines = Int.MAX_VALUE,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textStyle = TextStyle(fontSize = 22.sp),
            enabled = viewModel.isEditable,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (viewModel.text.isNotBlank()){
                    viewModelNote.addNote(Note(noteName = "", text = viewModel.text))
                    viewModel.text = ""
                    viewModel.deleteEditText()

                }
            },
            enabled = !viewModel.isRecording,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                contentColor = Color(0xFF42A5F5),
                disabledContentColor = Color(0xFFB0BEC5)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (!viewModel.isRecording)
                    Color(0xFF42A5F5)
                else
                    Color(0xFFB0BEC5)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Сохранить", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

