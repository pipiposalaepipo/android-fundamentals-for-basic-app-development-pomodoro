package com.skooldio.android.fundamentals.workshop.pomodoro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import android.content.Context
import android.content.Intent
import com.skooldio.android.fundamentals.workshop.pomodoro.Config
import com.skooldio.android.fundamentals.workshop.pomodoro.databinding.ActivityTimer2Binding
// ------------------------------

class TimerActivity : AppCompatActivity() {


    // Note: Using ActivityTimer2Binding to match your R.layout.activity_timer2
    private val binding: ActivityTimer2Binding by lazy {
        ActivityTimer2Binding.inflate(layoutInflater)
    }


    private var config: Config? = null


    companion object {
        private const val EXTRA_CONFIG = "config"

        fun newIntent(
            context: Context,
            config: Config
        ): Intent {
            return Intent(context, TimerActivity::class.java).apply {
                putExtra(EXTRA_CONFIG, config)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(binding.root)

        // --- CHANGED: Use binding.main (root view ID) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        restoreBundle()
    }


    private fun restoreBundle() {
        // This uses the deprecated getParcelableExtra method as shown in the PDF
        // for consistency with the lab.
        config = intent.getParcelableExtra(EXTRA_CONFIG)

        // You can now use the config object, e.g.:
        // Log.d("TimerActivity", "Config received: $config")
    }
}