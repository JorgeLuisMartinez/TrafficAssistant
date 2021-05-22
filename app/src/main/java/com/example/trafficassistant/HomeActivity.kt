package com.example.trafficassistant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
    //hola buenas soy yo
}
class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
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
        //// datos base de datos
        buttonSave.setOnClickListener {
            db.collection("users").document(email).set(
            hashMapOf("provider" to proveedor,
            "name" to textName.text.toString(),
            "lastName" to textLastName.text.toString(),
            "carNumber" to textNumber.text.toString(),
            "phone" to textPhone.text.toString())
        )
            textName.setText("")
            textLastName.setText("")
            textNumber.setText("")
            textPhone.setText("")
            textName.setEnabled(false)
            textLastName.setEnabled(false)
            textNumber.setEnabled(false)
            textPhone.setEnabled(false)
        }
        buttonUpdate.setOnClickListener {
        db.collection("users").document(email).get().addOnSuccessListener {
            textName.setText(it.get("name")as String?)
            textLastName.setText(it.get("lastName")as String?)
            textNumber.setText(it.get("carNumber")as String?)
            textPhone.setText(it.get("phone")as String?)
        }
            textName.setEnabled(true)
            textLastName.setEnabled(true)
            textNumber.setEnabled(true)
            textPhone.setEnabled(true)
        }
        buttonDelete.setOnClickListener {
        db.collection("users").document(email).delete()
            textName.setText("")
            textLastName.setText("")
            textNumber.setText("")
            textPhone.setText("")
            textName.setEnabled(true)
            textLastName.setEnabled(true)
            textNumber.setEnabled(true)
            textPhone.setEnabled(true)
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