package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(48.dp),
        placeholder = { Text(placeholder, color = TextSecondary, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                "Rechercher",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
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
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentPurple.copy(alpha = 0.6f),
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = GlassSurface,
            unfocusedContainerColor = GlassSurface,
            cursorColor = AccentPurple
        ),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
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
        Text(message, fontSize = 15.sp, color = TextSecondary)
    }
}