package com.example.myapplication04

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Oculta la barra de título por defecto
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Auto-Login: Si el usuario ya está autenticado, redirigir directamente
        if (auth.currentUser != null) {
            irAMainActivity()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        fun mostrarCargando(cargando: Boolean) {
            progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !cargando
            btnRegister.isEnabled = !cargando
        }

        // Botón Iniciar Sesión
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mostrarCargando(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    mostrarCargando(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                        irAMainActivity()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al iniciar sesión: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Botón Registrarse
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese correo y contraseña para registrarse", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mostrarCargando(true)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    mostrarCargando(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Usuario registrado e inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                        irAMainActivity()
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Recuperar contraseña
        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Ingrese su correo en el campo de texto para enviarle el enlace de recuperación", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mostrarCargando(true)
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    mostrarCargando(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Se ha enviado un correo de recuperación a $email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
