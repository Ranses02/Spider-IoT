package com.example.myapplication04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Oculta la barra de título por defecto
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvUserEmailTitle = findViewById<TextView>(R.id.tvUserEmailTitle)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val tvUserUid = findViewById<TextView>(R.id.tvUserUid)
        val btnReturnDashboard = findViewById<Button>(R.id.btnReturnDashboard)
        val btnLogoutProfile = findViewById<Button>(R.id.btnLogoutProfile)

        val email = user?.email ?: "usuario@desconocido.com"
        val uid = user?.uid ?: "Sin UID registrado"

        tvUserEmailTitle.text = email
        tvUserEmail.text = email
        tvUserUid.text = uid

        btnBack.setOnClickListener {
            finish()
        }

        btnReturnDashboard.setOnClickListener {
            finish()
        }

        btnLogoutProfile.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
