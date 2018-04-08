package xyz.romakononovich.notes.activity

import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.toolbar.*
import xyz.romakononovich.notes.BaseActivity
import xyz.romakononovich.notes.FORMAT_DATE
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.TIMESTAMP
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by romank on 27.01.18.
 */
class EditNoteActivity : BaseActivity() {
    private var timestamp: Long = 0
    private val date = Date()

    override fun getContentResId(): Int {
        return R.layout.activity_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        fillView()
    }

    private fun initToolbar() {
        title = resources.getString(R.string.edit_note_title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.ic_close)
    }

    private fun fillView() {
        if (intent.extras != null) {
            timestamp = intent.extras.getLong(TIMESTAMP)
            val note = getNoteFromDB(timestamp)
            etTitle.setText(note.title)
            etNote.setText(note.note)
            tvDate.text = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(Date(note.timestamp))
        }
    }

    private fun getNoteFromDB(timestamp: Long): Note {
        lateinit var note: Note
        realm.executeTransaction {
            note = realm.where(Note::class.java).equalTo(TIMESTAMP, timestamp).findFirstAsync()
        }
        return note
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        val itemOk = menu.findItem(R.id.action_ok)
        itemOk.setOnMenuItemClickListener {
            val note = Note()
            note.title = etTitle.text.toString()
            note.note = etNote.text.toString()
            note.timestamp = date.time
            realm.executeTransaction({
                realm.where(Note::class.java).equalTo(TIMESTAMP, timestamp).findAll().deleteFirstFromRealm()
                realm.insert(note)
            })
            onBackPressed()
            true
        }
        return true

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}