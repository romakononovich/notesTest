package xyz.romakononovich.notes.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_note.view.*
import xyz.romakononovich.notes.FORMAT_DATE
import xyz.romakononovich.notes.R
import xyz.romakononovich.notes.models.Note
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by romank on 26.01.18.
 */
class RvAdapter(
        context: Context,
        private val itemClickListener: RvAdapter.ItemClickListener,
        notes: List<Note>
) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    private val notes: MutableList<Note> = ArrayList(notes)
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = inflater.inflate(R.layout.item_note, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun getItemCount() = notes.size

    fun refreshListNotes(list: ArrayList<Note>) {
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(notes[position])
    }

    fun removeItem(position: Int) {
        notes.remove(notes[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notes.size)
    }

    class ViewHolder(
            view: View,
            itemClickListener: RvAdapter.ItemClickListener
    ) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { itemClickListener.onItemClicked(this) }
        }

        lateinit var note: Note
            private set

        fun bindItems(note: Note) {
            this.note = note
            itemView.tvTitle.text = note.title
            itemView.tvNote.text = note.note
            itemView.tvDate.text = setDate(note.timestamp)
        }


        private fun setDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

    }

    interface ItemClickListener {
        fun onItemClicked(viewHolder: ViewHolder)
    }
}