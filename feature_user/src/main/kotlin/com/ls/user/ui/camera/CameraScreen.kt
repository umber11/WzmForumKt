package com.ls.user.ui.camera

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ls.user.R
import java.util.concurrent.ExecutionException

/**
 * 相机页面展示（持有 CameraX 生命周期）
 */
@Composable
fun CameraScreen(
    onCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var providerReady by remember { mutableStateOf(false) }
    var useFrontCamera by remember { mutableStateOf(false) }

    fun bindCamera(provider: ProcessCameraProvider, front: Boolean) {
        val selector = if (front) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
        val preview = Preview.Builder().build()
        previewView?.let { preview.setSurfaceProvider(it.surfaceProvider) }
        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        provider.unbindAll()
        provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
        imageCapture = capture
        cameraProvider = provider
    }

    DisposableEffect(Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                cameraProvider = provider
                providerReady = true
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(previewView, providerReady) {
        val provider = cameraProvider ?: return@LaunchedEffect
        if (previewView != null) {
            bindCamera(provider, useFrontCamera)
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(R.mipmap.icon_picture),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .clickable {
                    imageCapture?.let { capture ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "my_image_${System.currentTimeMillis()}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        }
                        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                            context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                        ).build()
                        capture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val savedUri = outputFileResults.savedUri
                                    if (savedUri != null) {
                                        onCaptured(savedUri)
                                    }
                                    Log.d("CameraXApp", "Image saved: $savedUri")
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraXApp", "Error saving image: ${exception.message}")
                                }
                            })
                    }
                }
        )
        Image(
            painter = painterResource(R.mipmap.icon_switch_camera),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 14.dp, end = 14.dp)
                .size(44.dp)
                .clickable {
                    useFrontCamera = !useFrontCamera
                    cameraProvider?.let { bindCamera(it, useFrontCamera) }
                }
        )
    }
}
