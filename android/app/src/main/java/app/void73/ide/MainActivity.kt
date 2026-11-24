package app.void73.ide

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import uniffi.devcore.Editor
import uniffi.devcore.Terminal
import uniffi.devcore.coreVersion

class MainActivity : Activity() {
    private var term: Terminal? = null
    private val ui = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForegroundService(Intent(this, DevService::class.java))

        val editor = Editor("fn main() {}\n")
        val box = EditText(this).apply { setText(editor.text()); typeface = Typeface.MONOSPACE }
        val out = TextView(this).apply { typeface = Typeface.MONOSPACE }
        val run = Button(this).apply { text = "Run: echo hello" }

        run.setOnClickListener {
            if (term == null) term = Terminal.spawn("/system/bin/sh", filesDir.absolutePath, 24u, 80u)
            term!!.write("echo hello from devcore ${coreVersion()}\n".toByteArray())
        }
        val poll = object : Runnable {
            override fun run() {
                term?.readAvailable()?.takeIf { it.isNotEmpty() }
                    ?.let { out.append(String(it)) }
                ui.postDelayed(this, 200)
            }
        }
        ui.post(poll)

        setContentView(ScrollView(this).apply {
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 48, 24, 24)
                addView(box); addView(run); addView(out)
            })
        })
    }
}
