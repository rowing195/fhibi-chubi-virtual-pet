package com.umuumu.virtualpet.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.umuumu.virtualpet.R

@Composable
fun PetArtwork(modifier: Modifier = Modifier) {
    val resources = LocalContext.current.resources
    val sheet = remember(resources) { BitmapFactory.decodeResource(resources, R.drawable.pet_spritesheet).asImageBitmap() }
    // The character is decorative; the nearby text announces its status.
    Canvas(modifier) {
        drawImage(sheet, srcOffset = IntOffset.Zero, srcSize = IntSize(192, 208), dstSize = IntSize(size.width.toInt(), size.height.toInt()))
    }
}
