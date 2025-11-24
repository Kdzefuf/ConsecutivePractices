package com.example.consecutivepractices.ui.screens

import android.Manifest
import android.app.Activity
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
import androidx.compose.material3.AlertDialog
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
import com.example.consecutivepractices.viewmodel.ProfileViewModel
import java.io.File
import java.io.IOException

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

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        resumeUrl = profile.resumeUrl
        position = profile.position
        avatarUri = profile.avatarUri
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
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth()
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
            label = { Text("Ссылка на резюме (PDF)") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.updateProfile(
                UserProfile(
                    fullName = fullName,
                    avatarUri = avatarUri,
                    resumeUrl = resumeUrl,
                    position = position
                )
            )
        }
    }
}

fun requestPermissionsAndOpenGallery(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>, context: Context) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    launcher.launch(permissions)
}

fun requestPermissionsAndOpenCamera(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>, context: Context) {
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