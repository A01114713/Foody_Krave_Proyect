package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.login_email_input)
        password = findViewById(R.id.login_password_input)
    }

    fun revisarCampos(v : View) {
        if(email.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar el correo electronico", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        login()
    }

    fun login(){
        Firebase.auth.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnCompleteListener(this){
            if(it.isSuccessful){
                Log.d("FIREBASE", "Login exitoso")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("FIREBASE", "Login fracasó: ${it.exception?.message}")
                Toast.makeText(this, "Cuenta no existe o contraseña es erronea", Toast.LENGTH_SHORT).show()
            }
        }
    }
}