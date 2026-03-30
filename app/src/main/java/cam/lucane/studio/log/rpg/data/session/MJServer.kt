package cam.lucane.studio.log.rpg.data.session

import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.*
import java.net.*
import java.util.concurrent.ConcurrentHashMap

class MJServer {

    private val gson = Gson()
    private var serverSocket: ServerSocket? = null
    // ✅ ConcurrentHashMap : thread-safe pour accès multi-coroutines
    private val clients = ConcurrentHashMap<String, ClientConnection>()
    // ✅ Mutex pour protéger les modifications de _players
    private val playersMutex = Mutex()

    private val _players = MutableStateFlow<List<PlayerSlot>>(emptyList())
    val players: StateFlow<List<PlayerSlot>> = _players.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    var sessionToken: String = ""
        private set
    var serverPort: Int = 0
        private set

    // ✅ suspend + Dispatchers.IO : getLocalIpAddress() ne peut pas tourner sur le main thread
    suspend fun prepare(port: Int = 8765): SessionConfig = withContext(Dispatchers.IO) {
        sessionToken = generateToken()
        serverPort = port
        SessionConfig(getLocalIpAddress(), port, sessionToken)
    }

    suspend fun startListening(scope: CoroutineScope) {
        withContext(Dispatchers.IO) {
            serverSocket = ServerSocket(serverPort)
        }
        _isRunning.value = true

        scope.launch(Dispatchers.IO) {
            while (isActive && serverSocket?.isClosed == false) {
                try {
                    val socket = serverSocket!!.accept()
                    val id = socket.inetAddress.hostAddress + ":" + socket.port
                    val conn = ClientConnection(id, socket)
                    clients[id] = conn
                    handleClient(conn, scope)
                } catch (e: Exception) {
                    if (_isRunning.value) e.printStackTrace()
                }
            }
        }
    }

    private fun handleClient(conn: ClientConnection, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            android.util.Log.d("MJServer", "✅ Joueur connecté : ${conn.id}")
            try {
                val reader = BufferedReader(InputStreamReader(conn.socket.getInputStream()))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    android.util.Log.d("MJServer", "📨 Message reçu : $line")
                    val msg = runCatching {
                        gson.fromJson(line, SessionMessage::class.java)
                    }.getOrNull() ?: continue

                    if (msg.token != sessionToken) {
                        android.util.Log.w("MJServer", "❌ Token invalide : ${msg.token} (attendu: $sessionToken)")
                        conn.socket.close()
                        return@launch
                    }
                    when (msg.type) {
                        MessageType.SWITCH,
                        MessageType.UPDATE -> msg.payload?.let { updatePlayer(conn.id, it) }
                        MessageType.PING   -> sendTo(conn, SessionMessage(MessageType.PONG, sessionToken))
                        else               -> Unit
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MJServer", "💥 Erreur client ${conn.id} : ${e.message}")
            } finally {
                android.util.Log.d("MJServer", "🔌 Joueur déconnecté : ${conn.id}")
                markDisconnected(conn.id)
            }
        }
    }

    fun kickPlayer(socketId: String) {
        val conn = clients[socketId] ?: return
        sendTo(conn, SessionMessage(MessageType.KICK, sessionToken))
        runCatching { conn.socket.close() }
        clients.remove(socketId)
        _players.value = _players.value.filter { it.socketId != socketId }
    }

    private suspend fun updatePlayer(id: String, info: PlayerInfo) {
        android.util.Log.d("MJServer", "👤 updatePlayer → ${info.playerName} / ${info.characterName}")
        playersMutex.withLock {
            val current = _players.value.toMutableList()
            val idx = current.indexOfFirst { it.socketId == id }
            if (idx >= 0) {
                current[idx] = current[idx].copy(info = info, isConnected = true)
            } else {
                current.add(PlayerSlot(id, info.playerName, info))
            }
            _players.value = current
            android.util.Log.d("MJServer", "📋 Liste joueurs : ${_players.value.map { it.playerName }}")
        }
    }

    private suspend fun markDisconnected(id: String) {
        playersMutex.withLock {
            val current = _players.value.toMutableList()
            val idx = current.indexOfFirst { it.socketId == id }
            if (idx >= 0) current[idx] = current[idx].copy(isConnected = false)
            _players.value = current
        }
    }

    private fun sendTo(conn: ClientConnection, msg: SessionMessage) {
        runCatching {
            PrintWriter(conn.socket.getOutputStream(), true)
                .println(gson.toJson(msg))
        }
    }

    fun stop() {
        _isRunning.value = false
        clients.values.forEach { runCatching { it.socket.close() } }
        clients.clear()
        runCatching { serverSocket?.close() }
        _players.value = emptyList()
    }

    private fun generateToken(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    private fun getLocalIpAddress(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()?.toList() ?: return "127.0.0.1"

        val wifiIp = interfaces
            .filter { it.isUp && !it.isLoopback }
            .filter { iface ->
                iface.name.startsWith("wlan") ||
                        iface.name.startsWith("eth")  ||
                        iface.name.startsWith("rmnet_data") // certains Samsung
            }
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress

        if (wifiIp != null) return wifiIp

        return interfaces
            .filter { it.isUp && !it.isLoopback }
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress
            ?: "127.0.0.1"
    }

    data class ClientConnection(val id: String, val socket: Socket)
}