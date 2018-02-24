package xyz.romakononovich.notes.activity

import android.os.Bundle
import android.view.Menu
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.toolbar.*
import xyz.romakononovich.notes.BaseActivity
import xyz.romakononovich.notes.Constants.FORMAT_DATE
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*



/**
 * Created by romank on 27.01.18.
 */
class AddNoteActivity : BaseActivity() {
    val realm: Realm = Realm.getDefaultInstance()
    private val date = Date()


    override fun getContentResId(): Int {
        return R.layout.activity_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = resources.getString(R.string.add_note_title)
            toolbar.setNavigationIcon(R.drawable.ic_close)
        }
        tv_date.text = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(date)
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
            finish()
                true

            }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}