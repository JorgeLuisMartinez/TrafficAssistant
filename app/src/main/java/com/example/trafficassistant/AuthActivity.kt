package com.example.trafficassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.regex.Pattern


class AuthActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        // Analytics events
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de firebase completa")
        analytics.logEvent("InitScreen", bundle)
        //--------------------------------------------------
        //Setup
        setup()
        sesion()
    }

    private fun sesion(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val proveedor = prefs.getString("provider", null)
        if (email != null && proveedor != null){
            showHome(email, ProviderType.valueOf(proveedor))
        }
    }

    private fun setup(){
        title = "Autenticación"

            signUpButton.setOnClickListener {
                if (EmailEditText.text.isNotEmpty() || PasswordEditText.text.isNotEmpty()) {
                    if (EmailEditText.text.isNotEmpty() && PasswordEditText.text.isNotEmpty() && validarEmail(EmailEditText.text)) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(EmailEditText.text.toString(), PasswordEditText.text.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            } else {
                                showAlert()
                            }
                        }
                    } else {
                        EmailEditText.setError("Email no válido")
                    }
                }else{
                    showAlertcampos()
                }
            }

            loginButton.setOnClickListener {
                if (EmailEditText.text.isNotEmpty()|| PasswordEditText.text.isNotEmpty()) {
                    if (EmailEditText.text.isNotEmpty() && PasswordEditText.text.isNotEmpty() && validarEmail(EmailEditText.text)) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(EmailEditText.text.toString(), PasswordEditText.text.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            } else {

                                showAlert()
                            }
                        }
                    } else {
                        EmailEditText.setError("Email no válido")
                    }
                }else{
                    showAlertcampos()
                }
            }

        //if (EmailEditText.text.isNotEmpty() || PasswordEditText.text.isNotEmpty())

        googleSignInButtom.setOnClickListener {
                //Configuracion de autenticacion
                val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient(this, googleConf)
                googleClient.signOut()
                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlert(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Se ha producido un error autenticando al usuario")
            builder.setPositiveButton("aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
    }
    private fun showAlertcampos(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Porfavor no deje campos vacios")
        builder.setPositiveButton("aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, proveedor: ProviderType){
            val HomeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", proveedor.name)
            }
            startActivity(HomeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                Log.w("TAG", "Google sign in failed /*/*/*/*/*/*/*/*/*/*////", e)
                showAlert()
            }

        }

    }
    private fun validarEmail(email: Editable): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }


    }
