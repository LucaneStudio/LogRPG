package cam.lucane.studio.log.rpg.ui.combat

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cam.lucane.studio.log.rpg.ui.combat.model.CombatStatus
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.combat.panels.CombatLeftPanel
import cam.lucane.studio.log.rpg.ui.combat.panels.CombatRightPanel

@Composable
fun CombatScreen(
    viewModel     : CombatViewModel = viewModel(factory = CombatViewModel.Factory),
    onNavigateBack: () -> Unit,
) {
    val context        = LocalContext.current
    val state          by viewModel.state.collectAsState()
    val sessionPlayers by viewModel.sessionPlayers.collectAsState()

    var showEnd by remember { mutableStateOf(false) }

    // ── Landscape forcé + barres système cachées ─────────────────────────────
    // Keyer sur state.isStarted : l'effet est ignoré tant que le combat n'est pas
    // lancé, puis re-déclenché dès que isStarted passe à true.
    DisposableEffect(state.isStarted) {
        if (!state.isStarted) return@DisposableEffect onDispose {}
        val activity = context as? Activity ?: return@DisposableEffect onDispose {}
        val window     = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val originalOrientation = activity.requestedOrientation

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            activity.requestedOrientation = originalOrientation
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // ── Callbacks ────────────────────────────────────────────────────────────
    val onHpChange        : (String, Int) -> Unit          = { id, d -> viewModel.changeHp(id, d) }
    val onAddCondition    : (String, String) -> Unit       = { id, c -> viewModel.addCondition(id, c) }
    val onRemoveCondition : (String, String) -> Unit       = { id, c -> viewModel.removeCondition(id, c) }
    val onInitiativeSet   : (String, Int) -> Unit          = { id, v -> viewModel.updateInitiative(id, v) }
    val onBonusAdd        : (String, Int) -> Unit          = { id, b -> viewModel.addBonus(id, b) }
    val onBonusRemove     : (String) -> Unit               = { id   -> viewModel.removeBonus(id) }
    val onSetStatus       : (String, CombatStatus) -> Unit = { id, s -> viewModel.setStatus(id, s) }
    val onRemove          : (String) -> Unit               = { id   -> viewModel.removeParticipant(id) }
    val onAddParticipant = { name: String, type: ParticipantType, maxHp: Int, init: Int ->
        viewModel.addParticipant(name, type, maxHp, init)
    }

    if (!state.isStarted) {
        CombatSetupScreen(
            state               = state,
            connectedPlayers    = sessionPlayers,
            onAddSessionPlayers = { viewModel.addSessionPlayers(it) },
            onAddParticipant    = onAddParticipant,
            onSetInitiative     = onInitiativeSet,
            onRemove            = onRemove,
            onLaunch            = { viewModel.startCombat() },
            onCancel            = { onNavigateBack() },
        )
        return
    }

    // ── Layout paysage ───────────────────────────────────────────────────────
    Row(modifier = Modifier.fillMaxSize()) {
        CombatLeftPanel(
            state             = state,
            onHpChange        = onHpChange,
            onAddCondition    = onAddCondition,
            onRemoveCondition = onRemoveCondition,
            onNextTurn        = { viewModel.nextTurn() },
            onEndCombat       = { showEnd = true },
            onInitiativeSet   = onInitiativeSet,
            onBonusAdd        = onBonusAdd,
            onBonusRemove     = onBonusRemove,
            onSetStatus       = onSetStatus,
            modifier          = Modifier.weight(0.65f),
        )
        CombatRightPanel(
            state            = state,
            onHpChange       = onHpChange,      // ← branché ici
            onAddParticipant = onAddParticipant,
            onSetStatus      = onSetStatus,
            onRemove         = onRemove,
            onInitiativeSet  = onInitiativeSet,
            onBonusAdd       = onBonusAdd,
            onBonusRemove    = onBonusRemove,
            modifier         = Modifier.weight(0.35f),
        )
    }

    if (showEnd) {
        CombatEndSheet(
            state     = state,
            onDismiss = {
                // Forcer le retour en portrait immédiatement, sans attendre le dispose
                (context as? Activity)?.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                showEnd = false
                onNavigateBack()
            },
        )
    }
}