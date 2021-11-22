package com.itesm.foodykraveproyect

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AgregarPlatilloActivity : AppCompatActivity() {

    lateinit var nombre: EditText
    lateinit var imagen: ImageView
    lateinit var ingredientesTexto: EditText
    lateinit var receta: EditText
    lateinit var tipo: Button
    lateinit var tiempo: Button
    lateinit var imagenUri: Uri
    lateinit var buscarImagen: ActivityResultLauncher<String>

    lateinit var ingredientePrincipalID : String
    lateinit var ingredientePrincipalImagen : ImageView
    lateinit var ingredienteSecundarioID : String
    lateinit var ingredienteSecundarioImagen : ImageView
    var tipoVar: String = ""
    var tiempoVar: String = ""
    var imagenEmpty : Boolean = true

    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == 1){
            val data:Intent? = result.data
            ingredientePrincipalID = data?.getStringExtra("result").toString()
            leerImagen(ingredientePrincipalID, 1)
        } else if(result.resultCode == 2){
            val data:Intent? = result.data
            ingredienteSecundarioID = data?.getStringExtra("result").toString()
            leerImagen(ingredienteSecundarioID, 2)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_platillo)

        nombre = findViewById(R.id.agregar_platillo_nombre)
        imagen = findViewById(R.id.agregar_platillo_img)
        ingredientesTexto = findViewById(R.id.agregar_platillo_ingredientes_input)
        receta = findViewById(R.id.agregar_platillo_receta_input)
        tipo = findViewById(R.id.agregar_platillo_tipo_btn)
        tiempo = findViewById(R.id.agregar_platillo_tiempo_btn)
        ingredientePrincipalImagen = findViewById(R.id.agregar_platillo_ingrediente_principal_img)
        ingredienteSecundarioImagen = findViewById(R.id.agregar_platillo_ingrediente_secundario_img)

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) {
                imagenUri = it
                imagen.setImageURI(imagenUri)
                imagenEmpty = false
            }
        }
    }

    fun leerImagen(nombreImagen : String, type : Int){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesIngredientes/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                if(type == 1){
                    ingredientePrincipalImagen.setImageBitmap(bitmap)
                } else if (type == 2){
                    ingredienteSecundarioImagen.setImageBitmap(bitmap)
                }
                Log.d("FIREBASE Agregar Platillo", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Agregar Platillo", "exception: ${it.message}")
            }
    }

    fun registrarDatos() {
        val str = nombre.text.toString()
        val sub = str.subSequence(1, str.length).toString()
        val name = str[0].uppercase() + sub.lowercase()
        val platillo = hashMapOf(
            "nombre" to name,
            "ingredientes texto" to ingredientesTexto.text.toString(),
            "receta" to receta.text.toString(),
            "tipo" to tipoVar,
            "tiempo" to tiempoVar,
            "user id" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "ingrediente principal" to ingredientePrincipalID,
            "ingrediente secundario" to ingredienteSecundarioID
        )

        Firebase.firestore.collection("platillos")
            .add(platillo)
            .addOnSuccessListener {
                Toast.makeText(this, "Platillo agregado", Toast.LENGTH_SHORT).show()
                registrarImagen(it.id)
                Log.d("FIREBASE Agregar Platillo", "id: ${it.id}")
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agregar el platillo", Toast.LENGTH_SHORT).show()
                Log.e("FIREBASE Agregar Platillo", "exception: ${it.message}")
            }
    }

    fun checkPlatillo(v : View) {
        if(nombre.text.toString().isEmpty() || tipoVar.isEmpty() || tiempoVar.isEmpty()
            || ingredientesTexto.text.toString().isEmpty() || receta.text.toString().isEmpty()
            || ingredientePrincipalID.isEmpty() || ingredienteSecundarioID.isEmpty() || imagenEmpty){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show()
            return
        }
        registrarDatos()
    }

    fun seleccionarImagen(v: View) {
        buscarImagen.launch("image/*")
    }

    fun registrarImagen(referenciaDocumento: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesPlatillos/$referenciaDocumento")
        storageReference.putFile(imagenUri)
            .addOnSuccessListener {
                Log.d("FIREBASE Agregar Platillo", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Agregar Platillo", "exception: ${it.message}")
            }
    }

    fun popupTipoMostrar(v: View) {
        val popupTipo = PopupMenu(this, tipo)
        popupTipo.menuInflater.inflate(R.menu.popup_tipo_platillo, popupTipo.menu)
        popupTipo.setOnMenuItemClickListener { item ->
            tipoVar = item.title.toString()
            tipo.text = "Tipo  $tipoVar"
            true
        }
        popupTipo.show()
    }

    fun popupTiempoMostrar(v: View) {
        val popupTiempo = PopupMenu(this, tiempo)
        popupTiempo.menuInflater.inflate(R.menu.popup_tiempo_platillo, popupTiempo.menu)
        popupTiempo.setOnMenuItemClickListener { item ->
            tiempoVar = item.title.toString()
            tiempo.text = "Tiempo  $tiempoVar"
            true
        }
        popupTiempo.show()
    }

    fun buscarIngredientePrincipal(v : View){
        val intent = Intent(this, BuscarIngredienteActivity::class.java)
        intent.putExtra("type", "1")
        activityResultLauncher.launch(intent)
    }

    fun buscarIngredienteSecundario(v : View){
        val intent = Intent(this, BuscarIngredienteActivity::class.java)
        intent.putExtra("type", "2")
        activityResultLauncher.launch(intent)
    }

}