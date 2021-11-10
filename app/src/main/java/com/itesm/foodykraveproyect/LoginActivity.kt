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

        email = findViewById(R.id.email_login_text_edit)
        password = findViewById(R.id.password_text_edit)
    }

    fun revisarCampos(view : View) {
        if(email.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar el correo electronico", Toast.LENGTH_SHORT).show();
            return
        }
        if(password.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar la contraseña", Toast.LENGTH_SHORT).show();
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
                Log.d("FIREBASE", "Registro exitoso")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "Cuenta no existe o contraseña es erronea", Toast.LENGTH_SHORT).show();
            }
        }
    }
}