package com.skooldio.android.fundamentals.workshop.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skooldio.android.fundamentals.workshop.pomodoro.config.PomodoroConfig
import com.skooldio.android.fundamentals.workshop.pomodoro.databinding.ActivityMainBinding


import android.content.Context
import android.content.Intent
import com.skooldio.android.fundamentals.workshop.pomodoro.Config

import com.skooldio.android.fundamentals.workshop.pomodoro.TimerActivity
import com.skooldio.android.fundamentals.workshop.pomodoro.data.LocalStorage



class MainActivity : AppCompatActivity() {
    // Requester for permission requesting
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Do nothing
    }

    companion object {
        private const val WORK_DURATION_DEFAULT = PomodoroConfig.WORK_DURATION_DEFAULT
        private const val WORK_DURATION_MIN = PomodoroConfig.WORK_DURATION_MIN
        private const val WORK_DURATION_MAX = PomodoroConfig.WORK_DURATION_MAX

        private const val SHORT_BREAK_DURATION_DEFAULT = PomodoroConfig.SHORT_BREAK_DURATION_DEFAULT
        private const val SHORT_BREAK_DURATION_MIN = PomodoroConfig.SHORT_BREAK_DURATION_MIN
        private const val SHORT_BREAK_DURATION_MAX = PomodoroConfig.SHORT_BREAK_DURATION_MAX

        private const val LONG_BREAK_DURATION_DEFAULT = PomodoroConfig.LONG_BREAK_DURATION_DEFAULT
        private const val LONG_BREAK_DURATION_MIN = PomodoroConfig.LONG_BREAK_DURATION_MIN
        private const val LONG_BREAK_DURATION_MAX = PomodoroConfig.LONG_BREAK_DURATION_MAX
    }
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var workDuration: Int = WORK_DURATION_DEFAULT
    private var shortBreakDuration: Int = SHORT_BREAK_DURATION_DEFAULT
    private var longBreakDuration: Int = LONG_BREAK_DURATION_DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Additional code to support edge-to-edge in Android 15
        // Leave this code above the setContentView method
        enableEdgeToEdge()
        // REMOVED: Duplicate setContentView(R.layout.activity_main)
        // setContentView(R.layout.activity_main)

        // Additional code to support edge-to-edge in Android 15
        // Leave this code below the setContentView method

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupView()

        // Additional code to support new permission in Android 13
        // Leave this code on the last line of the method
        requestPostNotificationPermission()
    }


    override fun onStart() {
        super.onStart()
        restorePomodoroConfigValue()
        updateWorkDuration()
        updateShortBreakDuration()
        updateLongBreakDuration()
    }

    override fun onStop() {
        super.onStop()
        savePomodoroConfigValue()
    }


    private fun updateWorkDuration() {
        binding.textViewWorkDuration.text = getString(R.string.duration_value,
            workDuration)
    }
    private fun updateShortBreakDuration() {
        binding.textViewShortDuration.text = getString(R.string.duration_value,
            shortBreakDuration)
    }
    private fun updateLongBreakDuration() {
        binding.textViewLongDuration.text = getString(R.string.duration_value,
            longBreakDuration)
    }

    // --- ADDED: Helper functions for Lifecycle as per PDF (Page 16) ---
    private fun savePomodoroConfigValue() {
        LocalStorage.saveConfig(
            context = this,
            workDuration = workDuration,
            shortBreakDuration = shortBreakDuration,
            longBreakDuration = longBreakDuration
        )
    }

    private fun restorePomodoroConfigValue() {
        val (workDuration, shortBreakDuration, longBreakDuration) = LocalStorage.getConfig(this)
        this.workDuration = workDuration
        this.shortBreakDuration = shortBreakDuration
        this.longBreakDuration = longBreakDuration
    }
    // ----------------------------------------------------------------

    private fun setupView() {

        // updateWorkDuration()
        // updateShortBreakDuration()
        // updateLongBreakDuration()

        binding.buttonAddWorkDuration.setOnClickListener {
            workDuration += 5
            workDuration = workDuration.coerceIn(WORK_DURATION_MIN,
                WORK_DURATION_MAX)
            updateWorkDuration()
        }
        binding.buttonReduceWorkDuration.setOnClickListener {
            workDuration -= 5
            workDuration = workDuration.coerceIn(WORK_DURATION_MIN,
                WORK_DURATION_MAX)
            updateWorkDuration()
        }


        binding.buttonReady.setOnClickListener {

            val config = Config(
                workDuraton = workDuration, // Note: "workDuraton" is based on the PDF's data class definition (Page 32)
                shortBreakDuration = shortBreakDuration,
                longBreakDuration = longBreakDuration
            )
            val intent = TimerActivity.newIntent(
                context = this,
                config = config
            )
            startActivity(intent)
        }
        // ----------------------------------------------------------
    }

    // Check and request post notification permission for Android 13 or higher
    private fun requestPostNotificationPermission() {
        // Skip this permission requesting when running device is lower than Android 13
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return

        val isPermissionDenied = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED

        if (isPermissionDenied) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}