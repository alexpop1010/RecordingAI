package com.tapp.recordingai.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapp.recordingai.db.Note
import com.tapp.recordingai.R

@Composable
fun ItemNotes(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isProcessing = note.status == NoteStatus.AI_PROCESSING

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isProcessing) Color(0xFFE0E0E0) else Color(0xFFF5F5F5)
            )
            .clickable(
                enabled = !isProcessing
            ) {
                onClick()
            }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp)
        ) {
            Text(
                text = if (note.noteName.isBlank())
                    "Заметка ${note.id}"
                else
                    note.noteName,
                color = Color.Black
            )

            if (isProcessing) {
                Text(
                    text = "Идёт структурирование…",
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

        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = "Удалить",
            tint = if (isProcessing) Color.LightGray else Color.Black,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterEnd)
                .clickable(enabled = !isProcessing) {
                    onDelete()
                }
        )
    }
}


@Preview
@Composable
fun show(){
    ItemNotes(note = Note(1, "title", "text"), onClick = { }, onDelete = {} )
}
