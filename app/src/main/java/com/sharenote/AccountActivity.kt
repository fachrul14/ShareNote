package com.sharenote

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sharenote.databinding.ActivityAccountBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            dbRef = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(uid)

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value?.toString() ?: "-"
                    val email = auth.currentUser?.email ?: "-"
                    binding.tvName.text = name
                    binding.tvEmail.text = email
                }

                override fun onCancelled(error: DatabaseError) {
                    showCustomToast("Gagal memuat data")
                }
            })
        }

        // Ubah Nama
        binding.menuChangeName.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_name, null)
            val etNewName = dialogView.findViewById<EditText>(R.id.etNewName)

            AlertDialog.Builder(this)
                .setTitle("Ubah Nama")
                .setView(dialogView)
                .setPositiveButton("Simpan") { _, _ ->
                    val newName = etNewName.text.toString().trim()
                    if (newName.isEmpty()) {
                        showCustomToast("Nama tidak boleh kosong")
                    } else if (uid != null) {
                        dbRef.child("name").setValue(newName)
                            .addOnSuccessListener {
                                binding.tvName.text = newName
                                showCustomToast("Nama berhasil diubah")
                            }
                            .addOnFailureListener {
                                showCustomToast("Gagal mengubah nama")
                            }
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Ganti Password
        binding.menuChangePassword.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
            val etOldPassword = dialogView.findViewById<EditText>(R.id.etOldPassword)
            val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
            val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

            AlertDialog.Builder(this)
                .setTitle("Ganti Password")
                .setView(dialogView)
                .setPositiveButton("Ganti") { _, _ ->
                    val oldPass = etOldPassword.text.toString()
                    val newPass = etNewPassword.text.toString()
                    val confirmPass = etConfirmPassword.text.toString()

                    if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                        showCustomToast("Semua kolom harus diisi")
                        return@setPositiveButton
                    }

                    if (newPass != confirmPass) {
                        showCustomToast("Password baru tidak cocok")
                        return@setPositiveButton
                    }

                    val user = auth.currentUser
                    val email = user?.email

                    if (user != null && email != null) {
                        val credential = EmailAuthProvider.getCredential(email, oldPass)
                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                user.updatePassword(newPass)
                                    .addOnSuccessListener {
                                        showCustomToast("Password berhasil diganti")
                                    }
                                    .addOnFailureListener {
                                        showCustomToast("Gagal mengganti password")
                                    }
                            }
                            .addOnFailureListener {
                                showCustomToast("Password lama salah")
                            }
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Logout
        binding.menuLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    showCustomToast("Berhasil logout")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // BOTTOM NAVIGATION
        val bottomNav = binding.bottomNavigationView
        bottomNav.selectedItemId = R.id.nav_account

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddNoteActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_account -> true
                else -> false
            }
        }
    }

    // Custom Toast Function
    private fun showCustomToast(message: String) {
        val layoutInflater = layoutInflater
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
