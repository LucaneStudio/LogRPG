package cam.lucane.studio.log.rpg.ui.combat.model

data class CombatState(
    val participants    : List<CombatParticipant> = emptyList(),
    val round           : Int = 1,
    val isStarted       : Boolean = false,

    /**
     * IDs des personas qui ont déjà joué ce round, dans leur ordre de passage.
     * Ne contient PAS le persona actif.
     */
    val playedThisRound : List<String> = emptyList(),

    /**
     * ID du persona dont c'est le tour.
     * Null si le combat n'a pas commencé ou s'il n'y a plus de participant actif.
     */
    val currentId       : String? = null,
) {
    /**
     * Liste ordonnée pour l'affichage et la navigation :
     *  1. Déjà joués (ordre de passage verrouillé)
     *  2. Actif en ce moment
     *  3. Non encore joués → toujours triés par effectiveInitiative décroissant
     *
     * Quand un bonus/malus est appliqué à un persona non-joué, il se re-trie
     * immédiatement parmi les #3 sans jamais passer avant l'actif (#2).
     */
    val sortedActive: List<CombatParticipant>
        get() {
            val active     = participants.filter { it.status == CombatStatus.ACTIVE }
            val lockedIds  = (playedThisRound + listOfNotNull(currentId)).toSet()

            val played     = playedThisRound.mapNotNull { id -> active.find { it.id == id } }
            val current    = currentId?.let { id -> active.find { it.id == id } }
            val remaining  = active
                .filter { it.id !in lockedIds }
                .sortedByDescending { it.effectiveInitiative }

            return played + listOfNotNull(current) + remaining
        }

    val sortedInactive: List<CombatParticipant>
        get() = participants.filter { it.status != CombatStatus.ACTIVE }

    val currentParticipant: CombatParticipant?
        get() = currentId?.let { id -> participants.find { it.id == id } }

    val currentTurnIndex: Int
        get() = playedThisRound.size

    /** Vrai quand tous les actifs ont joué (fin de round imminent). */
    val isEndOfRound: Boolean
        get() {
            val totalActive = participants.count { it.status == CombatStatus.ACTIVE }
            return currentId != null && totalActive > 0 &&
                    playedThisRound.size + 1 >= totalActive
        }

    val linkedPJs: List<CombatParticipant>
        get() = participants.filter { it.linkedSocketId != null }

    /** Premier persona à jouer parmi les actifs (pour démarrer ou reprendre). */
    fun firstActiveId(): String? = participants
        .filter { it.status == CombatStatus.ACTIVE }
        .maxByOrNull { it.effectiveInitiative }
        ?.id
}