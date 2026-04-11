package cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

private const val CODE_LENGTH = 6

@Composable
fun ManualCodeContent(
    onCodeConfirmed : (code: String, onResult: (Boolean) -> Unit) -> Unit,
    onBack          : () -> Unit,
) {
    var code         by remember { mutableStateOf("") }
    var discovering  by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val canSearch = code.length == CODE_LENGTH && !discovering

    fun search() {
        if (!canSearch) return
        discovering  = true
        errorMessage = null
        onCodeConfirmed(code) { ok ->
            discovering = false
            if (!ok) errorMessage =
                "Session introuvable. Vérifiez le code\net que vous êtes sur le même réseau WiFi que le MJ."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text          = "REJOINDRE PAR CODE",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = ColorsSystem.TextSecondary,
            letterSpacing = 1.5.sp,
            fontFamily    = NunitoFontFamily,
            modifier      = Modifier.align(Alignment.Start),
        )

        Spacer(Modifier.height(32.dp))

        Text("🔑", fontSize = 52.sp)

        Spacer(Modifier.height(12.dp))

        Text(
            text       = "Entrez le code affiché\npar votre Maître de Jeu",
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorsSystem.TextPrimary,
            fontFamily = NunitoFontFamily,
            textAlign  = TextAlign.Center,
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value       = code,
            onValueChange = { v ->
                code = v.filter { it.isLetterOrDigit() }.take(CODE_LENGTH).uppercase()
                errorMessage = null
            },
            placeholder = {
                Text(
                    text      = "AB3X7K",
                    color     = ColorsSystem.TextDisabled,
                    fontFamily = NunitoFontFamily,
                    fontSize  = 28.sp,
                    letterSpacing = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth(),
                )
            },
            textStyle = TextStyle(
                fontSize      = 28.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 10.sp,
                textAlign     = TextAlign.Center,
                color         = ColorsSystem.TextPrimary,
                fontFamily    = NunitoFontFamily,
            ),
            singleLine      = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction      = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(onSearch = { search() }),
            shape  = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AccentPurple,
                unfocusedBorderColor = ColorsSystem.TextDisabled,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text      = "${code.length} / $CODE_LENGTH",
            fontSize  = 11.sp,
            color     = if (code.length == CODE_LENGTH) ColorsSystem.Green else ColorsSystem.TextDisabled,
            fontFamily = NunitoFontFamily,
            modifier  = Modifier.align(Alignment.End),
        )

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text      = it,
                fontSize  = 12.sp,
                color     = AccentRed,
                fontFamily = NunitoFontFamily,
                textAlign  = TextAlign.Center,
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick  = ::search,
            enabled  = canSearch,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = AccentPurple,
                disabledContainerColor = AccentPurple.copy(alpha = 0.4f),
            ),
        ) {
            if (discovering) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    color       = ColorsSystem.BackgroundCard,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Recherche en cours…",
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color      = ColorsSystem.BackgroundCard,
                )
            } else {
                Text(
                    "Rejoindre la session",
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color      = ColorsSystem.BackgroundCard,
                    modifier   = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick  = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = ColorsSystem.TextDisabled.copy(alpha = 0.25f),
            ),
        ) {
            Text(
                "Scanner un QR Code à la place",
                fontWeight = FontWeight.ExtraBold,
                color      = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}