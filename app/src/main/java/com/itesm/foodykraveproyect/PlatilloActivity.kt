package com.itesm.foodykraveproyect

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PlatilloActivity : AppCompatActivity() {

    lateinit var nombre: TextView
    lateinit var imagen : ImageView
    lateinit var autor: TextView
    lateinit var ingredientes: TextView
    lateinit var receta: TextView
    lateinit var boton: Button
    lateinit var platilloID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platillo)

        nombre = findViewById(R.id.platillo_nombre)
        imagen = findViewById(R.id.platillo_imagen)
        autor = findViewById(R.id.platillo_autor)
        ingredientes = findViewById(R.id.platillo_ingredientes)
        receta = findViewById(R.id.platillo_receta)
        boton = findViewById(R.id.agregar_eliminar_btn)
        platilloID = intent.getStringExtra("PlatilloID").toString()
    }

    override fun onStart(){
        super.onStart()
        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == platilloID){
                        nombre.text = documento.data["nombre"].toString()
                        ingredientes.text = "Ingredientes:\n${documento.data["ingredientes texto"].toString()}"
                        receta.text = "Receta:\n${documento.data["receta"].toString()}"
                        leerAutor(documento.data["user id"].toString())
                        leerImagen(platilloID)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Platillo", "Error al leer servicios: ${it.message}")
            }
    }

    fun leerAutor(autorid : String){
        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == autorid) {
                        autor.text = documento.data["username"].toString()
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Platillo", "error al leer usuarios: ${it.message}")
            }
    }

    fun leerImagen(nombreImagen : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesPlatillos/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                imagen.setImageBitmap(bitmap)
                Log.d("FIREBASE Platillo", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Platillo", "exception: ${it.message}")
            }
    }

    fun agregarEliminar(v : View){
        var platillosGuardados: ArrayList<String>
        val usuario = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var documentoID : String

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == usuario){
                        platillosGuardados = documento.data["platillos"] as ArrayList<String>
                        if (!platillosGuardados.contains(platilloID)) {
                            platillosGuardados.add(platilloID)
                            documentoID = documento.id
                                Firebase.firestore.collection("usuarios").document(documentoID).update(
                                    mapOf("platillos" to platillosGuardados)
                                )
                            Toast.makeText(this, "Platillo guardado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "El platillo ya esta guardado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Platillo", "error al leer servicios: ${it.message}")
            }
    }
}