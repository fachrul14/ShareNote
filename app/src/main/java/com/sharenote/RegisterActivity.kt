package com.sharenote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sharenote.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Cek apakah semua form terisi
        fun checkFields() {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val allFilled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()

            binding.btnRegister.isEnabled = allFilled

            if (allFilled) {
                binding.btnRegister.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.blue_button)
                binding.btnRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.btnRegister.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.gray_button)
                binding.btnRegister.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }

        // TextWatcher untuk validasi real-time
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        // Tambahkan listener ke EditText
        binding.etName.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)

        // Tombol Register
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val database = FirebaseDatabase.getInstance("https://sharenoteapp-16f08-default-rtdb.asia-southeast1.firebasedatabase.app").reference



                            val userMap = HashMap<String, Any>()
                            userMap["name"] = name
                            userMap["email"] = email

                            database.child("users").child(uid).setValue(userMap)
                                .addOnSuccessListener {
                                    runOnUiThread {
                                        Toast.makeText(
                                            this,
                                            "Register berhasil",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        auth.signOut()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    }
                                }

                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Gagal menyimpan data user: ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(this, "UID tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Gagal register: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Teks "Login di sini"
        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
