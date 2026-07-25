package com.tapp.recordingai.view.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tapp.recordingai.R
import com.tapp.recordingai.utils.LanguageSet
import com.tapp.recordingai.utils.NavConstants

@Composable
fun Settings(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity

    val mode = LanguageSet.getMode(context)

    fun applyLanguage(newMode: String) {
        if (newMode == LanguageSet.getMode(context)) return
        LanguageSet.setMode(context, newMode)
        activity.recreate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp)
            .background(Color.White)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        SectionTitle(stringResource(R.string.account))

        SimpleRow(
            title = stringResource(R.string.account),
            rightText = stringResource(R.string.basic)
        )

        SimpleRow(
            title = stringResource(R.string.upToPro),
            showArrow = true
        )

        SectionTitle(stringResource(R.string.language_section_title))

        SelectableLanguageRow(
            label = stringResource(R.string.language_system),
            subtitle = stringResource(R.string.language_system_hint),
            selected = mode == LanguageSet.MODE_SYSTEM,
            onClick = { applyLanguage(LanguageSet.MODE_SYSTEM) }
        )

        SelectableLanguageRow(
            label = stringResource(R.string.language_russian),
            subtitle = stringResource(R.string.language_russian_hint),
            selected = mode == LanguageSet.MODE_RUSSIAN,
            onClick = { applyLanguage(LanguageSet.MODE_RUSSIAN) }
        )

        SelectableLanguageRow(
            label = stringResource(R.string.language_english),
            subtitle = stringResource(R.string.language_english_hint),
            selected = mode == LanguageSet.MODE_ENGLISH,
            onClick = { applyLanguage(LanguageSet.MODE_ENGLISH) }
        )

        SectionTitle(stringResource(R.string.storage))

        SimpleRow(
            title = stringResource(R.string.deleted),
            subtitle = stringResource(R.string.deleted30),
            showArrow = true,
            onClick = {
                navController.navigate(NavConstants.DELETED)
            }
        )

        SectionTitle(stringResource(R.string.sup))

        SimpleRow(
            title = stringResource(R.string.writeSup),
            showArrow = true,
            onClick = {
                val url = "https://t.me/falencigHelp"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )
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
fun SelectableLanguageRow(
    label: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun SimpleRow(
    title: String,
    subtitle: String? = null,
    rightText: String? = null,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        Modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable { onClick() }
                } else it
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (showArrow) {
                Text(
                    ">",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
