package com.sharenote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sharenote.databinding.ActivityAddNoteBinding

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var auth: FirebaseAuth
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
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
        binding.btnSave.setOnClickListener {
            updateNote()
        }
    }

    private fun updateNote() {
        val uid = auth.currentUser?.uid ?: return
        val id = noteId

        if (id == null) {
            Toast.makeText(this, "Data catatan tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTitle = binding.etTitle.text.toString().trim()
        val updatedDescription = binding.etDescription.text.toString().trim()
        val updatedCourseName = binding.etCourseName.text.toString().trim()
        val updatedDriveLink = binding.etDriveLink.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedCourseName.isEmpty() || updatedDriveLink.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui catatan", Toast.LENGTH_SHORT).show()
            }
    }
}
 