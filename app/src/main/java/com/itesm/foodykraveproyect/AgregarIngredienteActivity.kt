package com.itesm.foodykraveproyect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class AgregarIngredienteActivity : AppCompatActivity() {

    lateinit var nombre : EditText
    lateinit var imagen : ImageView
    lateinit var tipoBoton : Button
    lateinit var saborBoton : Button
    lateinit var tipoVar : String
    lateinit var saborVar : String
    lateinit var imagenUri : Uri
    lateinit var buscarImagen : ActivityResultLauncher<String>
    var imagenEmpty : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ingrediente)

        nombre = findViewById(R.id.nombre_ingrediente_text)
        tipoBoton = findViewById(R.id.tipo_button)
        saborBoton = findViewById(R.id.sabor_button)
        imagen = findViewById(R.id.ingrediente_image)

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            imagenUri = it
            imagen.setImageURI(imagenUri)
            imagenEmpty = false
        }
    }

    fun popupTipoMostrar (view : View){
        val popupTipo:PopupMenu = PopupMenu(this, tipoBoton)
        popupTipo.menuInflater.inflate(R.menu.popup_tipo_ingrediente,popupTipo.menu)
        popupTipo.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            tipoVar = item.title.toString()
            tipoBoton.text = "Tipo    " + tipoVar
            true
        })
        popupTipo.show()
    }

    fun popupSaborMostrar (view : View){
        val popupSabor:PopupMenu = PopupMenu(this, saborBoton)
        popupSabor.menuInflater.inflate(R.menu.popup_sabor_ingrediente,popupSabor.menu)
        popupSabor.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            saborVar = item.title.toString()
            saborBoton.text = "Sabor    " + saborVar
            true
        })
        popupSabor.show()
    }

    fun registrarDatos() {
        val ingrediente = hashMapOf(
            "nombre" to nombre.text.toString(),
            "tipo" to tipoVar,
            "sabor" to saborVar
        )

        Firebase.firestore.collection("ingredientes")
            .add(ingrediente)
            .addOnSuccessListener {

                Toast.makeText(this, "Ingrediente agregado", Toast.LENGTH_SHORT).show();
                registrarImagen(it.id)
                Log.d("FIREBASE", "id: ${it.id}")
                finish()
            }
            .addOnFailureListener {

                Toast.makeText(this, "Error al agregar el ingrediente", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun checkIngrediente(view : View) {
        if(nombre.text.toString().isEmpty() || tipoVar.isEmpty() || saborVar.isEmpty() || imagenEmpty){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show();
            return
        }

        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["nombre"] == nombre.text.toString()){
                        Toast.makeText(this, "Ingrediente ya existente", Toast.LENGTH_SHORT).show();
                        return@addOnSuccessListener
                    }
                }
                registrarDatos()
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun seleccionarImagen(view : View){
        buscarImagen.launch("image/*")
    }

    fun registrarImagen(referenciaDocumento : String){

        val storageReference = FirebaseStorage.getInstance().getReference("imagenesIngredientes/$referenciaDocumento")
        storageReference.putFile(imagenUri)
            .addOnSuccessListener {

                Log.d("FIREBASE", "Correctamente cargado")
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }
}