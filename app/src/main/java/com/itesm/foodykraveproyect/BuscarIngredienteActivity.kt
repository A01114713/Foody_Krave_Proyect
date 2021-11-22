package com.itesm.foodykraveproyect

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class BuscarIngredienteActivity : AppCompatActivity() {

    lateinit var rvIngredientes : RecyclerView
    lateinit var busqueda : TextView
    lateinit var type : String

    var ingredientes : MutableList<Ingrediente> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_ingrediente)

        type = intent.getStringExtra("type").toString()
        busqueda = findViewById(R.id.buscar_ingrediente_input)
        rvIngredientes = findViewById(R.id.rv_ingredientes)

        rvIngredientes.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    leerImagen(documento.id, documento.data["nombre"].toString())
                }
                iniciarRecycler()
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Buscar Ingrediente", "Error al leer servicios: ${it.message}")
            }
    }

    fun buscarIngrediente(v : View){
        ingredientes.clear()
        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if (busqueda.text.isBlank()){
                        leerImagen(documento.id, documento.data["nombre"].toString())
                    } else if(documento.data["nombre"].toString().lowercase().contains(busqueda.text.toString().lowercase())){
                        leerImagen(documento.id, documento.data["nombre"].toString())
                    }
                }
                iniciarRecycler()
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Buscar Ingrediente", "error al leer servicios: ${it.message}")
            }
    }

    fun agregarIngrediente(v: View){
        val intent = Intent(this, AgregarIngredienteActivity::class.java)
        startActivity(intent)
    }

    fun leerImagen(nombreImagen : String, nombreVar : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesIngredientes/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                ingredientes.add(Ingrediente(nombreVar, bitmap, nombreImagen))
                Log.d("FIREBASE Buscar Ingrediente", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Buscar Ingrediente", "exception: ${it.message}")
            }
    }

    fun iniciarRecycler(){
        val adapter = IngredienteAdapter(ingredientes)
        rvIngredientes.adapter = adapter
        adapter.setOnItemClickListener(object : IngredienteAdapter.onItemClickListener{
            override fun onItemClick(id: String) {
                val intent = Intent()
                intent.putExtra("result", id)
                if(type == "1"){
                    setResult(1, intent)
                } else if(type == "2"){
                    setResult(2, intent)
                }
                finish()
            }
        })
    }
}