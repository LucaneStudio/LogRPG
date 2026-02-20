package cam.lucane.studio.log.rpg.ui.components.common.header

import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.R
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun HomeHeader(
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Logo pill ──
        Row(
            modifier = Modifier
                .coloredShadow(
                    color = ColorsSystem.Shadow.copy(0.08f),
                    borderRadius = 99.dp,
                    blurRadius = 12.dp,
                    offsetY = 3.dp
                )
                .background(ColorsSystem.BackgroundCard, CircleShape)
                .padding(
                    end = 20.dp, start = 12.dp,
                    top = 7.dp, bottom = 7.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_logrpg_monograme),
                    contentDescription = "LogRPG logo",
                )
            }

            Icon(
                modifier = Modifier
                    .size(width = 52.dp, height = 14.dp)
                    .offset(y = 1.dp),
                painter = painterResource(R.drawable.ic_logrpg_typo),
                tint = ColorsSystem.TextPrimary,
                contentDescription = "LogRPG logo"
            )
        }

        // ── Bouton import ──
        Box(
            modifier = Modifier
                .size(38.dp)
                .coloredShadow(
                    color = ColorsSystem.Shadow.copy(0.08f),
                    borderRadius = 99.dp,
                    blurRadius = 10.dp,
                    offsetY = 3.dp
                )
                .background(ColorsSystem.BackgroundCard, CircleShape)
                .clickable { onImportClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📥",
                fontSize = 16.sp
            )
        }
    }
}