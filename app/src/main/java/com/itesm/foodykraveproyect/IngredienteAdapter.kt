package com.itesm.foodykraveproyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        val ingredienteImagen = view.findViewById<ImageView>(R.id.ingrediente_item_img)
        val ingredienteTexto = view.findViewById<TextView>(R.id.ingrediente_item_txt)
        val ingredienteID = view.findViewById<TextView>(R.id.ingrediente_item_id)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(ingredienteID.text.toString())
            }
        }

        fun render(ingredientes : Ingrediente){
            ingredienteImagen.setImageBitmap(ingredientes.imagenBitmap)
            ingredienteTexto.text = ingredientes.nombreIngrediente
            ingredienteID.text = ingredientes.idIngrediente
        }
    }
}