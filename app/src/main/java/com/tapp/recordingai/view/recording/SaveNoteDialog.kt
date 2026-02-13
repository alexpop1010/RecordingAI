package com.tapp.recordingai.view.recording

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapp.recordingai.R

@Composable
fun SaveNoteDialog(
    onDismiss: () -> Unit,
    onKeep: () -> Unit,
    onStructure: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = stringResource(R.string.note_saved_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.note_saved_subtitle),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onStructure,
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFF42A5F5)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF42A5F5)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.note_saved_structure),
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onKeep,
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFF42A5F5)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF42A5F5)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.note_saved_keep),
                        fontSize = 17.sp
                    )
                }
            }
        },
        confirmButton = {}
    )
}
