package xyz.romakononovich.notes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_base.*

/**
 * Created by romank on 27.01.18.
 */
abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        layoutInflater.inflate(getContentResId(),container)
        initToolbar()
    }
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
    }
    abstract fun getContentResId(): Int

}