package com.itesm.foodykraveproyect

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    lateinit var tipoVar: String
    lateinit var tiempo: Button
    lateinit var tiempoVar: String
    lateinit var imagenUri: Uri
    lateinit var buscarImagen: ActivityResultLauncher<String>

    lateinit var ingredientePrincipalID : String
    lateinit var ingredientePrincipalImagen : ImageView
    lateinit var ingredienteSecundarioID : String
    lateinit var ingredienteSecundarioImagen : ImageView
    var imagenEmpty : Boolean = true

    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->

        if(result.resultCode == 1){
            val data:Intent? = result.data
            ingredientePrincipalID = data?.getStringExtra("result").toString()
            leerImagen(ingredientePrincipalID, 1)
        }

        if(result.resultCode == 2){
            val data:Intent? = result.data
            ingredienteSecundarioID = data?.getStringExtra("result").toString()
            leerImagen(ingredienteSecundarioID, 2)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_platillo)

        nombre = findViewById(R.id.nombre_platillo_text)
        imagen = findViewById(R.id.platillo_agregar_image)
        ingredientesTexto = findViewById(R.id.ingredientes_platillo_text)
        receta = findViewById(R.id.receta_platillo_text)
        tipo = findViewById(R.id.platillo_tipo_button)
        tiempo = findViewById(R.id.platillo_tiempo_button)
        ingredientePrincipalImagen = findViewById(R.id.ingrediente_principal_imagen)
        ingredienteSecundarioImagen = findViewById(R.id.ingrediente_secundario_imagen)

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            imagenUri = it
            imagen.setImageURI(imagenUri)
            imagenEmpty = false
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

                Log.d("FIREBASE", "Correctamente cargado")
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun registrarDatos() {
        val platillo = hashMapOf(
            "nombre" to nombre.text.toString(),
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

                Toast.makeText(this, "Platillo agregado", Toast.LENGTH_SHORT).show();
                registrarImagen(it.id)
                Log.d("FIREBASE", "id: ${it.id}")
                finish()
            }
            .addOnFailureListener {

                Toast.makeText(this, "Error al agregar el platillo", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun checkPlatillo(view : View) {
        if(nombre.text.toString().isEmpty() || tipoVar.isEmpty() || tiempoVar.isEmpty()
            || ingredientesTexto.text.toString().isEmpty() || receta.text.toString().isEmpty()
            || ingredientePrincipalID.isEmpty() || ingredienteSecundarioID.isEmpty() || imagenEmpty){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show();
            return
        }
        registrarDatos()
    }

    fun seleccionarImagen(view: View) {
        buscarImagen.launch("image/*")
    }

    fun registrarImagen(referenciaDocumento: String) {

        val storageReference = FirebaseStorage.getInstance().getReference("imagenesPlatillos/$referenciaDocumento")
        storageReference.putFile(imagenUri)
            .addOnSuccessListener {

                Log.d("FIREBASE", "Correctamente cargado")
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun popupTipoMostrar(view: View) {
        val popupTipo: PopupMenu = PopupMenu(this, tipo)
        popupTipo.menuInflater.inflate(R.menu.popup_tipo_platillo, popupTipo.menu)
        popupTipo.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            tipoVar = item.title.toString()
            tipo.text = "Tipo  " + tipoVar
            true
        })
        popupTipo.show()
    }

    fun popupTiempoMostrar(view: View) {
        val popupTiempo: PopupMenu = PopupMenu(this, tiempo)
        popupTiempo.menuInflater.inflate(R.menu.popup_tiempo_platillo, popupTiempo.menu)
        popupTiempo.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            tiempoVar = item.title.toString()
            tiempo.text = "Tiempo  " + tiempoVar
            true
        })
        popupTiempo.show()
    }

    fun buscarIngredientePrincipal(view : View){
        val intent = Intent(this, BuscarIngredienteActivity::class.java)
        intent.putExtra("type", "1")
        activityResultLauncher.launch(intent)
        //startActivity(intent)
    }

    fun buscarIngredienteSecundario(view : View){
        val intent = Intent(this, BuscarIngredienteActivity::class.java)
        intent.putExtra("type", "2")
        activityResultLauncher.launch(intent)
    }

}