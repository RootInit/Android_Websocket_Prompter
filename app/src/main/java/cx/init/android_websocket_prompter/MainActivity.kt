/**
 * Android app that displays and speaks incoming messages from a Jetty WebSocket server.
 * The server can be started and stopped with the toggle button in the main activity.
 * Text to speech is used for speaking out the incoming messages.
 */
package cx.init.android_websocket_prompter

import android.app.Application
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import java.util.*

/**
 * Android application used to initialize the web server during app startup.
 * The server object can then be accessed from any class within the app.
 */
class WebServerDisplayApp : Application() {
    /**
     * The current running WebSocket server instance
     */
    companion object {
        lateinit var server: WebSocketServer
    }

    /**
     * Initializes the WebSocket server on app creation
     */
    override fun onCreate() {
        super.onCreate()
        server = WebSocketServer()
    }
}

/**
 * The main Home activity of the Android app
 */
class MainActivity: AppCompatActivity(), TextToSpeech.OnInitListener {

    /**
     * The TextView that displays incoming WebSocket messages
     */
    private lateinit var textView: TextView
    /**
     * TextToSpeech engine used to speak out incoming WebSocket messages
     */
    private var tts: TextToSpeech? = null

    /**
     * Initializes the app components on app creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Set up TextView
        textView = findViewById<View>(R.id.textView) as TextView
        // Set up TextToSpeech
        tts = TextToSpeech(this, this)

        // Set up the server toggle button
        val webServerToggle: ToggleButton = findViewById<View>(R.id.serverToggleButton) as ToggleButton
        webServerToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start the WebSocket server
                WebServerDisplayApp.server.start()
                if (tts == null) {
                    webServerToggle.textOn = "Server Started (tts failed)"
                }
            } else {
                // Stop the WebSocket server
                WebServerDisplayApp.server.stop()
            }
        }

        // Set up the TextView updater coroutine
        val scope = MainScope()
        scope.launch(Dispatchers.Main) {
            WebSocketServer.messageFlow.collect { message ->
                textView.text = message
                tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    /**
     * Initializes the TextToSpeech engine with the default language
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts!!.language = Locale.getDefault()
        }
    }

    /**
     * Cleans up the TextToSpeech engine on activity destruction
     */
    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}

/**
 * The Jetty WebSocket server handling incoming client connections and messages
 */
class WebSocketServer() {
    /**
     * MutableStateFlow used to emit incoming messages to the UI
     */
    companion object {
        val messageFlow = MutableStateFlow("")
    }

    /**
     * Initializes the Jetty WebSocket server at port 2352
     */
    init {
        Spark.port(2352)
        Spark.webSocket("/", WSHandler::class.java)
    }

    /**
     * Starts the Jetty WebSocket server
     */
    fun start() {
        Spark.init()
    }

    /**
     * Stops the Jetty WebSocket server
     */
    fun stop() {
        Spark.stop()
    }

    /**
     * The WebSocket handler class that handles incoming WebSocket client connections and messages
     */
    @WebSocket
    class WSHandler {
        /**
         * Called when a client WebSocket connection is established
         */
        @OnWebSocketConnect
        fun onConnected(session: Session) = println("session connected")
        /**
         * Called when a client WebSocket connection is closed
         */
        @OnWebSocketClose
        fun onClosed(session: Session, statusCode: Int, reason: String?) = println("closed sessions")
        /**
         * Called when a client WebSocket message is received
         * The message is emitted to the UI via the MutableStateFlow
         */
        @OnWebSocketMessage
        fun onMessage(session: Session, message: String) {
            messageFlow.value = message
        }
    }
}
