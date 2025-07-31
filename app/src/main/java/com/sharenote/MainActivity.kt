package com.sharenote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sharenote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var noteList: MutableList<Note> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(
            "https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Ambil nama user untuk greeting
        val uid = currentUser.uid
        database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                binding.tvGreeting.text = "Halo, ${name ?: "pengguna"}!"
            }

            override fun onCancelled(error: DatabaseError) {
                showCustomToast("Gagal mengambil data user")
            }
        })

        // Inisialisasi RecyclerView dan adapter
        setupRecyclerView()

        // SearchView listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        // Bottom Navigation listener
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true // Sudah di halaman ini
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddNoteActivity::class.java))
                    true
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotesFromFirebase()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(this)
        binding.rvNotes.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = noteAdapter
        }
    }

    private fun loadNotesFromFirebase() {
        val currentUser = auth.currentUser ?: return

        database.child("notes").child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    noteList.clear()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(Note::class.java)
                        if (note != null) {
                            note.id = noteSnapshot.key ?: ""
                            noteList.add(note)
                        }
                    }
                    noteAdapter.setNotes(noteList)
                }

                override fun onCancelled(error: DatabaseError) {
                    showCustomToast("Gagal mengambil data")
                }
            })
    }

    private fun filterNotes(query: String?) {
        val filteredList = ArrayList<Note>()

        if (!query.isNullOrEmpty()) {
            for (note in noteList) {
                if (
                    (note.title ?: "").contains(query, ignoreCase = true) ||
                    (note.courseName ?: "").contains(query, ignoreCase = true)
                ) {
                    filteredList.add(note)
                }
            }
        } else {
            filteredList.addAll(noteList)
        }

        noteAdapter.updateList(filteredList)
    }

    // ✅ Custom Toast
    private fun showCustomToast(message: String) {
        val layoutInflater = layoutInflater
        val layout: View = layoutInflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
