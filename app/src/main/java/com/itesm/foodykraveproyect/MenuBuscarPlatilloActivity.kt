package com.itesm.foodykraveproyect

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MenuBuscarPlatilloActivity : AppCompatActivity() {

    lateinit var ingredientePrincipalImagen : ImageView
    lateinit var ingredienteSecundarioImagen : ImageView
    lateinit var tiempo: Button
    lateinit var tipo: Button

    var ingredientePrincipalID : String = "0"
    var ingredienteSecundarioID : String = "0"
    var tipoVar: String = "0"
    var tiempoVar: String = "0"

    lateinit var encontrados : ArrayList<String>

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
        setContentView(R.layout.activity_menu_buscar_platillo)

        tipo = findViewById(R.id.menu_buscar_platillo_tipo_btn)
        tiempo = findViewById(R.id.menu_buscar_platillo_tiempo_btn)
        ingredientePrincipalImagen = findViewById(R.id.menu_buscar_platillo_ingrediente_principal_img)
        ingredienteSecundarioImagen = findViewById(R.id.menu_buscar_platillo_ingrediente_secundario_img)

        encontrados = arrayListOf()
    }

    fun buscarPlatillo(v: View){
        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if ((documento.data["ingrediente principal"] == ingredientePrincipalID || ingredientePrincipalID.equals("0"))
                        && (documento.data["ingrediente secundario"] == ingredienteSecundarioID || ingredienteSecundarioID.equals("0"))
                        && (documento.data["tipo"] == tipoVar || tipoVar.equals("0"))
                        && (documento.data["tiempo"] == tiempoVar || tiempoVar.equals("0"))
                    ) {
                        encontrados.add(documento.id)
                    }
                }
                if(encontrados.isEmpty()){
                    Toast.makeText(this, "No hay coincidencias", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, BuscarPlatilloActivity::class.java)
                    intent.putExtra("Platillos", encontrados)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Menu Buscar Platillo", "Error al leer servicios: ${it.message}")
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
                Log.d("FIREBASE Menu Buscar Platillo", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Menu Buscar Platillo", "Exception: ${it.message}")
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