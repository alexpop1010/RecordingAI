package com.tapp.recordingai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Settings() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp)
            .background(Color.White)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        SectionTitle(stringResource(R.string.settings))

        SimpleRow(
            title = stringResource(R.string.account),
            rightText = stringResource(R.string.basic)
        )

        SimpleRow(
            title = stringResource(R.string.upToPro),
            showArrow = true
        )

        SectionTitle(stringResource(R.string.prefs))

        SwitchRow(
            title = stringResource(R.string.sysLanguage),
            subtitle = stringResource(R.string.chose),
            checked = false,
            onCheckedChange = {}
        )

        SectionTitle(stringResource(R.string.storage))

        SimpleRow(
            title = stringResource(R.string.deleted),
            subtitle = stringResource(R.string.deleted30),
            showArrow = true
        )

        SectionTitle(stringResource(R.string.sup))

        SimpleRow(title = stringResource(R.string.writeSup), showArrow = true)
        SimpleRow(title = stringResource(R.string.privacy), showArrow = true)
        SimpleRow(title = stringResource(R.string.rating), showArrow = true)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SimpleRow(
    title: String,
    subtitle: String? = null,
    rightText: String? = null,
    showArrow: Boolean = false
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (rightText != null) {
                Text(
                    rightText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showArrow) {
                Text(">")
            }
        }
    }
}

@Composable
fun SwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text(title, fontSize = 16.sp)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
@Preview
fun ShowSettings(){
    Settings()

}