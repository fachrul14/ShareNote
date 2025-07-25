package com.sharenote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sharenote.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val courseName = binding.etCourseName.text.toString().trim()
        val driveLink = binding.etDriveLink.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || courseName.isEmpty() || driveLink.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val dbRef = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("notes")
            .child(uid)

        val noteId = dbRef.push().key!!  // Generate unique key

        // Buat objek Note lengkap termasuk ID
        val noteData = Note(
            id = noteId,
            title = title,
            description = description,
            courseName = courseName,
            driveLink = driveLink
        )

        dbRef.child(noteId).setValue(noteData)
            .addOnSuccessListener {
                Toast.makeText(this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
            }
    }
}
