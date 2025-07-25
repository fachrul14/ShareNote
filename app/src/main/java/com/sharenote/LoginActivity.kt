package com.sharenote

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sharenote.databinding.ActivityLoginBinding
import android.content.res.ColorStateList

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val isFilled = email.isNotEmpty() && password.isNotEmpty()

                binding.btnLogin.isEnabled = isFilled
                if (isFilled) {
                    binding.btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0381CE")))
                    binding.btnLogin.setTextColor(Color.WHITE)
                } else {
                    binding.btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")))
                    binding.btnLogin.setTextColor(Color.BLACK)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etEmail.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login gagal: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
