package com.example.consecutivepractices.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.consecutivepractices.R
import com.example.consecutivepractices.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = if (profile.avatarUri.isNotEmpty()) {
                rememberAsyncImagePainter(profile.avatarUri)
            } else {
                painterResource(R.drawable.ic_launcher_foreground)
            },
            contentDescription = "Аватар",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.fullName.ifEmpty { "Не указано" },
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (profile.position.isNotBlank()) {
            Text(
                text = profile.position,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (profile.resumeUrl.isNotBlank()) {
            Button(
                onClick = {
                    openResumeUrl(context, profile.resumeUrl.trim())
                }
            ) {
                Text("Открыть резюме")
            }
        }
    }
}

private fun openResumeUrl(context: android.content.Context, url: String) {
    try {
        var finalUrl = url

        if (finalUrl.isNotEmpty() && !finalUrl.contains("://")) {
            finalUrl = "https://$finalUrl"
        }

        if (finalUrl.contains("drive.google.com")) {
            openGoogleDriveUrl(context, finalUrl)
        } else {
            openGenericUrl(context, finalUrl)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            "Ошибка: ${e.localizedMessage ?: "неизвестная ошибка"}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun openGoogleDriveUrl(context: android.content.Context, url: String) {
    try {
        val uri = Uri.parse(url)

        val driveIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.docs")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        when {
            driveIntent.resolveActivity(context.packageManager) != null -> {
                context.startActivity(driveIntent)
            }
            browserIntent.resolveActivity(context.packageManager) != null -> {
                context.startActivity(browserIntent)
            }
            pdfIntent.resolveActivity(context.packageManager) != null -> {
                context.startActivity(pdfIntent)
            }
            else -> {
                showUrlFallbackDialog(context, url)
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
        openGenericUrl(context, url)
    }
}

private fun openGenericUrl(context: android.content.Context, url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(fallbackIntent)
            } catch (e: Exception) {
                showUrlFallbackDialog(context, url)
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
        showUrlFallbackDialog(context, url)
    }
}

private fun showUrlFallbackDialog(context: android.content.Context, url: String) {
    androidx.appcompat.

    app.AlertDialog.Builder(context)
        .setTitle("Не удалось открыть ссылку")
        .setMessage("Ссылка: $url")
        .setPositiveButton("Открыть в браузере") { dialog, which ->
            forceOpenInBrowser(context, url)
        }
        .setNegativeButton("Копировать ссылку") { dialog, which ->
            copyToClipboard(context, url)
            Toast.makeText(context, "Ссылка скопирована", Toast.LENGTH_SHORT).show()
        }
        .setNeutralButton("Отмена", null)
        .show()
}

private fun forceOpenInBrowser(context: android.content.Context, url: String) {
    try {
        var finalUrl = url
        if (finalUrl.isNotEmpty() && !finalUrl.contains("://")) {
            finalUrl = "https://$finalUrl"
        }

        val uri = Uri.parse(finalUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Не удалось открыть ссылку. Скопируйте её и откройте вручную.",
            Toast.LENGTH_LONG
        ).show()
        copyToClipboard(context, url)
    }
}

private fun copyToClipboard(context: android.content.Context, text: String) {
    try {
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Резюме", text)
        clipboard.setPrimaryClip(clip)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}