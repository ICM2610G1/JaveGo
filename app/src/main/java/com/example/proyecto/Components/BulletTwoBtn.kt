package com.example.proyecto.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.R

@Composable
fun TwoButtonBullet(modifier: Modifier) {
    Row(
        modifier = modifier.clip(
            RoundedCornerShape(16.dp)
        ).background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button (
            content ={
                Image(
                    painter = painterResource(R.drawable.man),
                    contentDescription = "Icon Man",
                    colorFilter = ColorFilter.tint(Color.Blue)
                )
            },
            modifier = Modifier.weight(1f),
            onClick = {},
            shape = RectangleShape,
            colors = ButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.Gray
            )
        )
        VerticalDivider(
            modifier = Modifier.height(35.dp),
            color = Color.Gray
        )
        Button (
            modifier = Modifier.weight(1f),
            content = {
                Image(
                    painter = painterResource(R.drawable.woman),
                    contentDescription = "Icon Woman",
                    colorFilter = ColorFilter.tint(Color.Magenta)
                )
            },
            onClick = {},
            shape = RectangleShape,
            colors = ButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.Gray
            )
        )
    }
}
