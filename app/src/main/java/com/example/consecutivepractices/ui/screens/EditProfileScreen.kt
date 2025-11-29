package com.example.consecutivepractices.ui.screens

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.consecutivepractices.R
import com.example.consecutivepractices.data.models.UserProfile
import com.example.consecutivepractices.service.NotificationScheduler
import com.example.consecutivepractices.util.TimeValidator
import com.example.consecutivepractices.viewmodel.ProfileViewModel
import java.io.File
import java.io.IOException
import java.util.Calendar

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf(profile.fullName) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var position by remember { mutableStateOf(profile.position) }
    var avatarUri by remember { mutableStateOf(profile.avatarUri) }
    var favoritePairTime by remember { mutableStateOf(profile.favoritePairTime) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    val notificationScheduler = remember { NotificationScheduler(context) }

    val showTimePicker = {
        val calendar = Calendar.getInstance()

        if (favoritePairTime.isNotEmpty() && TimeValidator.isValidTime(favoritePairTime)) {
            val parts = favoritePairTime.split(":")
            calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            calendar.set(Calendar.MINUTE, parts[1].toInt())
        }

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                val selectedTime = String.format("%02d:%02d", hour, minute)
                favoritePairTime = selectedTime
                timeError = null
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.setTitle("Выберите время пары")
        timePickerDialog.show()
    }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        resumeUrl = profile.resumeUrl
        position = profile.position
        avatarUri = profile.avatarUri
        favoritePairTime = profile.favoritePairTime
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                avatarUri = uri.toString()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != null) {
            avatarUri = cameraPhotoUri.toString()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val galleryGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)
        } else {
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false)
        }
        val cameraGranted = permissions.getOrDefault(Manifest.permission.CAMERA, false)

        when {
            galleryGranted -> openGallery(galleryLauncher)
            cameraGranted -> openCamera(cameraLauncher, context) { uri -> cameraPhotoUri = uri }
            else -> Toast.makeText(context, "Разрешения отклонены", Toast.LENGTH_SHORT).show()
        }
    }

    val isTimeValid = remember(favoritePairTime) {
        favoritePairTime.isEmpty() || TimeValidator.isValidTime(favoritePairTime)
    }

    val canSave = remember(fullName, favoritePairTime) {
        fullName.isNotBlank() && isTimeValid
    }

    DisposableEffect(Unit) {
        onDispose {
            if (canSave) {
                val newProfile = UserProfile(
                    fullName = fullName,
                    avatarUri = avatarUri,
                    resumeUrl = resumeUrl,
                    position = position,
                    favoritePairTime = favoritePairTime
                )
                viewModel.updateProfile(newProfile)

                if (favoritePairTime.isNotEmpty() && TimeValidator.isValidTime(favoritePairTime)) {
                    notificationScheduler.scheduleNotification(favoritePairTime, fullName)
                } else {
                    notificationScheduler.cancelNotification()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clickable { showImageSourceDialog = true }
        ) {
            Image(
                painter = if (avatarUri.isNotEmpty()) {
                    rememberAsyncImagePainter(avatarUri)
                } else {
                    painterResource(R.drawable.ic_launcher_foreground)
                },
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            isError = fullName.isBlank(),
            supportingText = {
                if (fullName.isBlank()) {
                    Text("Имя не может быть пустым")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text("Должность") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = resumeUrl,
            onValueChange = { resumeUrl = it },
            label = { Text("Ссылка") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = favoritePairTime,
            onValueChange = {
                favoritePairTime = it
                timeError = if (it.isNotEmpty() && !TimeValidator.isValidTime(it)) {
                    "Некорректный формат времени"
                } else {
                    null
                }
            },
            label = { Text("Время любимой пары") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = showTimePicker) {
                    Icon(Icons.Default.Schedule, contentDescription = "Выбрать время")
                }
            },
            isError = timeError != null,
            supportingText = {
                if (timeError != null) {
                    Text(timeError!!)
                } else {
                    Text("Формат: HH:mm")
                }
            },
            placeholder = { Text("18:30") }
        )

        if (!canSave) {
            Text(
                text = "Заполните обязательные поля правильно",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выберите источник") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    requestPermissionsAndOpenCamera(permissionLauncher, context)
                }) {
                    Text("Камера")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    requestPermissionsAndOpenGallery(permissionLauncher, context)
                }) {
                    Text("Галерея")
                }
            }
        )
    }
}

fun requestPermissionsAndOpenGallery(
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    context: Context
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    launcher.launch(permissions)
}

fun requestPermissionsAndOpenCamera(
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    context: Context
) {
    launcher.launch(arrayOf(Manifest.permission.CAMERA))
}

fun openGallery(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }
    launcher.launch(intent)
}

fun openCamera(
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>,
    context: android.content.Context,
    onUriReady: (Uri) -> Unit
) {
    try {
        val photoFile = createImageFile(context)
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        onUriReady(photoUri)
        launcher.launch(photoUri)
    } catch (e: IOException) {
        Toast.makeText(context, "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
    }
}

fun createImageFile(context: android.content.Context): File {
    val storageDir = context.getExternalFilesDir(null)
    return File.createTempFile(
        "avatar_${System.currentTimeMillis()}_",
        ".jpg",
        storageDir
    )
}