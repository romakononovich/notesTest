package xyz.romakononovich.notes.activity

import xyz.romakononovich.notes.fingerprint.CryptoUtils
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import xyz.romakononovich.notes.fingerprint.FingerprintUtils
import android.content.Context
import android.preference.PreferenceManager
import android.os.Bundle
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.os.CancellationSignal
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import xyz.romakononovich.notes.Constants.PIN
import xyz.romakononovich.notes.R
import android.widget.ImageView
import xyz.romakononovich.notes.BaseActivity


/**
 * Created by romank on 28.01.18.
 */
class LoginActivity : BaseActivity() {
    override fun getContentResId(): Int {
        return R.layout.activity_login
    }

    private lateinit var preferences: SharedPreferences
    private lateinit var fingerprintHelper: FingerprintHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar {
            setDisplayShowTitleEnabled(false)
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.contains(PIN)) {
            et_pin.isEnabled = false
            et_pin.text.clear()
            btn_login.visibility = View.GONE
            finger.visibility = View.VISIBLE
        }
        btn_login.setOnClickListener { prepareLogin() }

    }

    override fun onResume() {
        super.onResume()
        if (preferences.contains(PIN)) {
            et_pin.isEnabled = false
            et_pin.text.clear()
            btn_login.visibility = View.GONE
            finger.visibility = View.VISIBLE
            prepareSensor()
        }
    }

    override fun onStop() {
        super.onStop()
        fingerprintHelper.cancel()
    }

    private fun prepareLogin() {

        val pin = et_pin.text.toString()
        if (pin.isNotEmpty()) {
            savePin(pin)
            animation(finger, true)
            if (intent.extras != null && intent.extras.getBoolean("isWidget")) {
                startActivity(AddNoteActivity::class.java)
            } else {
                startActivity(MainActivity::class.java)
            }
        } else {
            toast(resources.getString(R.string.login_pin_empty))
        }
    }

    private fun savePin(pin: String) {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.SensorState.READY, this)) {
            val encoded = CryptoUtils.encode(pin)
            preferences.edit().putString(PIN, encoded).apply()
        }
    }

    private fun prepareSensor() {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.SensorState.READY, this)) {
            val cryptoObject = CryptoUtils.cryptoObject
            if (cryptoObject != null) {
                toast(resources.getString(R.string.login_use_fingerprint))
                fingerprintHelper = FingerprintHelper(this)
                fingerprintHelper.startAuth(cryptoObject)
            } else {
                preferences.edit().remove(PIN).apply()
                toast(resources.getString(R.string.login_new_fingerprint))
            }

        }
    }


    inner class FingerprintHelper internal constructor(private val context: Context) : FingerprintManagerCompat.AuthenticationCallback() {
        private lateinit var cancellationSignal: CancellationSignal

        internal fun startAuth(cryptoObject: FingerprintManagerCompat.CryptoObject) {
            cancellationSignal = CancellationSignal()
            val manager = FingerprintManagerCompat.from(context)
            manager.authenticate(cryptoObject, 0, cancellationSignal, this, null)
        }

        internal fun cancel() {
            cancellationSignal.cancel()
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
            toast(errString)
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
            toast(helpString)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            val cipher = result!!.cryptoObject.cipher
            val encoded = preferences.getString(PIN, null)
            val decoded = CryptoUtils.decode(encoded, cipher)
            et_pin.setText(decoded)
            toast(resources.getString(R.string.login_success))
            animation(finger, true)
            if (intent.extras != null && intent.extras.getBoolean("isWidget")) {
                startActivity(AddNoteActivity::class.java)
            } else {
                startActivity(MainActivity::class.java)
            }

        }


        override fun onAuthenticationFailed() {
            animation(finger, false)
            toast(resources.getString(R.string.login_try_again))
        }
    }

    fun animation(view: View, isOk: Boolean) {
        val v: ImageView = view as ImageView
        when {
            isOk -> v.setImageResource(R.drawable.finger_print_anim_ok)
            !isOk -> v.setImageResource(R.drawable.finger_print_anim_error)
        }
        val d: Drawable = v.drawable
        if (d is AnimatedVectorDrawableCompat) {
            val avd: AnimatedVectorDrawableCompat = d
            avd.start()
        } else if (d is AnimatedVectorDrawable) {
            val avd: AnimatedVectorDrawable = d
            avd.start()
        }


    }
}