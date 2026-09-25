package com.example.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.data.model.IconShape

fun getShapeForIcon(iconShape: IconShape): Shape {
    return when (iconShape) {
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(32.dp)
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(18.dp)
        IconShape.TEARDROP -> RoundedCornerShape(
            topStart = 26.dp,
            topEnd = 26.dp,
            bottomEnd = 26.dp,
            bottomStart = 6.dp
        )
        IconShape.HEXAGON -> GenericShape { size, _ ->
            val w = size.width
            val h = size.height
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.25f)
            lineTo(w, h * 0.75f)
            lineTo(w * 0.5f, h)
            lineTo(0f, h * 0.75f)
            lineTo(0f, h * 0.25f)
            close()
        }
    }
}
