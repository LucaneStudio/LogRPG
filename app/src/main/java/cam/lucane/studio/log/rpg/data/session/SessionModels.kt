package cam.lucane.studio.log.rpg.data.session

data class PlayerInfo(
    val playerName: String,
    val characterName: String,
    val currentHealth: Int,
    val maxHealth: Int,
    val currentMana: Int,
    val maxMana: Int,
    val hasMana: Boolean = true,           // false = mode emplacements de sorts
    val spellSlotsJson: String? = null     // ✨ JSON des emplacements si hasMana=false
)

data class SessionMessage(
    val type: MessageType,
    val token: String,
    val payload: PlayerInfo? = null
)

enum class MessageType {
    SWITCH, UPDATE, KICK, PING, PONG
}

data class SessionConfig(
    val ip: String,
    val port: Int,
    val token: String
) {
    fun toQRContent() = "logrpg://session?ip=$ip&port=$port&token=$token"
}

data class PlayerSlot(
    val socketId: String,
    val playerName: String,
    val info: PlayerInfo?,
    val isConnected: Boolean = true
)