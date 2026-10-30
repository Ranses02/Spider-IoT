package com.example.myapplication04

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Referencia directa e explícita a la URL de tu Firebase Realtime Database
        database = try {
            FirebaseDatabase.getInstance("https://myapplication01-b8b93a24-default-rtdb.firebaseio.com").reference
        } catch (_: Exception) {
            FirebaseDatabase.getInstance().reference
        }

        val tvHumedad = findViewById<TextView>(R.id.tvHumedad)
        val tvTemperatura = findViewById<TextView>(R.id.tvTemperatura)
        val tvLuz = findViewById<TextView>(R.id.tvLuz)
        val tvEstadoBomba = findViewById<TextView>(R.id.tvEstadoBomba)
        val switchBomba = findViewById<MaterialSwitch>(R.id.switchBomba)
        val btnProfileHeader = findViewById<ImageButton>(R.id.btnProfileHeader)

        btnProfileHeader.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val sensoresRef = database.child("sensores")

        // 1. Inicializador automático de sensores:
        // Si el nodo "sensores" no existe aún en la base de datos de Firebase, la app lo crea automáticamente
        sensoresRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val datosIniciales = mapOf(
                        "humedad" to "65",
                        "temperatura" to "24",
                        "luz" to "300"
                    )
                    sensoresRef.setValue(datosIniciales)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // 2. Escuchador general en tiempo real para el grupo "sensores"
        sensoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val humedad = snapshot.child("humedad").value?.toString() ?: "--"
                    val temperatura = snapshot.child("temperatura").value?.toString() ?: "--"
                    val luz = snapshot.child("luz").value?.toString() ?: "--"

                    tvHumedad.text = "$humedad %"
                    tvTemperatura.text = "$temperatura °C"
                    tvLuz.text = "$luz lx"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error de lectura Firebase: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // 3. Escuchadores individuales inmediatos
        sensoresRef.child("humedad").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let { tvHumedad.text = "$it %" }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        sensoresRef.child("temperatura").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let { tvTemperatura.text = "$it °C" }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        sensoresRef.child("luz").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let { tvLuz.text = "$it lx" }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // 4. Control y sincronización de la bomba de agua ("actuadores/bomba")
        val bombaRef = database.child("actuadores").child("bomba")
        bombaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val estadoBomba = snapshot.getValue(Boolean::class.java) ?: false
                if (!switchBomba.isPressed) {
                    switchBomba.isChecked = estadoBomba
                }
                tvEstadoBomba.text = if (estadoBomba) "Estado: Encendida ⚡" else "Estado: Apagada ⚪"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        switchBomba.setOnCheckedChangeListener { _, isChecked ->
            tvEstadoBomba.text = if (isChecked) "Estado: Encendida ⚡" else "Estado: Apagada ⚪"
            if (switchBomba.isPressed) {
                bombaRef.setValue(isChecked)
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this@MainActivity,
                            "Error al actualizar la bomba: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}
