package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun platillosGuardados (view : View){
        var platillosGuardados : ArrayList<String> = arrayListOf()
        var usuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == usuario){
                        platillosGuardados = documento.data["platillos"] as ArrayList<String>
                        if(platillosGuardados.size == 0){
                            Toast.makeText(this, "No hay platillos guardados", Toast.LENGTH_SHORT).show();
                        } else if(platillosGuardados.size > 0){
                            val intent = Intent(this, BuscarPlatilloActivity::class.java)
                            intent.putExtra("Platillos", platillosGuardados)
                            startActivity(intent)
                        }
                    }
                }
            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun buscarPlatillo (view : View){
        val intent = Intent(this, MenuBuscarPlatilloActivity::class.java)
        startActivity(intent)
    }

    fun agregarPlatillo (view : View){
        val intent = Intent(this, AgregarPlatilloActivity::class.java)
        startActivity(intent)
    }

    fun cuenta (view : View){
        val intent = Intent(this, CuentaActivity::class.java)
        startActivity(intent)
    }
}