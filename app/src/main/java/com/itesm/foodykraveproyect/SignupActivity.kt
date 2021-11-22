package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var username : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.signup_email_input)
        password = findViewById(R.id.signup_password_input)
        username = findViewById(R.id.signup_username_input)
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
        if(username.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar el nombre de usuario", Toast.LENGTH_SHORT).show()
            return
        }
        registrarCuenta()
    }

    fun registrarCuenta(){
        Firebase.auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()).addOnCompleteListener(this){
            if(it.isSuccessful){
                Log.d("FIREBASE", "Registro exitoso")
                registrarUsername()
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registrarUsername() {
        val usuario = hashMapOf(
            "user id" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "username" to username.text.toString(),
            "platillos" to arrayListOf<String>()
        )

        Firebase.firestore.collection("usuarios")
            .add(usuario)
            .addOnSuccessListener {
                Log.d("FIREBASE", "id: ${it.id}")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }
}