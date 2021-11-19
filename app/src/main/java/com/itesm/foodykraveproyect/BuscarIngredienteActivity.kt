package com.itesm.foodykraveproyect

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
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
        ingredientesIniciales()
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rvIngredientes = findViewById(R.id.rvingredientes_list)
        busqueda = findViewById(R.id.buscar_ingrediente_text)
    }

    fun ingredientesIniciales(){
        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {

                for (documento in it) {
                    leerImagen(documento.id, documento.data["nombre"].toString())
                }

            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun buscarIngrediente(view : View){
        ingredientes.clear()
        Firebase.firestore.collection("ingredientes")
            .get()
            .addOnSuccessListener {

                for (documento in it) {
                    if (documento.data["nombre"] == busqueda.text.toString()) {
                        leerImagen(documento.id, documento.data["nombre"].toString())
                    }
                }
            }
            .addOnFailureListener() {

                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun agregarIngrediente(view: View){
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
                Log.d("FIREBASE", "Correctamente cargado")
                iniciarRecycler()
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun iniciarRecycler(){
        rvIngredientes.layoutManager = LinearLayoutManager(this)
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