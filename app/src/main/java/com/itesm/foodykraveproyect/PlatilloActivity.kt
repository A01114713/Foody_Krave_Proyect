package com.itesm.foodykraveproyect

import android.content.Intent
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
    lateinit var platilloID: String
    lateinit var boton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platillo)

        nombre = findViewById(R.id.platillo_nombre_text)
        imagen = findViewById(R.id.platillo_image)
        autor = findViewById(R.id.autor_text)
        ingredientes = findViewById(R.id.ingredientes_text)
        receta = findViewById(R.id.receta_text)
        boton = findViewById(R.id.agregar_eliminar_button)
        platilloID = intent.getStringExtra("PlatilloID").toString()
    }

    fun leerDatos() {
        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == platilloID){
                        nombre.text = documento.data["nombre"].toString()
                        ingredientes.text = "Ingredientes: \n" + documento.data["ingredientes texto"].toString()
                        receta.text = "Receta: \n" + documento.data["receta"].toString()
                        leerAutor(documento.data["user id"].toString())
                        leerImagen(platilloID)
                    }
                }
            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
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
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer usuarios: ${it.message}")
            }
    }

    fun leerImagen(nombreImagen : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesPlatillos/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                imagen.setImageBitmap(bitmap)
                Log.d("FIREBASE", "Correctamente cargado")
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun agregarEliminar(view : View){
        var platillosGuardados : ArrayList<String> = arrayListOf()
        var usuario = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var documentoID : String

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {

                for (documento in it) {
                    if(documento.data["user id"] == usuario){
                        platillosGuardados = documento.data["platillos"] as ArrayList<String>
                        platillosGuardados.add(platilloID)
                        documentoID = documento.id.toString()

                        Firebase.firestore.collection("usuarios").document(documentoID).update(
                            mapOf("platillos" to platillosGuardados)
                        )

                        Toast.makeText(this, "Platillo guardado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    override fun onStart(){
        super.onStart()
        leerDatos()
    }
}