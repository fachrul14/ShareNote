package com.sharenote

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Thread {
            for (progress in 0..100) {
                Thread.sleep(30)
                progressBar.progress = progress
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Sudah login → langsung ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Belum login → ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }.start()
    }
}
