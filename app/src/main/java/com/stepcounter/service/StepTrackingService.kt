package com.stepcounter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.stepcounter.R
import com.stepcounter.core.location.LocationTracker
import com.stepcounter.core.sensor.AccelerometerProcessor
import com.stepcounter.core.sensor.PhonePositionDetector
import com.stepcounter.core.sensor.StepDetector
import com.stepcounter.core.session.SessionManager
import com.stepcounter.data.repository.StepRepository
import com.stepcounter.domain.model.WalkSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class StepTrackingService : android.app.Service(), SensorEventListener {

    @Inject lateinit var sensorManager: SensorManager
    @Inject lateinit var accelerometerProcessor: AccelerometerProcessor
    @Inject lateinit var stepDetector: StepDetector
    @Inject lateinit var phonePositionDetector: PhonePositionDetector
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var stepRepository: StepRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var accelerometer: Sensor? = null
    private var isTracking = false
    private var currentSteps = 0

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerProcessor.processEvent(event)
                phonePositionDetector.analyze(event)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        if (isTracking) return
        isTracking = true

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                200000
            )
        }

        serviceScope.launch {
            accelerometerProcessor.filteredSignal.collectLatest { filteredValue ->
                val positionMultiplier = phonePositionDetector.getPositionMultiplier()
                val timestamp = System.currentTimeMillis()
                stepDetector.processSignal(filteredValue, timestamp, positionMultiplier)
            }
        }

        serviceScope.launch {
            stepDetector.stepEvents.collectLatest { stepEvent ->
                currentSteps++
                sessionManager.addStep(stepEvent.timestamp)

                val currentTime = System.currentTimeMillis()
                if (sessionManager.shouldActivateGps(currentTime)) {
                    locationTracker.startTracking()
                }
            }
        }
    }

    private fun stopTracking() {
        isTracking = false
        sensorManager.unregisterListener(this)
        locationTracker.stopTracking()

        sessionManager.getCurrentSession()?.let { session ->
            session.endTime = System.currentTimeMillis()
            serviceScope.launch {
                stepRepository.saveSession(session)
            }
        }

        sessionManager.reset()
        currentSteps = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            accelerometerProcessor.processEvent(it)
            phonePositionDetector.analyze(it)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your steps in the background"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Counter")
            .setContentText("Tracking steps: $currentSteps")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private const val CHANNEL_ID = "step_tracking_channel"
        private const val NOTIFICATION_ID = 1
    }
}
