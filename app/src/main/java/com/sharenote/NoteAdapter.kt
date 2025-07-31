package com.sharenote

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

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
                    R.id.menu_delete -> {
                        showDeleteConfirmationDialog(note, position)
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

    fun updateList(newList: List<Note>) {
        notes.clear()
        notes.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(note: Note, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Hapus Catatan")
        builder.setMessage("Apakah Anda yakin ingin menghapus catatan ini?")
        builder.setPositiveButton("Hapus") { _, _ ->
            deleteNoteFromFirebase(note)
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun deleteNoteFromFirebase(note: Note) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val noteRef = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("notes")
                .child(userId)
                .child(note.id ?: "")

            noteRef.removeValue()
                .addOnSuccessListener {
                    val index = notes.indexOfFirst { it.id == note.id }
                    if (index != -1) {
                        notes.removeAt(index)
                        notifyItemRemoved(index)
                        notifyItemRangeChanged(index, notes.size)
                    }
                    showCustomToast("Catatan berhasil dihapus")
                }
                .addOnFailureListener { e ->
                    showCustomToast("Gagal menghapus catatan: ${e.message}")
                }
        } else {
            showCustomToast("User tidak ditemukan")
        }
    }

    private fun showCustomToast(message: String) {
        val layoutInflater = LayoutInflater.from(context)
        val layout: View = layoutInflater.inflate(R.layout.custom_toast, null)

        val toastText: TextView = layout.findViewById(R.id.toast_text)
        toastText.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
