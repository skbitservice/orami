package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OramiCoral

val OramiRedStart = Color(0xFFE50914)
val OramiRedEnd = Color(0xFFB81D13)

@Composable
fun OramiLogo(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    ringSize: Dp = 28.dp,
    dotSize: Dp = 8.dp,
    showTagline: Boolean = true,
    taglineSize: TextUnit = 9.sp,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bold Red Gradient Ring "O"
            Box(
                modifier = Modifier
                    .size(ringSize)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF2A2A),
                                Color(0xFFD60000),
                                Color(0xFF9E0000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner hollow circle to form the 'O'
                Box(
                    modifier = Modifier
                        .size(ringSize * 0.45f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            // "RAM"
            Text(
                text = "RAM",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                ),
                color = textColor
            )

            // "I" with floating Red sphere dot
            Box(
                modifier = Modifier.height(ringSize),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Red circular dot floating above
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-2).dp)
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFF3B30), Color(0xFFCC0000))
                            )
                        )
                )

                // Stem of "I"
                Text(
                    text = "I",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = fontSize,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = textColor
                )
            }

            // ".in"
            Text(
                text = ".in",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = fontSize * 0.65f,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                ),
                color = textColor,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "MY ONLY CHOICE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = taglineSize,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = textColor.copy(alpha = 0.9f)
            )
        }
    }
}
