package com.sharenote

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
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
                        showCustomToast("Login berhasil")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        showCustomToast("Login gagal: ${it.exception?.message}")
                    }
                }
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
}
