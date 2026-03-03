package cam.lucane.studio.log.rpg.utils

import android.graphics.Bitmap
import android.util.Base64
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Format custom LR (LogRPG) — 4 QR codes séparés
 *
 * QR1 — Stats   : LR1|name|curHP|maxHP|tempHP|curMana|maxMana|manaMode|slots|currencyMode|credits|legacyNotes
 * QR2 — Sorts   : LR2\nname|desc|cost|range|duration|cat|notes\n...
 * QR3 — Inv.    : LR3\nname|desc|qty|weight|cat|notes|isConsumable|isEquipped\n...
 * QR4 — Notes   : LR4\ntitle|content\n...
 *
 * Séparateur champ : │ (U+2502) — rare dans le texte utilisateur
 * Séparateur record : \n
 * Valeur null/vide : champ vide entre deux │
 * GZIP + Base64 sur chaque payload avant encodage QR
 */
object MultiQrCodeUtils {

    private const val SEP = "│"   // séparateur champ (U+2502)
    private const val PREFIX_STATS = "LR1"
    private const val PREFIX_ABILITIES = "LR2"
    private const val PREFIX_ITEMS = "LR3"
    private const val PREFIX_NOTES = "LR4"
    private const val QR_LIMIT = 2953  // limite QR binaire niveau L

    // ─────────────────────────────────────────
    //  ENCODE
    // ─────────────────────────────────────────

    fun encodeStats(character: Character, slots: List<SpellSlot>): String {
        val slotsStr = slots.filter { it.max > 0 }
            .joinToString(",") { "${it.current}/${it.max}" }
        val raw = listOf(
            PREFIX_STATS,
            character.name,
            character.currentHealth,
            character.maxHealth,
            character.temporaryHealth,
            character.currentMana,
            character.maxMana,
            character.manaMode.name,
            slotsStr,
            character.currencyMode.name,
            character.credits,
            character.notes ?: ""
        ).joinToString(SEP)
        return compress(raw)
    }

    fun encodeAbilities(abilities: List<Ability>): String {
        val lines = mutableListOf(PREFIX_ABILITIES)
        abilities.forEach { a ->
            // ✅ Supprimer les champs vides en fin de ligne → moins de données à compresser
            val fields = listOf(
                a.name, a.description,
                a.cost ?: "", a.range ?: "",
                a.duration ?: "", a.category ?: "",
                a.notes ?: ""
            )
            lines.add(fields.trimTrailingEmpty().joinToString(SEP))
        }
        return compress(lines.joinToString("\n"))
    }

    fun encodeItems(items: List<Item>): String {
        val lines = mutableListOf(PREFIX_ITEMS)
        items.forEach { i ->
            // ✅ isConsumable/isEquipped en queue — souvent les deux à 0, gain notable
            val fields = listOf(
                i.name, i.description,
                i.quantity.toString(),
                i.weight ?: "", i.category ?: "",
                i.notes ?: "",
                if (i.isConsumable) "1" else "0",
                if (i.isEquipped) "1" else "0"
            )
            lines.add(fields.trimTrailingEmpty().joinToString(SEP))
        }
        return compress(lines.joinToString("\n"))
    }

    fun encodeNotes(notes: List<Note>): String {
        val lines = mutableListOf(PREFIX_NOTES)
        notes.forEach { n ->
            // ✅ Escaper les \n du contenu — sinon split("\n") au décodage crée 1 note par ligne
            val safeContent = n.content.replace("\n", "\\n")
            lines.add("${n.title}${SEP}${safeContent}")
        }
        return compress(lines.joinToString("\n"))
    }

    // ─────────────────────────────────────────
    //  DECODE
    // ─────────────────────────────────────────

    /** Retourne le type du payload ou null si non reconnu */
    fun detectType(raw: String): QrType? {
        val decompressed = runCatching { decompress(raw) }.getOrNull() ?: return null
        return when {
            decompressed.startsWith(PREFIX_STATS) -> QrType.STATS
            decompressed.startsWith(PREFIX_ABILITIES) -> QrType.ABILITIES
            decompressed.startsWith(PREFIX_ITEMS) -> QrType.ITEMS
            decompressed.startsWith(PREFIX_NOTES) -> QrType.NOTES
            else -> null
        }
    }

    fun decodeStats(raw: String): StatsPayload? {
        return runCatching {
            val text = decompress(raw)
            val parts = text.split(SEP)
            if (parts.size < 12 || parts[0] != PREFIX_STATS) return null
            val slots = parseSlots(parts[8])
            StatsPayload(
                name = parts[1],
                currentHealth = parts[2].toInt(),
                maxHealth = parts[3].toInt(),
                temporaryHealth = parts[4].toInt(),
                currentMana = parts[5].toInt(),
                maxMana = parts[6].toInt(),
                manaMode = parts[7],
                spellSlots = slots,
                currencyMode = parts[9],
                credits = parts[10].toInt(),
                legacyNotes = parts[11].ifEmpty { null }
            )
        }.getOrNull()
    }

    fun decodeAbilities(raw: String): List<AbilityPayload>? {
        return runCatching {
            val lines = decompress(raw).split("\n")
            if (lines.isEmpty() || lines[0] != PREFIX_ABILITIES) return null
            lines.drop(1).filter { it.isNotBlank() }.map { line ->
                val p = line.split(SEP)
                AbilityPayload(
                    name = p.getOrElse(0) { "" },
                    description = p.getOrElse(1) { "" },
                    cost = p.getOrElse(2) { "" }.ifEmpty { null },
                    range = p.getOrElse(3) { "" }.ifEmpty { null },
                    duration = p.getOrElse(4) { "" }.ifEmpty { null },
                    category = p.getOrElse(5) { "" }.ifEmpty { null },
                    notes = p.getOrElse(6) { "" }.ifEmpty { null }
                )
            }
        }.getOrNull()
    }

    fun decodeItems(raw: String): List<ItemPayload>? {
        return runCatching {
            val lines = decompress(raw).split("\n")
            if (lines.isEmpty() || lines[0] != PREFIX_ITEMS) return null
            lines.drop(1).filter { it.isNotBlank() }.map { line ->
                val p = line.split(SEP)
                ItemPayload(
                    name = p.getOrElse(0) { "" },
                    description = p.getOrElse(1) { "" },
                    quantity = p.getOrElse(2) { "1" }.toIntOrNull() ?: 1,
                    weight = p.getOrElse(3) { "" }.ifEmpty { null },
                    category = p.getOrElse(4) { "" }.ifEmpty { null },
                    notes = p.getOrElse(5) { "" }.ifEmpty { null },
                    isConsumable = p.getOrElse(6) { "0" } == "1",
                    isEquipped = p.getOrElse(7) { "0" } == "1"
                )
            }
        }.getOrNull()
    }

    fun decodeNotes(raw: String): List<NotePayload>? {
        return runCatching {
            val lines = decompress(raw).split("\n")
            if (lines.isEmpty() || lines[0] != PREFIX_NOTES) return null
            lines.drop(1).filter { it.isNotBlank() }.map { line ->
                val idx = line.indexOf(SEP)
                if (idx == -1) NotePayload(line, "")
                else {
                    val title   = line.substring(0, idx)
                    // ✅ Désescaper les \n pour restaurer le contenu multi-ligne original
                    val content = line.substring(idx + SEP.length).replace("\\n", "\n")
                    NotePayload(title, content)
                }
            }
        }.getOrNull()
    }

    // ─────────────────────────────────────────
    //  QR BITMAP
    // ─────────────────────────────────────────

    fun generateBitmap(payload: String, size: Int = 600): Bitmap? {
        return runCatching {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
            )
            val matrix = MultiFormatWriter().encode(
                payload, BarcodeFormat.QR_CODE, size, size, hints
            )
            BarcodeEncoder().createBitmap(matrix)
        }.getOrNull()
    }

    fun estimateSize(payload: String): Int = payload.length

    fun fitsInQr(payload: String): Boolean = payload.length <= QR_LIMIT

    // ─────────────────────────────────────────
    //  COMPRESSION
    // ─────────────────────────────────────────

    /** Retire les chaînes vides en fin de liste pour réduire la taille du payload */
    private fun List<String>.trimTrailingEmpty(): List<String> {
        var end = size
        while (end > 0 && this[end - 1].isEmpty()) end--
        return subList(0, end)
    }

    private fun compress(text: String): String {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(text.toByteArray(Charsets.UTF_8)) }
        return Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
    }

    private fun decompress(encoded: String): String {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        return GZIPInputStream(bytes.inputStream()).use {
            it.readBytes().toString(Charsets.UTF_8)
        }
    }

    private fun parseSlots(raw: String): List<SlotPayload> {
        if (raw.isBlank()) return emptyList()
        return raw.split(",").mapIndexedNotNull { idx, s ->
            val parts = s.split("/")
            if (parts.size == 2) SlotPayload(
                level = idx + 1,
                current = parts[0].toIntOrNull() ?: 0,
                max = parts[1].toIntOrNull() ?: 0
            ) else null
        }
    }

    // ─────────────────────────────────────────
    //  DATA CLASSES
    // ─────────────────────────────────────────

    enum class QrType { STATS, ABILITIES, ITEMS, NOTES }

    data class StatsPayload(
        val name: String,
        val currentHealth: Int, val maxHealth: Int, val temporaryHealth: Int,
        val currentMana: Int, val maxMana: Int,
        val manaMode: String,
        val spellSlots: List<SlotPayload>,
        val currencyMode: String, val credits: Int,
        val legacyNotes: String?
    )

    data class SlotPayload(val level: Int, val current: Int, val max: Int)

    data class AbilityPayload(
        val name: String, val description: String,
        val cost: String?, val range: String?,
        val duration: String?, val category: String?,
        val notes: String?
    )

    data class ItemPayload(
        val name: String, val description: String,
        val quantity: Int, val weight: String?,
        val category: String?, val notes: String?,
        val isConsumable: Boolean, val isEquipped: Boolean
    )

    data class NotePayload(val title: String, val content: String)
}