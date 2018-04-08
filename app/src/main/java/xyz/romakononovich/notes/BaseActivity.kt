package xyz.romakononovich.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_base.*

/**
 * Created by romank on 27.01.18.
 */
abstract class BaseActivity : AppCompatActivity() {
    val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        layoutInflater.inflate(getContentResId(), container)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
    }

    abstract fun getContentResId(): Int

    fun Context.toast(message: CharSequence): Toast = Toast
            .makeText(this, message, Toast.LENGTH_SHORT)
            .apply {
                view.findViewById<TextView>(android.R.id.message).gravity = Gravity.CENTER
                show()
            }

    fun Context.startActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

}