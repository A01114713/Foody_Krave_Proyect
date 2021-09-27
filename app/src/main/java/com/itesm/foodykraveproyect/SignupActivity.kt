package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var username : EditText
    var arrayVacio: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.email_text_edit_signup)
        password = findViewById(R.id.password_edit_text_signup)
        username = findViewById(R.id.username_edit_text_signup)
    }

    fun registro(view : View?){
        Firebase.auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()).addOnCompleteListener(this){

            if(it.isSuccessful){
                Log.d("FIREBASE", "Registro exitoso")
                registrarUsername()
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "Correo ya registrado o es incorrecto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun registrarUsername() {
        val usuario = hashMapOf(
            "user id" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "username" to username.text.toString(),
            "platillos" to arrayVacio
        )

        Firebase.firestore.collection("usuarios")
            .add(usuario)
            .addOnSuccessListener {

                Log.d("FIREBASE", "id: ${it.id}")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
                Toast.makeText(this, "Nombre no se pudo guardar", Toast.LENGTH_SHORT).show();
            }
    }
}