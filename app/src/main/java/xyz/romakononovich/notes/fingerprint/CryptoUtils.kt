package xyz.romakononovich.notes.fingerprint

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.os.Build
import android.annotation.TargetApi
import android.annotation.SuppressLint
import android.support.annotation.Nullable
import android.util.Base64
import android.util.Log
import xyz.romakononovich.notes.KEY_ALIAS
import xyz.romakononovich.notes.KEY_STORE
import xyz.romakononovich.notes.TRANSFORMATION
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.spec.InvalidKeySpecException
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource


/**
 * Created by romank on 28.01.18.
 */
@TargetApi(Build.VERSION_CODES.M)
object CryptoUtils {

    private val TAG = javaClass.simpleName
    private lateinit var sKeyStore: KeyStore
    private lateinit var sKeyPairGenerator: KeyPairGenerator
    private lateinit var sCipher: Cipher



    private val keyStore: Boolean
        get() {
            try {
                sKeyStore = KeyStore.getInstance(KEY_STORE)
                sKeyStore.load(null)
                return true
            } catch (e: KeyStoreException) {
                Log.d(TAG, e.printStackTrace().toString())
            } catch (e: IOException) {
                Log.d(TAG, e.printStackTrace().toString())
            } catch (e: NoSuchAlgorithmException) {
                Log.d(TAG, e.printStackTrace().toString())
            } catch (e: CertificateException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

            return false
        }


    private val keyPairGenerator: Boolean
        @TargetApi(Build.VERSION_CODES.M)
        get() {
            try {
                sKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE)
                return true
            } catch (e: NoSuchAlgorithmException) {
                Log.d(TAG, e.printStackTrace().toString())
            } catch (e: NoSuchProviderException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

            return false
        }


    private val cipher: Boolean
        @SuppressLint("GetInstance")
        get() {
            try {
                sCipher = Cipher.getInstance(TRANSFORMATION)
                return true
            } catch (e: NoSuchAlgorithmException) {
                Log.d(TAG, e.printStackTrace().toString())
            } catch (e: NoSuchPaddingException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

            return false
        }

    private val key: Boolean
        get() {
            try {
                return sKeyStore.containsAlias(KEY_ALIAS) || generateNewKey()
            } catch (e: KeyStoreException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

            return false

        }

    val cryptoObject: FingerprintManagerCompat.CryptoObject?
        @Nullable
        get() = if (prepare() && initCipher(Cipher.DECRYPT_MODE)) {
            FingerprintManagerCompat.CryptoObject(sCipher)
        } else null

    fun encode(inputString: String): String? {
        try {
            if (prepare() && initCipher(Cipher.ENCRYPT_MODE)) {
                val bytes = sCipher.doFinal(inputString.toByteArray())
                return Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: IllegalBlockSizeException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: BadPaddingException) {
            Log.d(TAG, e.printStackTrace().toString())
        }

        return null
    }


    fun decode(encodedString: String, cipher: Cipher): String? {
        try {
            val bytes = Base64.decode(encodedString, Base64.NO_WRAP)
            return String(cipher.doFinal(bytes))
        } catch (exception: IllegalBlockSizeException) {
            exception.printStackTrace()
        } catch (exception: BadPaddingException) {
            exception.printStackTrace()
        }

        return null
    }

    private fun prepare(): Boolean {
        return keyStore && cipher && key
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun generateNewKey(): Boolean {

        if (keyPairGenerator) {

            try {
                sKeyPairGenerator.initialize(
                        KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                                .setUserAuthenticationRequired(true)
                                .build())
                sKeyPairGenerator.generateKeyPair()
                return true
            } catch (e: InvalidAlgorithmParameterException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

        }
        return false
    }


    private fun initCipher(mode: Int): Boolean {
        try {
            sKeyStore.load(null)

            when (mode) {
                Cipher.ENCRYPT_MODE -> initEncodeCipher(mode)

                Cipher.DECRYPT_MODE -> initDecodeCipher(mode)
                else -> return false //this cipher is only for encode\decode
            }
            return true

        } catch (exception: KeyPermanentlyInvalidatedException) {
            deleteInvalidKey()

        } catch (e: KeyStoreException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: CertificateException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: UnrecoverableKeyException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: IOException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: InvalidKeyException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: InvalidKeySpecException) {
            Log.d(TAG, e.printStackTrace().toString())
        } catch (e: InvalidAlgorithmParameterException) {
            Log.d(TAG, e.printStackTrace().toString())
        }

        return false
    }

    @Throws(KeyStoreException::class, NoSuchAlgorithmException::class, UnrecoverableKeyException::class, InvalidKeyException::class)
    private fun initDecodeCipher(mode: Int) {
        val key = sKeyStore.getKey(KEY_ALIAS, null) as PrivateKey
        sCipher.init(mode, key)
    }

    @Throws(KeyStoreException::class, InvalidKeySpecException::class, NoSuchAlgorithmException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class)
    private fun initEncodeCipher(mode: Int) {
        val key = sKeyStore.getCertificate(KEY_ALIAS).publicKey

        // workaround for using public key
        // from https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.html
        val unrestricted = KeyFactory.getInstance(key.algorithm).generatePublic(X509EncodedKeySpec(key.encoded))
        // from https://code.google.com/p/android/issues/detail?id=197719
        val spec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)

        sCipher.init(mode, unrestricted, spec)
    }

    private fun deleteInvalidKey() {
        if (keyStore) {
            try {
                sKeyStore.deleteEntry(KEY_ALIAS)
            } catch (e: KeyStoreException) {
                Log.d(TAG, e.printStackTrace().toString())
            }

        }
    }


}