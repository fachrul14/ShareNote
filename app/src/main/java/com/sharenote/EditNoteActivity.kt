package com.sharenote

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sharenote.databinding.ActivityEditNoteBinding

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var auth: FirebaseAuth
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Ambil data dari intent
        noteId = intent.getStringExtra("noteId")
        val courseName = intent.getStringExtra("courseName")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val driveLink = intent.getStringExtra("driveLink")

        // Set data ke form
        binding.etCourseName.setText(courseName)
        binding.etTitle.setText(title)
        binding.etDescription.setText(description)
        binding.etDriveLink.setText(driveLink)

        // Tombol Simpan
        binding.btnUpdate.setOnClickListener {
            updateNote()
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun updateNote() {
        val uid = auth.currentUser?.uid ?: return
        val id = noteId

        if (id == null) {
            showCustomToast("Data catatan tidak ditemukan")
            return
        }

        val updatedTitle = binding.etTitle.text.toString().trim()
        val updatedDescription = binding.etDescription.text.toString().trim()
        val updatedCourseName = binding.etCourseName.text.toString().trim()
        val updatedDriveLink = binding.etDriveLink.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedCourseName.isEmpty() || updatedDriveLink.isEmpty()) {
            showCustomToast("Harap isi semua kolom")
            return
        }

        val updatedNote = Note(
            id = id,
            title = updatedTitle,
            description = updatedDescription,
            courseName = updatedCourseName,
            driveLink = updatedDriveLink
        )

        val dbRef = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("notes")
            .child(uid)
            .child(id)

        dbRef.setValue(updatedNote)
            .addOnSuccessListener {
                showCustomToast("Catatan berhasil diperbarui")
                finish()
            }
            .addOnFailureListener {
                showCustomToast("Gagal memperbarui catatan")
            }
    }
}
