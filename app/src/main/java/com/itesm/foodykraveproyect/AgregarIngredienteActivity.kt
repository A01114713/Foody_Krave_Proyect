package com.itesm.foodykraveproyect

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AgregarIngredienteActivity : AppCompatActivity() {

    lateinit var nombre : EditText
    lateinit var imagen : ImageView
    lateinit var tipoBoton : Button
    lateinit var saborBoton : Button
    lateinit var imagenUri : Uri
    lateinit var buscarImagen : ActivityResultLauncher<String>
    var tipoVar : String = ""
    var saborVar : String = ""
    var imagenEmpty : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ingrediente)

        nombre = findViewById(R.id.agregar_ingrediente_nombre)
        tipoBoton = findViewById(R.id.agregar_ingrediente_tipo_btn)
        saborBoton = findViewById(R.id.agregar_ingrediente_sabor_btn)
        imagen = findViewById(R.id.agregar_ingrediente_img)

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) {
                imagenUri = it
                imagen.setImageURI(imagenUri)
                imagenEmpty = false
            }
        }
    }

    fun popupTipoMostrar (v : View){
        val popupTipo = PopupMenu(this, tipoBoton)
        popupTipo.menuInflater.inflate(R.menu.popup_tipo_ingrediente,popupTipo.menu)
        popupTipo.setOnMenuItemClickListener { item ->
            tipoVar = item.title.toString()
            tipoBoton.text = "Tipo    $tipoVar"
            true
        }
        popupTipo.show()
    }

    fun popupSaborMostrar (v : View){
        val popupSabor = PopupMenu(this, saborBoton)
        popupSabor.menuInflater.inflate(R.menu.popup_sabor_ingrediente,popupSabor.menu)
        popupSabor.setOnMenuItemClickListener { item ->
            saborVar = item.title.toString()
            saborBoton.text = "Sabor    $saborVar"
            true
        }
        popupSabor.show()
    }

    fun registrarDatos() {
        val str = nombre.text.toString()
        val sub = str.subSequence(1, str.length).toString()
        val name = str[0].uppercase() + sub.lowercase()
        val ingrediente = hashMapOf(
            "nombre" to name,
            "tipo" to tipoVar,
            "sabor" to saborVar
        )

        Firebase.firestore.collection("ingredientes")
            .add(ingrediente)
            .addOnSuccessListener {
                Toast.makeText(this, "Ingrediente agregado", Toast.LENGTH_SHORT).show()
                registrarImagen(it.id)
                Log.d("FIREBASE Agregar Ingrediente", "id: ${it.id}")
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agregar el ingrediente", Toast.LENGTH_SHORT).show()
                Log.e("FIREBASE Agregar Ingrediente", "exception: ${it.message}")
            }
    }

    fun checkIngrediente(v : View) {
        if(nombre.text.toString().isEmpty() || tipoVar.isEmpty() || saborVar.isEmpty() || imagenEmpty){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["nombre"].toString().lowercase() == nombre.text.toString().lowercase()){
                        Toast.makeText(this, "Ingrediente ya existente", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                }
                registrarDatos()
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Agregar Ingrediente", "error al leer servicios: ${it.message}")
            }
    }

    fun seleccionarImagen(v : View){
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