package com.itesm.foodykraveproyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class IngredienteAdapter(val ingredientes : List<Ingrediente>) : RecyclerView.Adapter<IngredienteAdapter.IngredienteHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: String)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IngredienteHolder(layoutInflater.inflate(R.layout.ingrediente_item, parent, false), mListener)
    }

    override fun onBindViewHolder(holder: IngredienteHolder, position: Int) {
        holder.render(ingredientes[position])
    }

    override fun getItemCount(): Int {
        return ingredientes.size
    }

    class IngredienteHolder(val view : View, listener: onItemClickListener) : RecyclerView.ViewHolder(view){
        val ingredienteTexto = view.findViewById<TextView>(R.id.ingrediente_texto_item)
        val ingredienteImagen = view.findViewById<ImageView>(R.id.ingrediente_image_item)
        val ingredienteID = view.findViewById<TextView>(R.id.ingrediente_id_item)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(ingredienteID.text.toString())
            }
        }

        fun render(ingredientes : Ingrediente){

            ingredienteTexto.text = ingredientes.nombreIngrediente
            ingredienteImagen.setImageBitmap(ingredientes.imagenBitmap)
            ingredienteID.text = ingredientes.idIngrediente
        }
    }
}