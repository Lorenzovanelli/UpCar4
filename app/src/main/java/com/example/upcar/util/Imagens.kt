package com.example.upcar.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun decodificarFoto(context: Context, uri: Uri, ladoMax: Int = 256): ImageBitmap? = try {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }

    var amostra = 1
    while (limites.outWidth / (amostra * 2) >= ladoMax && limites.outHeight / (amostra * 2) >= ladoMax) {
        amostra *= 2
    }

    val opcoes = BitmapFactory.Options().apply { inSampleSize = amostra }
    context.contentResolver.openInputStream(uri)
        ?.use { BitmapFactory.decodeStream(it, null, opcoes) }
        ?.asImageBitmap()
} catch (e: Exception) {
    null
}
