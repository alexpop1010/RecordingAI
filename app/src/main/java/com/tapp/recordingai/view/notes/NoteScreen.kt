package com.tapp.recordingai.view.notes


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NoteScreen(
    idNote: Int,
    onBack: () -> Unit = {},
    viewModel: NoteViewModel = koinViewModel()
) {
    LaunchedEffect(idNote) {
        val note = viewModel.getNoteById(idNote)
        viewModel.showNote(note)
    }
    var isEditing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
            Text(
                text = "Назад",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                text = if (isEditing) "Сохранить" else "Редактировать",
                fontSize = 18.sp,
                color = if (isEditing) Color(0xFF4A40FF) else Color.Black,
                modifier = Modifier.clickable {
                    if (isEditing) viewModel.updateNote(idNote)
                    isEditing = !isEditing
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isEditing) {
            BasicTextField(
                value = viewModel.title,
                onValueChange = { viewModel.changeTitle(it)},
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
                    onValueChange = { viewModel.changeText(it) },
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









