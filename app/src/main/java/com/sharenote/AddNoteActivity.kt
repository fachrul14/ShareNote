package com.sharenote

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sharenote.databinding.ActivityAddNoteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        // Bottom Navigation
        val bottomNav = binding.bottomNavigationView
        bottomNav.selectedItemId = R.id.nav_add // Set tab aktif ke Tambah

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true // Sudah di halaman ini
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val courseName = binding.etCourseName.text.toString().trim()
        val driveLink = binding.etDriveLink.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || courseName.isEmpty() || driveLink.isEmpty()) {
            showCustomToast("Harap isi semua kolom")
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val dbRef = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("notes")
            .child(uid)

        val noteId = dbRef.push().key!!  // Generate unique key

        val noteData = Note(
            id = noteId,
            title = title,
            description = description,
            courseName = courseName,
            driveLink = driveLink
        )

        dbRef.child(noteId).setValue(noteData)
            .addOnSuccessListener {
                showCustomToast("Catatan berhasil disimpan")
                finish()
            }
            .addOnFailureListener {
                showCustomToast("Gagal menyimpan catatan")
            }
    }

    private fun showCustomToast(message: String) {
        val layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, null)
        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
