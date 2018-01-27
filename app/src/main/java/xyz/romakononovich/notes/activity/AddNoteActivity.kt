package xyz.romakononovich.notes.activity

import android.os.Bundle
import android.view.Menu
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.toolbar.*
import xyz.romakononovich.notes.BaseActivity
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by romank on 27.01.18.
 */
class AddNoteActivity : BaseActivity() {
    var realm: Realm = Realm.getDefaultInstance()
    private val date = Date()

    override fun getContentResId(): Int {
        return R.layout.activity_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        tv_date.text = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(date)
    }

    private fun initToolbar() {
        title = resources.getString(R.string.add_note_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        val itemOk = menu.findItem(R.id.action_ok)
        itemOk.setOnMenuItemClickListener {
            val note = Note()
            note.title = et_title.text.toString()
            note.note = et_note.text.toString()
            note.timestamp = date.time
            realm.executeTransaction({
                realm.insert(note)})
                onBackPressed()
                true
            }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}