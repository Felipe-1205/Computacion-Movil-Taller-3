package com.example.supertaller3

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.supertaller3.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private val TAG = "lOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.registro.setOnClickListener() {
            startActivity(Intent(this, Registrarse::class.java))
        }
        mAuth = FirebaseAuth.getInstance()

        binding.iniciarSesion.setOnClickListener() {
            sigin()
        }
    }
    private fun sigin() {
        mAuth.signInWithEmailAndPassword(
            binding.usuarioIn.text.toString().trim(),
            binding.contraIn.text.toString().trim()
        ).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                val user = mAuth.currentUser
                val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("USER_ID", user?.uid)
                editor.apply()
                val intent = Intent(baseContext, Menu::class.java)
                startActivity(intent)
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(this@MainActivity, "Algun Dato Es Incorrecto.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.usuarioIn.text = null
                binding.contraIn.text = null
            }
        })
    }
}