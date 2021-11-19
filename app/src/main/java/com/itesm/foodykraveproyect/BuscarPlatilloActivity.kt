package com.itesm.foodykraveproyect

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class BuscarPlatilloActivity : AppCompatActivity() {

    lateinit var rvPlatillos : RecyclerView

    var busquedas : ArrayList<String> = arrayListOf()

    var platillos : MutableList<Platillo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_platillo)

        rvPlatillos = findViewById(R.id.rvplatillos_list)
        busquedas = intent.getStringArrayListExtra("Platillos") as ArrayList<String>
        buscarPlatillo()
    }

    fun buscarPlatillo(){
        platillos.clear()
        Firebase.firestore.collection("platillos")
            .get()
            .addOnSuccessListener {
                for (busqueda in busquedas){
                    for (documento in it) {
                        if(documento.id == busqueda.toString()){
                            leerImagen(documento.id, documento.data["nombre"].toString())
                        }
                    }
                }
            }
            .addOnFailureListener() {

                Toast.makeText(this, "Error 1", Toast.LENGTH_SHORT).show();
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun leerImagen(nombreImagen : String, nombreVar : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesPlatillos/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                platillos.add(Platillo(nombreVar, bitmap, nombreImagen))
                iniciarRecycler()
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun iniciarRecycler(){
        rvPlatillos.layoutManager = LinearLayoutManager(this)
        val adapter = PlatilloAdapter(platillos)
        rvPlatillos.adapter = adapter
        adapter.setOnItemClickListener(object : PlatilloAdapter.onItemClickListener{
            override fun onItemClick(id: String) {
                val intent = Intent(this@BuscarPlatilloActivity, PlatilloActivity::class.java)
                intent.putExtra("PlatilloID", id)
                startActivity(intent)
            }
        })
    }
}