package xyz.romakononovich.notes.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_note.view.*
import xyz.romakononovich.notes.Constants.FORMAT_DATE
import xyz.romakononovich.notes.Constants.TIMESTAMP
import xyz.romakononovich.notes.activity.EditNoteActivity
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by romank on 26.01.18.
 */
class RvAdapter(private val listNotes: ArrayList<Note>) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_note, parent, false)
        val holder = ViewHolder(view)
        view.setOnClickListener {
            val intent = Intent(view.context, EditNoteActivity::class.java)
            intent.putExtra(TIMESTAMP, listNotes[holder.adapterPosition].timestamp)
            view.context.startActivity(intent)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    fun refreshListNotes(list: ArrayList<Note>) {
        listNotes.clear()
        listNotes.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(listNotes[position])
        holder.noteTitle
    }

    fun removeItem(position: Int) {
        listNotes.remove(listNotes[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listNotes.size)


    }

    class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        var noteTitle = itemView.tv_title
        fun bindItems(note: Note) {
            v.tv_title.text = note.title
            v.tv_note.text = note.note
            v.tv_date.text = setDate(note.timestamp)
        }


        private fun setDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

    }
}