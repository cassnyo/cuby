package com.cassnyo.cuby.common.ui.composable

import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.caverock.androidsvg.SVG
import kotlin.math.roundToInt

@Composable
fun ScrambleImage(
    imageSvg: String,
    modifier: Modifier = Modifier,
) {
    val image = remember {
        val svg = SVG.getFromString(imageSvg)
        val bitmap = createBitmap(
            width = svg.documentWidth.roundToInt(),
            height = svg.documentHeight.roundToInt()
        )
        val canvas = Canvas(bitmap)
        svg.renderToCanvas(canvas)
        bitmap.asImageBitmap()
    }

    Image(
        bitmap = image,
        contentDescription = null,
        modifier = modifier,
    )
}