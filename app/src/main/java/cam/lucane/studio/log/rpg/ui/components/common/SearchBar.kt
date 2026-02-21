package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    mainColor: Color,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(50.dp)
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 16.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            ),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                fontFamily = NunitoFontFamily,
                color = ColorsSystem.TextSecondary
            )
        },
        leadingIcon = {
            Text(
                text = "\uD83D\uDD0D",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        "Effacer",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = mainColor,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = ColorsSystem.BackgroundCard,
            unfocusedContainerColor = ColorsSystem.BackgroundCard,
            cursorColor = mainColor
        ),
        textStyle = TextStyle(fontSize = 13.sp, fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
    )
}

@Composable
fun EmptySearchState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 15.sp, fontFamily = NunitoFontFamily,
            color = ColorsSystem.TextSecondary
        )
    }
}