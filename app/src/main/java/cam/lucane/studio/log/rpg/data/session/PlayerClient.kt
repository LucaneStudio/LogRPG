package cam.lucane.studio.log.rpg.data.session

import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

class PlayerClient {

    private var ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val gson = Gson()
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var sessionToken = ""

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _wasKicked = MutableStateFlow(false)
    val wasKicked: StateFlow<Boolean> = _wasKicked.asStateFlow()

    suspend fun connect(config: SessionConfig, scope: CoroutineScope): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                android.util.Log.d("PlayerClient", "🔌 Connexion → ${config.ip}:${config.port} token=${config.token}")
                val s = Socket()
                s.tcpNoDelay = true  // ✅ Désactive le buffering Nagle
                s.connect(InetSocketAddress(config.ip, config.port), 5000)
                socket = s
                writer = BufferedWriter(
                    OutputStreamWriter(s.getOutputStream(), Charsets.UTF_8)
                )
                sessionToken = config.token
                _isConnected.value = true
                android.util.Log.d("PlayerClient", "✅ Connecté")
                listenForMessages(s, scope)
                true
            }.getOrElse {
                android.util.Log.e("PlayerClient", "❌ Échec : ${it.message}")
                false
            }
        }

    private fun listenForMessages(socket: Socket, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            runCatching {
                val reader = BufferedReader(
                    InputStreamReader(socket.getInputStream(), Charsets.UTF_8)
                )
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val msg = runCatching {
                        gson.fromJson(line, SessionMessage::class.java)
                    }.getOrNull() ?: continue

                    when (msg.type) {
                        MessageType.KICK -> {
                            android.util.Log.d("PlayerClient", "🦵 Kické par le MJ")
                            _wasKicked.value = true
                            disconnect()
                        }
                        else -> Unit
                    }
                }
            }
            _isConnected.value = false
        }
    }

    fun sendUpdate(info: PlayerInfo) = send(
        SessionMessage(MessageType.UPDATE, sessionToken, info)
    )

    fun switchCharacter(info: PlayerInfo) = send(
        SessionMessage(MessageType.SWITCH, sessionToken, info)
    )

    private fun send(msg: SessionMessage) {
        val json = gson.toJson(msg)
        android.util.Log.d("PlayerClient", "📤 Envoi : $json")
        ioScope.launch {
            runCatching {
                val w = writer ?: run {
                    android.util.Log.e("PlayerClient", "❌ writer est null !")
                    return@launch
                }
                w.write(json)
                w.newLine()
                w.flush()
                android.util.Log.d("PlayerClient", "✅ Envoyé")
            }.onFailure {
                android.util.Log.e("PlayerClient", "❌ Erreur envoi : ${it::class.simpleName} - ${it.message}")
            }
        }
    }

    fun disconnect() {
        _isConnected.value = false
        runCatching { writer?.close() }
        runCatching { socket?.close() }
        socket = null
        writer = null
        ioScope.cancel()
        ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    companion object {
        fun parseQRCode(qrContent: String): SessionConfig? = runCatching {
            val uri = Uri.parse(qrContent)
            SessionConfig(
                ip    = uri.getQueryParameter("ip")    ?: return null,
                port  = uri.getQueryParameter("port")?.toInt() ?: return null,
                token = uri.getQueryParameter("token") ?: return null
            )
        }.getOrNull()
    }
}