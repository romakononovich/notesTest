package xyz.romakononovich.notes.activity

import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.toolbar.*
import xyz.romakononovich.notes.BaseActivity
import xyz.romakononovich.notes.FORMAT_DATE
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by romank on 27.01.18.
 */
class AddNoteActivity : BaseActivity() {
    private val date = Date()


    override fun getContentResId(): Int {
        return R.layout.activity_note
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = resources.getString(R.string.add_note_title)
            toolbar.setNavigationIcon(R.drawable.ic_close)
        }
        tvDate.text = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(date)
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
                realm.insert(note)
            })
            finish()
            true

        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}