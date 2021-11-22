package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseUser




class CuentaActivity : AppCompatActivity() {

    lateinit var nombre : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta)

        nombre = findViewById(R.id.cuenta_mi_info)
        buscarNombre()
    }

    fun buscarNombre(){
        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == FirebaseAuth.getInstance().currentUser?.uid.toString()){
                        val username = documento.data["username"].toString();
                        val user = FirebaseAuth.getInstance().getCurrentUser()
                        val correo = user?.email
                        nombre.text = "Username: $username \n" + "Correo: $correo"
                        break
                    }
                }
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun misPlatillos (v : View){
        val platillosPropios: ArrayList<String> = arrayListOf()
        val usuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == usuario){
                        platillosPropios.add(documento.id)
                    }
                }
                if(platillosPropios.isEmpty()){
                    Toast.makeText(this, "No tienes platillos publicados", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, BuscarPlatilloActivity::class.java)
                    intent.putExtra("Platillos", platillosPropios)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al leer servicios: ${it.message}")
            }
    }

    fun logout (v : View){
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}