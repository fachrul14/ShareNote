package com.sharenote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val context: Context) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val notes = mutableListOf<Note>()

    fun setNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)

        // Menampilkan popup menu saat card ditekan
        holder.itemView.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.menuInflater.inflate(R.menu.note_popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_view -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.driveLink))
                        context.startActivity(intent)
                        true
                    }
                    R.id.menu_edit -> {
                        val intent = Intent(context, EditNoteActivity::class.java).apply {
                            putExtra("noteId", note.id)
                            putExtra("courseName", note.courseName)
                            putExtra("title", note.title)
                            putExtra("description", note.description)
                            putExtra("driveLink", note.driveLink)
                        }
                        context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

        fun bind(note: Note) {
            tvCourse.text = note.courseName ?: "Tanpa Mata Kuliah"
            tvTitle.text = note.title ?: "Tanpa Judul"
            tvDescription.text = note.description ?: "Tanpa Deskripsi"
        }
    }
}
