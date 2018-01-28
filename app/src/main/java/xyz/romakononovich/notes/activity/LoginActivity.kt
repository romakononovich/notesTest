package xyz.romakononovich.notes.activity

import android.widget.Toast
import xyz.romakononovich.notes.fingerprint.CryptoUtils
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import xyz.romakononovich.notes.fingerprint.FingerprintUtils
import android.content.Intent
import android.content.Context
import android.preference.PreferenceManager
import android.os.Bundle
import android.content.SharedPreferences
import android.support.v4.os.CancellationSignal
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import xyz.romakononovich.notes.Constants.PIN
import xyz.romakononovich.notes.R


/**
 * Created by romank on 28.01.18.
 */
class LoginActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null
    private var fingerprintHelper: FingerprintHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences!!.contains(PIN)){
            et_pin.isEnabled = false
            et_pin.text.clear()
            btn_login.visibility= View.GONE
        }
        btn_login.setOnClickListener { prepareLogin() }

    }

    override fun onResume() {
        super.onResume()
        if (preferences!!.contains(PIN)) {
            et_pin.isEnabled = false
            et_pin.text.clear()
            btn_login.visibility= View.GONE
            prepareSensor()
        }
    }

    override fun onStop() {
        super.onStop()
        if (fingerprintHelper != null) {
            fingerprintHelper!!.cancel()
        }
    }

    private fun prepareLogin() {

        val pin = et_pin.text.toString()
        if (pin.isNotEmpty()) {
            savePin(pin)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "pin is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePin(pin: String) {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.SensorState.READY, this)) {
            val encoded = CryptoUtils.encode(pin)
            preferences!!.edit().putString(PIN, encoded).apply()
        }
    }

    private fun prepareSensor() {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.SensorState.READY, this)) {
            val cryptoObject = CryptoUtils.cryptoObject
            if (cryptoObject != null) {
                Toast.makeText(this, "use fingerprint to login", Toast.LENGTH_LONG).show()
                fingerprintHelper = FingerprintHelper(this)
                fingerprintHelper!!.startAuth(cryptoObject)
            } else {
                preferences!!.edit().remove(PIN).apply()
                Toast.makeText(this, "new fingerprint enrolled. enter pin again", Toast.LENGTH_SHORT).show()
            }

        }
    }


    inner class FingerprintHelper internal constructor(private val mContext: Context) : FingerprintManagerCompat.AuthenticationCallback() {
        private var mCancellationSignal: CancellationSignal? = null

        internal fun startAuth(cryptoObject: FingerprintManagerCompat.CryptoObject) {
            mCancellationSignal = CancellationSignal()
            val manager = FingerprintManagerCompat.from(mContext)
            manager.authenticate(cryptoObject, 0, mCancellationSignal, this, null)
        }

        internal fun cancel() {
            if (mCancellationSignal != null) {
                mCancellationSignal!!.cancel()
            }
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
            Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            val cipher = result!!.cryptoObject.cipher
            val encoded = preferences!!.getString(PIN, null)
            val decoded = CryptoUtils.decode(encoded!!, cipher)
            et_pin.setText(decoded)
            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }

        override fun onAuthenticationFailed() {
            Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show()
        }

    }


}