package com.example.trafficassistant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
    //hola buenas soy yo
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //intents
        mapsButtom.setOnClickListener {
            val mapint = Intent(this, MapsActivity::class.java)
            startActivity(mapint)
        }
        // Setup
        val bundle =intent.extras
        val email = bundle?.getString("email")
        val proveedor = bundle?.getString("provider")
        setup(email?:"",proveedor?:"")
        //Guardar datos/Estado
        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",proveedor)
        prefs.apply()
    }
    private fun setup(email:String, proveedor:String){
        title ="inicio"
        textViewEmail.text = email
        textViewProveedor.text = proveedor
        LogOutButton.setOnClickListener {
            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            //-----------------
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val Mybuild = AlertDialog.Builder(this)
        Mybuild.setMessage("Esta seguro que desea cerrar sesión")
        Mybuild.setTitle("Cerrar sesion")
        Mybuild.setPositiveButton("SI") { dialog, which -> finish() }
        Mybuild.setNegativeButton("NO") { dialog, which -> dialog.cancel() }
        val dialog = Mybuild.create()
        dialog.show()
    }

}