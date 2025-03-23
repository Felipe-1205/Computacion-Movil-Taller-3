package com.example.supertaller3


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat


import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.supertaller3.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val markers = HashMap<String, Marker?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun vectorToBitmap(vector: Drawable): Bitmap {
        val width = vector.intrinsicWidth
        val height = vector.intrinsicHeight
        vector.setBounds(0, 0, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vector.draw(canvas)
        return bitmap
    }
    private fun updateMarker(marker: Marker, location: LatLng) {
        marker.position = location
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isZoomControlsEnabled = true

        val autenticado = ContextCompat.getDrawable(this, R.drawable.autenticado_usuario)
        val markerautenticado = autenticado?.let { vectorToBitmap(it) }
        val markerOptions1 = MarkerOptions()
            .position(LatLng(0.0, 0.0))
            .icon(markerautenticado?.let { BitmapDescriptorFactory.fromBitmap(it) })
        val customMarker1 = mMap.addMarker(markerOptions1)
        markers["autenticado"] = customMarker1


        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userRef = database.getReference("location/${currentUser.uid}")

            // Escucha los cambios en la ubicación del usuario autenticado en tiempo real
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ubicacion = dataSnapshot.getValue(Ubicacion::class.java)
                    if (ubicacion != null) {
                        val nuevaUbicacion1 = LatLng(ubicacion.latitud, ubicacion.longitud)
                        val customMarker1 = markers["autenticado"]
                        if (customMarker1 != null) {
                            updateMarker(customMarker1, nuevaUbicacion1)
                            val zoomLevel = 10.0f // Ajusta el valor según tus necesidades
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nuevaUbicacion1, zoomLevel))
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Ocurrió un error
                }
            })
        }

        val otro = ContextCompat.getDrawable(this, R.drawable.otro_usuario)
        val markerotro = otro?.let { vectorToBitmap(it) }
        val markerOptions2 = MarkerOptions()
            .position(LatLng(0.0, 0.0))
            .icon(markerotro?.let { BitmapDescriptorFactory.fromBitmap(it) })
        val customMarker2 = mMap.addMarker(markerOptions2)
        markers["otro"] = customMarker2

        val databaseReference = FirebaseDatabase.getInstance().reference
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // Escucha los cambios en la ubicación del otro usuario en tiempo real
        databaseReference.child("location")
            .orderByChild("email")
            .equalTo(userEmail)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val ubicacion = snapshot.getValue(Ubicacion::class.java)
                    if (ubicacion != null) {
                        val nuevaUbicacion2 = LatLng(ubicacion.latitud, ubicacion.longitud)
                        val customMarker2 = markers["otro"]
                        if (customMarker2 != null) {
                            updateMarker(customMarker2, nuevaUbicacion2)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val ubicacion = snapshot.getValue(Ubicacion::class.java)
                    if (ubicacion != null) {
                        val nuevaUbicacion2 = LatLng(ubicacion.latitud, ubicacion.longitud)
                        val customMarker2 = markers["otro"]
                        if (customMarker2 != null) {
                            updateMarker(customMarker2, nuevaUbicacion2)
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Algo ha sido eliminado de la base de datos, puedes manejarlo si es necesario
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Algo ha sido movido en la base de datos, puedes manejarlo si es necesario
                }

                override fun onCancelled(error: DatabaseError) {
                    // Ocurrió un error
                }
            })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuLogOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }
            R.id.switchAvailable -> {
                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    val userRef = database.getReference("users/${currentUser.uid}")
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val user = dataSnapshot.getValue(User::class.java)
                                if (user != null) {
                                    user.activo = if (user.activo == 0) 1 else 0
                                    userRef.setValue(user)
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
                }
                return true
            }
            R.id.Updateinfo -> {
                val intent = Intent(baseContext, ActualizarDatos::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}