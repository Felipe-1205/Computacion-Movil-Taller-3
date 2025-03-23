package com.example.supertaller3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supertaller3.databinding.ActivityRegistrarseBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registrarse : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarseBinding
    val PATH_USERS = "users/"
    private lateinit var mAuth: FirebaseAuth
    private val TAG = "Regis"
    val PATH_UBI = "location/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        binding.reg.setOnClickListener() {
            validarRegistro()
        }
    }
    private fun validarRegistro() {
        val correo = binding.emailReg.text.toString().trim()
        val nombre = binding.nombreReg.text.toString().trim()
        val contrasena = binding.contraReg.text.toString().trim()
        val cedula = binding.cedulaReg.text.toString().trim()
        val apellido = binding.apellidoReg.text.toString().trim()

        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese Un correo Valido", Toast.LENGTH_SHORT).show()
            binding.emailReg.setText("")
            return
        }
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese Un nombre Valido", Toast.LENGTH_SHORT).show()
            binding.nombreReg.setText("")
            return
        }
        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese Una cedula Valida", Toast.LENGTH_SHORT).show()
            binding.cedulaReg.setText("")
            return
        }
        if (apellido.isEmpty()) {
            Toast.makeText(this, "Ingrese Un Apellido Valido", Toast.LENGTH_SHORT).show()
            binding.apellidoReg.setText("")
            return
        }
        if (contrasena.isEmpty() || contrasena.length < 6) {
            Toast.makeText(this, "Ingrese Una contraseña Valida", Toast.LENGTH_SHORT).show()
            binding.contraReg.setText("")
            return
        }
        regis()
    }
    private fun regis() {
        mAuth.createUserWithEmailAndPassword(
            binding.emailReg.text.toString().trim(),
            binding.contraReg.text.toString().trim()
        ).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                val user = mAuth.currentUser
                if (user != null) {
                    // Actualizar la información del usuario
                    val upcrb = UserProfileChangeRequest.Builder()
                    upcrb.setDisplayName("${binding.nombreReg.text}")
                    upcrb.setPhotoUri(Uri.parse("path/to/pic"))
                    user.updateProfile(upcrb.build())
                    val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString("USER_ID", user.uid)
                    editor.apply()
                    // Crear un objeto User con los datos del usuario
                    val myUser = User()
                    myUser.nombre = binding.nombreReg.text.toString()
                    myUser.apellido = binding.apellidoReg.text.toString()
                    myUser.cedula = binding.cedulaReg.text.toString()
                    myUser.email = binding.emailReg.text.toString()
                    myUser.activo = 0
                    myRef = database.getReference("$PATH_USERS${user.uid}")
                    myRef.setValue(myUser)
                    val myUbi = Ubicacion()
                    myUbi.latitud = 0.0
                    myUbi.longitud = 0.0
                    myUbi.email = binding.emailReg.text.toString()
                    myRef = database.getReference("$PATH_UBI${user.uid}")
                    myRef.setValue(myUbi)
                    updateUI(user)
                }
            }
            if (!task.isSuccessful) {
                Toast.makeText(this@Registrarse, "error registrando usuario", Toast.LENGTH_SHORT)
                    .show()
                task.exception?.message?.let { Log.e(TAG, it) }
            }
        })
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext, Menu::class.java)
            intent.putExtra("user", currentUser.displayName)
            startActivity(intent)
        } else {
            binding.emailReg.setText("")
            binding.contraReg.setText("")
        }
    }
}