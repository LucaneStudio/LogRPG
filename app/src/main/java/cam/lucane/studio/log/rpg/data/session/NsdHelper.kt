package cam.lucane.studio.log.rpg.data.session

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.*

private const val NSD_SERVICE_TYPE   = "_logrpg._tcp."
private const val NSD_SERVICE_PREFIX = "LogRPG-"

class NsdAdvertiser(context: Context) {

    private val nsd = context.getSystemService(NsdManager::class.java)
    private var regListener: NsdManager.RegistrationListener? = null

    fun start(port: Int, token: String) {
        stop()
        val info = NsdServiceInfo().apply {
            serviceName = "$NSD_SERVICE_PREFIX${token.uppercase()}"
            serviceType = NSD_SERVICE_TYPE
            this.port   = port
        }
        regListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(i: NsdServiceInfo) {
                android.util.Log.d("NsdAdvertiser", "✅ Annoncé : ${i.serviceName}")
            }
            override fun onRegistrationFailed(i: NsdServiceInfo, e: Int) {
                android.util.Log.e("NsdAdvertiser", "❌ Enregistrement échoué : $e")
            }
            override fun onServiceUnregistered(i: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(i: NsdServiceInfo, e: Int) = Unit
        }.also {
            runCatching { nsd.registerService(info, NsdManager.PROTOCOL_DNS_SD, it) }
                .onFailure { android.util.Log.e("NsdAdvertiser", "❌ Impossible : ${it.message}") }
        }
    }

    fun stop() {
        val l = regListener ?: return
        regListener = null
        runCatching { nsd.unregisterService(l) }
    }
}

class NsdDiscoverer(context: Context) {

    private val nsd = context.getSystemService(NsdManager::class.java)
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    fun discover(
        code    : String,
        scope   : CoroutineScope,
        onFound : (SessionConfig) -> Unit,
        onError : () -> Unit,
    ) {
        stop()
        val target = "$NSD_SERVICE_PREFIX${code.trim().uppercase()}"
        var done   = false

        scope.launch {
            delay(12_000L)
            if (!done) {
                done = true
                stop()
                withContext(Dispatchers.Main) { onError() }
            }
        }

        fun resolve(serviceInfo: NsdServiceInfo) {
            nsd.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                override fun onResolveFailed(i: NsdServiceInfo, e: Int) {
                    android.util.Log.e("NsdDiscoverer", "❌ Résolution échouée : $e")
                    if (!done) { done = true; scope.launch(Dispatchers.Main) { onError() } }
                }
                override fun onServiceResolved(i: NsdServiceInfo) {
                    if (done) return
                    done = true
                    val ip = i.host?.hostAddress
                    if (ip == null) { scope.launch(Dispatchers.Main) { onError() }; return }
                    val cfg = SessionConfig(ip = ip, port = i.port, token = code.trim().uppercase())
                    android.util.Log.d("NsdDiscoverer", "✅ Résolu → $ip:${i.port}")
                    scope.launch(Dispatchers.Main) { onFound(cfg) }
                }
            })
        }

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(t: String, e: Int) {
                android.util.Log.e("NsdDiscoverer", "❌ Découverte impossible : $e")
                if (!done) { done = true; scope.launch(Dispatchers.Main) { onError() } }
            }
            override fun onStopDiscoveryFailed(t: String, e: Int) = Unit
            override fun onDiscoveryStarted(t: String) {
                android.util.Log.d("NsdDiscoverer", "🔍 Recherche démarrée pour $target")
            }
            override fun onDiscoveryStopped(t: String) = Unit
            override fun onServiceLost(i: NsdServiceInfo) = Unit
            override fun onServiceFound(i: NsdServiceInfo) {
                android.util.Log.d("NsdDiscoverer", "📡 Service trouvé : ${i.serviceName}")
                if (i.serviceName == target && !done) {
                    val dl = discoveryListener
                    discoveryListener = null
                    dl?.let { runCatching { nsd.stopServiceDiscovery(it) } }
                    resolve(i)
                }
            }
        }.also {
            runCatching { nsd.discoverServices(NSD_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, it) }
                .onFailure {
                    android.util.Log.e("NsdDiscoverer", "❌ Erreur lancement : ${it.message}")
                    if (!done) { done = true; scope.launch(Dispatchers.Main) { onError() } }
                }
        }
    }

    fun stop() {
        val dl = discoveryListener ?: return
        discoveryListener = null
        runCatching { nsd.stopServiceDiscovery(dl) }
    }
}