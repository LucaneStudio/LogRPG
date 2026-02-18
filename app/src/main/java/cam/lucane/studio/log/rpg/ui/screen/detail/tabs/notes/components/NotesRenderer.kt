package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun NotesRenderer(
    markdown: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        color = GlassSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            if (markdown.isBlank()) {
                // État vide
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📝", fontSize = 48.sp)
                        Text(
                            "Aucune note",
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                        Text(
                            "Passez en mode Edit pour commencer",
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Rendu markdown
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    markdown.lines().forEach { line ->
                        RenderMarkdownLine(line)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderMarkdownLine(line: String) {
    when {
        // Titre H1 : # Texte
        line.startsWith("# ") && !line.startsWith("## ") -> {
            Text(
                text = line.removePrefix("# "),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Titre H2 : ## Texte
        line.startsWith("## ") && !line.startsWith("### ") -> {
            Text(
                text = line.removePrefix("## "),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 6.dp, bottom = 3.dp)
            )
        }

        // Titre H3 : ### Texte
        line.startsWith("### ") -> {
            Text(
                text = line.removePrefix("### "),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        // Liste : - Item
        line.trimStart().startsWith("- ") -> {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("•", color = AccentPurple, fontSize = 14.sp)
                Text(
                    text = parseInlineMarkdown(line.trimStart().removePrefix("- ")),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Citation : > Texte
        line.trimStart().startsWith("> ") -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = AccentPurple.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    AccentPurple.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = parseInlineMarkdown(line.trimStart().removePrefix("> ")),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Ligne vide
        line.isBlank() -> {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Texte normal avec formatage inline
        else -> {
            Text(
                text = parseInlineMarkdown(line),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun parseInlineMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var currentText = text
        var index = 0

        while (index < currentText.length) {
            when {
                // **Gras**
                currentText.substring(index).startsWith("**") -> {
                    val endIndex = currentText.indexOf("**", index + 2)
                    if (endIndex != -1) {
                        val boldText = currentText.substring(index + 2, endIndex)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) {
                            append(boldText)
                        }
                        index = endIndex + 2
                    } else {
                        append(currentText[index])
                        index++
                    }
                }

                // *Italique*
                currentText.substring(index).startsWith("*") &&
                        !currentText.substring(index).startsWith("**") -> {
                    val endIndex = currentText.indexOf("*", index + 1)
                    if (endIndex != -1) {
                        val italicText = currentText.substring(index + 1, endIndex)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = TextPrimary)) {
                            append(italicText)
                        }
                        index = endIndex + 1
                    } else {
                        append(currentText[index])
                        index++
                    }
                }

                // `Code`
                currentText.substring(index).startsWith("`") -> {
                    val endIndex = currentText.indexOf("`", index + 1)
                    if (endIndex != -1) {
                        val codeText = currentText.substring(index + 1, endIndex)
                        withStyle(
                            SpanStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                background = AccentPurple.copy(alpha = 0.15f),
                                color = AccentPurple
                            )
                        ) {
                            append(" $codeText ")
                        }
                        index = endIndex + 1
                    } else {
                        append(currentText[index])
                        index++
                    }
                }

                // Texte normal
                else -> {
                    withStyle(SpanStyle(color = TextPrimary)) {
                        append(currentText[index])
                    }
                    index++
                }
            }
        }
    }
}