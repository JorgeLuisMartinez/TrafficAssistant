package com.example.trafficassistant

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var map:GoogleMap
    companion object{
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

    private fun enableLocation (){
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()){
            //SI
            map.isMyLocationEnabled = true
        }else{
            //NO
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission (){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION)
        }
    }

    //////////////////
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }
    /////////////////

    ///////// MAPA MAPA MAPA MAPA MAPA MAPA MAPA MAPA
    override fun onMapReady(googleMap: GoogleMap) {
        map=googleMap
        //MARCADORES
        createMarker()
        createMarker2()
        createMarker3()
        val cordeenadasprincipales = LatLng(7.888593, -72.496212)
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(cordeenadasprincipales, 14f), 3000, null
        )
        //
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
    }
    ////////
    private fun createMarkerejemplonormal() {
        val coordinates = LatLng(7.911265, -72.499812)
        val marker = MarkerOptions().position(coordinates).title("Universidad de santander udes, campues cúcuta")
        map.addMarker(marker)
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 18f), 4000, null
        )
    }
    private fun createMarker() {
        val coordinates = LatLng(7.911265, -72.499812)
        val marker = MarkerOptions().position(coordinates).title("Universidad de santander udes, campues cúcuta").icon(BitmapDescriptorFactory.fromResource(R.drawable.choque))
        map.addMarker(marker)
    }

    private fun createMarker2() {
        val coordinates = LatLng(7.893078, -72.502370)
        val marker = MarkerOptions().position(coordinates).title("Estadio").icon(BitmapDescriptorFactory.fromResource(R.drawable.dos))
        map.addMarker(marker)
    }
    private fun createMarker3() {
        val coordinates = LatLng(7.863519, -72.481267)
        val marker = MarkerOptions().position(coordinates).title("Comida rápida locochón").icon(BitmapDescriptorFactory.fromResource(R.drawable.tres))
        map.addMarker(marker)
    }

    // FUNCIONES DEL BOTON LOCALIZACION
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Localización en tiempo real", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}