package xyz.romakononovich.notes.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import xyz.romakononovich.notes.BaseActivity
import xyz.romakononovich.notes.Constants.TIMESTAMP
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.adapter.RvAdapter
import xyz.romakononovich.notes.models.Note
import java.util.*

/**
 * Created by romank on 27.01.18.
 */

class MainActivity : BaseActivity() {
    private lateinit var rvAdapter: RvAdapter
    var realm: Realm = Realm.getDefaultInstance()
    lateinit var listNotes: ArrayList<Note>

    override fun getContentResId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        initSwipe()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
    }

    private fun initView() {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(applicationContext)
        rvAdapter = RvAdapter(getNotesFromDB())
        rv.adapter = rvAdapter
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }
        })
        fab.setOnClickListener {
            startActivity(Intent(applicationContext, AddNoteActivity::class.java))
        }
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    rvAdapter.removeItem(position)
                    realm.executeTransaction {
                        realm.where(Note::class.java).equalTo(TIMESTAMP, listNotes[position].timestamp).findAll().deleteFirstFromRealm()
                    }
                    listNotes = getNotesFromDB()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }

    private fun getNotesFromDB(): ArrayList<Note> {
        val list: ArrayList<Note> = ArrayList()
        lateinit var realmNotes: RealmResults<Note>
        realm.executeTransaction {
            realmNotes = realm.where(Note::class.java).sort(TIMESTAMP, Sort.DESCENDING).findAll()
            if (!realmNotes.isEmpty()) {
                list.addAll(realmNotes.toTypedArray())
            }
        }
        listNotes = list
        return list

    }

    override fun onResume() {
        super.onResume()
        rvAdapter.refreshListNotes(getNotesFromDB())

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}