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

class MenuBuscarPlatilloActivity : AppCompatActivity() {
    lateinit var tipo: Button
    var tipoVar: String = "0"
    lateinit var tiempo: Button
    var tiempoVar: String = "0"

    var ingredientePrincipalID : String = "0"
    lateinit var ingredientePrincipalImagen : ImageView
    var ingredienteSecundarioID : String = "0"
    lateinit var ingredienteSecundarioImagen : ImageView

    var encontrados : ArrayList<String> = arrayListOf()

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
        setContentView(R.layout.activity_menu_buscar_platillo)

        tipo = findViewById(R.id.buscar_platillo_tipo_button)
        tiempo = findViewById(R.id.buscar_platillo_tiempo_button)
        ingredientePrincipalImagen = findViewById(R.id.buscar_ingrediente_principal_imagen)
        ingredienteSecundarioImagen = findViewById(R.id.buscar_ingrediente_secundario_imagen)
    }

    fun buscarPlatillo(view: View){
        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if ((documento.data["tiempo"] == tiempoVar || tiempoVar == "0")
                        && (documento.data["tipo"] == tipoVar || tipoVar == "0")
                        && (documento.data["ingrediente principal"] == ingredientePrincipalID || ingredientePrincipalID == "0")
                        && (documento.data["ingrediente secundario"] == ingredienteSecundarioID || ingredienteSecundarioID == "0")
                    ) {
                        encontrados.add(documento.id.toString())
                    }
                }
                if(encontrados.size == 0){
                    Toast.makeText(this, "No hay coincidencias", Toast.LENGTH_SHORT).show();
                } else if(encontrados.size > 0) {
                    val intent = Intent(this, BuscarPlatilloActivity::class.java)
                    intent.putExtra("Platillos", encontrados)
                    startActivity(intent)
                }
            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
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