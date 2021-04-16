package com.example.trafficassistant

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
}