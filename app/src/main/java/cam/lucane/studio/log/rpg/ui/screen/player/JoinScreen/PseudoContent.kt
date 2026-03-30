package cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.components.common.SessionBanner
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun PseudoContent(
    playerName: String,
    onNameChange: (String) -> Unit,
    connecting: Boolean,
    errorMessage: String?,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        SessionBanner(
            label    = "SESSION TROUVÉE",
            subtitle = "Connectez-vous pour rejoindre la partie"
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "VOTRE IDENTITÉ",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorsSystem.TextPrimary,
            letterSpacing = 1.5.sp,
            fontFamily = NunitoFontFamily
        )

        // ── Champ pseudo ──────────────────────────────────────────────
        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "VOTRE PRÉNOM / PSEUDO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorsSystem.TextSecondary,
                    letterSpacing = 1.5.sp,
                    fontFamily = NunitoFontFamily
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = playerName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Alice, Gandalf42, Bob…",
                            color = ColorsSystem.TextDisabled,
                            fontFamily = NunitoFontFamily,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onContinue() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = ColorsSystem.TextDisabled,
                        unfocusedBorderColor = ColorsSystem.Divider,
                        focusedContainerColor   = ColorsSystem.BackgroundCard,
                        unfocusedContainerColor = ColorsSystem.BackgroundCard,
                        cursorColor = ColorsSystem.TextSecondary
                    ),
                    textStyle = TextStyle(
                        fontFamily  = NunitoFontFamily,
                        fontSize    = 14.sp,
                        fontWeight  = FontWeight.Bold,
                        color       = ColorsSystem.TextPrimary
                    )
                )

                // Preview MJ
                if (playerName.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(ColorsSystem.Green.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .background(ColorsSystem.Green, RoundedCornerShape(99.dp))
                        )
                        Text(
                            text = "Le MJ vous verra comme : $playerName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorsSystem.Green,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }

                Text(
                    text = "Le MJ verra ce nom à côté de votre personnage.",
                    fontSize = 10.sp,
                    color = ColorsSystem.TextDisabled,
                    fontFamily = NunitoFontFamily,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        // ── Erreur ────────────────────────────────────────────────────
        errorMessage?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = AccentRed,
                fontFamily = NunitoFontFamily,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // ── Boutons ───────────────────────────────────────────────────
        Button(
            onClick  = onContinue,
            enabled  = playerName.isNotBlank() && !connecting,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green)
        ) {
            if (connecting) {
                CircularProgressIndicator(
                    color = ColorsSystem.Green,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = if (connecting) "Connexion…" else "Continuer",
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color = if(connecting) ColorsSystem.Green else Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Button(
            onClick  = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.outlinedButtonColors(containerColor = ColorsSystem.TextDisabled.copy(0.3f)),
        ) {
            Text(
                text = "Rescanner le QR Code",
                fontWeight = FontWeight.ExtraBold,
                color = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily
            )
        }
    }
}