package com.po.admos

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Composable
fun GalleryPickerScreen() {
    val contentResolver = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val coroutineScope = rememberCoroutineScope()
        val mimeTypeFilter =
            arrayOf("image/jpeg", "image/png", "image/gif", "image/jpg")

        // single file picker
        val profileImageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
        val selectProfileActivity =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { fileUri ->
                if (Build.VERSION.SDK_INT < 28) {
                    profileImageBitmap.value =
                        MediaStore.Images.Media.getBitmap(contentResolver.contentResolver, fileUri)
                            .asImageBitmap()
                } else {
                    val source =
                        ImageDecoder.createSource(contentResolver.contentResolver, fileUri!!)
                    profileImageBitmap.value =
                        ImageDecoder.decodeBitmap(source).asImageBitmap()
                }
            }

        // multi file picker
        val photoImageBitmap = remember { mutableStateListOf<ImageBitmap>() }
        val selectPhotoActivity =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { result ->
                photoImageBitmap.clear()
                result.forEach {
                    if (Build.VERSION.SDK_INT < 28) {
                        photoImageBitmap.add(
                            MediaStore.Images.Media.getBitmap(
                                contentResolver.contentResolver,
                                it
                            ).asImageBitmap()
                        )
                    } else {
                        val source =
                            ImageDecoder.createSource(contentResolver.contentResolver, it!!)
                        photoImageBitmap.add(
                            ImageDecoder.decodeBitmap(source).asImageBitmap()
                        )
                    }
                }
            }

        // UI
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = if (profileImageBitmap.value == null) painterResource(R.drawable.ic_launcher_background) else BitmapPainter(
                    profileImageBitmap.value!!
                ),
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .clip(CircleShape)
                    .clickable { selectProfileActivity.launch(mimeTypeFilter) }
            )

            Button(onClick = {
                coroutineScope.launch {
                    selectPhotoActivity.launch(mimeTypeFilter)
                }
            }) {
                Text(text = "Select Photo")
            }

            if (photoImageBitmap.size > 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(photoImageBitmap, itemContent = { photo ->
                        Image(
                            painter = BitmapPainter(photo),
                            contentDescription = "Photo image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(150.dp)
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    })
                }
            }
        }
    }
}

// extension functions
fun Uri.getFileName(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    if (cursor == null || !cursor.moveToFirst()) return null

    val indexName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val fileName = cursor.getString(indexName)
    cursor.close()

    return fileName
}

fun Uri.getFile(context: Context): File? {
    val fileDescriptor = context.contentResolver.openFileDescriptor(this, "r", null)
    if (fileDescriptor == null) return null

    val file = File(context.cacheDir, getFileName(context)!!)
    val fileOutputStream = FileOutputStream(file)

    val fileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
    fileInputStream.copyTo(fileOutputStream)
    fileDescriptor.close()

    return file
}
