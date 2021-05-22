package com.example.trafficassistant

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private var longitud: Double = 0.0
    private var latitud: Double = 0.0
    private lateinit var map: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createFragment()

    }

    override fun onBackPressed() {
        val Mybuild = AlertDialog.Builder(this)
        Mybuild.setMessage("Esta seguro que desea cerrar Maps")
        Mybuild.setTitle("Salir de mapas")
        Mybuild.setPositiveButton("SI") { dialog, which -> finish() }
        Mybuild.setNegativeButton("NO") { dialog, which -> dialog.cancel() }
        val dialog = Mybuild.create()
        dialog.show()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            //SI
            map.isMyLocationEnabled = true
        } else {
            //NO
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    //////////////////
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    ///////// MAPA MAPA MAPA MAPA MAPA MAPA MAPA MAPA
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()

        /// SE CREAN MARCADORES ALMACENADOS
        crearCollection()

        /// METODOS PARA GUARDAR ALERTAS
        // REGISTRAR ACCIDENTE
        val fab1: View = findViewById(R.id.idFabAccidente)
        fab1.setOnClickListener { view ->
            Snackbar.make(view, "Se Registro el Accidente", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()

            val data = hashMapOf(
                "Latitud" to latitud,
                "Longitud" to longitud,
                "TipoAlerta" to "Accidente",
                "Titulo" to "Accidente de Transito"
            )

            db.collection("Alertas")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "Registro Exitoso",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("Registro Fallido", "Error adding document", e)
                }
        }
        // REGISTRAR OBRA
        val fab2: View = findViewById(R.id.idFabObra)
        fab2.setOnClickListener { view ->
            Snackbar.make(view, "Se Registro Obra en la Vía", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()

            val data = hashMapOf(
                "Latitud" to latitud,
                "Longitud" to longitud,
                "TipoAlerta" to "Obra",
                "Titulo" to "Obra en la Vía"
            )

            db.collection("Alertas")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "Registro Exitoso",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("Registro Fallido", "Error adding document", e)
                }
        }
        // REGISTRAR SEMAFORO
        val fab3: View = findViewById(R.id.idFabSemaforo)
        fab3.setOnClickListener { view ->
            Snackbar.make(view, "Se Registro Semaforo Averiado", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()

            val data = hashMapOf(
                "Latitud" to latitud,
                "Longitud" to longitud,
                "TipoAlerta" to "Semaforo",
                "Titulo" to "Semaforo Averiado"
            )

            db.collection("Alertas")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "Registro Exitoso",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("Registro Fallido", "Error adding document", e)
                }
        }
        /// SE CREAN Y MODIFICAN MARCADORES EN TIEMPO REAL
        db.collection("Alertas")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d("TAG", "New city: ${dc.document.data}")
                            val latitudRT = dc.document.data.get("Latitud")
                            val LongitudRT = dc.document.data.get("Longitud")
                            val Tipo = dc.document.data.get("TipoAlerta")
                            val Titulo = dc.document.data.get("Titulo")
                            Log.d(
                                "Datossssss: ", "latitudRT: ${latitudRT}" +
                                        "LongitudRT: ${LongitudRT}" +
                                        "Tipo: ${Tipo}" +
                                        "Titulo: ${Titulo}" +
                                        "-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*"
                            )
                            createMarkereTimeR(
                                latitudRT as Double,
                                LongitudRT as Double,
                                Tipo as String,
                                Titulo as String
                            )
                        }
                        DocumentChange.Type.MODIFIED -> Log.d(
                            "TAG",
                            "Modified city: ${dc.document.data}"
                        )
                        DocumentChange.Type.REMOVED -> {
                            map.clear()
                            recargar(googleMap)
                            val view: View = findViewById(R.id.idgroupButtons)
                            Snackbar.make(
                                view,
                                "Se elimino una alerta recientemente",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null)
                                .show()
                        }
                    }
                }
            }
        /// ANIMACION INICIAL AL CARGAR EL MAPA
        val cordeenadasprincipales = LatLng(7.888593, -72.496212)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(cordeenadasprincipales, 12f), 4000, null
        )

    }

    /// METODO PARA RECARGAR EL MAPA
    private fun recargar(googleMap: GoogleMap) {
        onMapReady(googleMap)
    }

    /// METODO PARA OBTENER LA COLLECTION DE LA BASE DE DATOS Y CREAR LOS MARCADORES
    private fun crearCollection() {
        db.collection("Alertas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val latitudRT = document.data.get("Latitud")
                    val LongitudRT = document.data.get("Longitud")
                    val Tipo = document.data.get("TipoAlerta")
                    val Titulo = document.data.get("Titulo")
                    Log.d(
                        "Datossssss INICIALES: ", "latitudRT: ${latitudRT}" +
                                "LongitudRT: ${LongitudRT}" +
                                "Tipo: ${Tipo}" +
                                "Titulo: ${Titulo}" +
                                "-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*"
                    )
                    createMarkereTimeR(
                        latitudRT as Double,
                        LongitudRT as Double,
                        Tipo as String,
                        Titulo as String
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w(
                    "TAG",
                    "Error getting documents:-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*",
                    exception
                )
            }
    }

    /// METODO PARA CREAR MARCADORES
    private fun createMarkereTimeR(
        latitudRT: Double,
        LongitudRT: Double,
        Tipo: String,
        Titulo: String
    ) {
        val coordinates = LatLng(latitudRT, LongitudRT)
        var icono = 0
        if (Tipo.equals("Accidente")) {
            icono = (R.drawable.choque)
        } else if (Tipo.equals("Obra")) {
            icono = (R.drawable.dos)
        } else if (Tipo.equals("Semaforo")) {
            icono = (R.drawable.tres)
        }
        val marker = MarkerOptions().position(coordinates).title(Titulo)
            .icon(BitmapDescriptorFactory.fromResource(icono))
        map.addMarker(marker)
    }

    // FUNCIONES DEL BOTON LOCALIZACION
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Mi Localización En Tiempo Real", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        latitud = p0.latitude
        longitud = p0.longitude
        Toast.makeText(
            this,
            "Coordenadas: Lat: ${p0.latitude}, Lon: ${p0.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }
}