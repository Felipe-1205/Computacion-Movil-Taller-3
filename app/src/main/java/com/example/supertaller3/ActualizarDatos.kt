package com.example.supertaller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.supertaller3.databinding.ActivityActualizarDatosBinding
import com.example.supertaller3.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarDatos : AppCompatActivity() {

    private lateinit var binding: ActivityActualizarDatosBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarDatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.reg.setOnClickListener() {
            validarRegistro()
        }
    }

    private fun validarRegistro() {
        val nombre = binding.nombreReg.text.toString().trim()
        val cedula = binding.cedulaReg.text.toString().trim()
        val apellido = binding.apellidoReg.text.toString().trim()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userRef = database.getReference("users/${currentUser.uid}")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            if (nombre.isNotEmpty()) {
                                user.nombre = nombre
                            }
                            if (cedula.isNotEmpty()) {
                                user.cedula = cedula
                            }
                            if (apellido.isNotEmpty()) {
                                user.apellido = apellido
                            }
                            userRef.setValue(user)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        val intent = Intent(this, Menu::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}