package xyz.romakononovich.notes.fingerprint

import android.os.Build
import android.annotation.TargetApi
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.app.KeyguardManager
import android.content.Context


/**
 * Created by romank on 28.01.18.
 */
object FingerprintUtils {

    enum class SensorState {
        NOT_SUPPORTED,
        NOT_BLOCKED,
        NO_FINGERPRINTS,
        READY
    }

    private fun checkFingerprintCompatibility(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).isHardwareDetected
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun checkSensorState(context: Context): SensorState {
        if (checkFingerprintCompatibility(context)) {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (!keyguardManager.isKeyguardSecure) {
                return SensorState.NOT_BLOCKED
            }

            return if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
                SensorState.NO_FINGERPRINTS
            } else SensorState.READY

        } else {
            return SensorState.NOT_SUPPORTED
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun isSensorStateAt(state: SensorState, context: Context): Boolean {
        return checkSensorState(context) == state
    }
}