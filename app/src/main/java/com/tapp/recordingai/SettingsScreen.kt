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
            text = "Настройки",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )


        SectionTitle("Аккаунт")

        SimpleRow(
            title = "Статус аккаунта",
            rightText = "Базовый"
        )

        SimpleRow(
            title = "Повысить до Продвинутый",
            showArrow = true
        )


        SectionTitle("Предпочтения")


        SwitchRow(
            title = "Системный язык",
            subtitle = "Выбрать рус/англ",
            checked = false,
            onCheckedChange = {}
        )


        SectionTitle("Хранилище")

        SimpleRow(
            title = "Недавно удаленные",
            subtitle = "Содержит удаленные за 30 дней заметки",
            showArrow = true
        )


        SectionTitle("Поддержка")

        SimpleRow(title = "Написать в поддержку", showArrow = true)
        SimpleRow(title = "Политика конфиденциальности", showArrow = true)
        SimpleRow(title = "Оценить приложение", showArrow = true)
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