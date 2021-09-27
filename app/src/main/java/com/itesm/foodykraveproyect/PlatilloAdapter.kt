package com.itesm.foodykraveproyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlatilloAdapter(val platillos : List<Platillo>) : RecyclerView.Adapter<PlatilloAdapter.PlatilloHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: String)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatilloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlatilloHolder(layoutInflater.inflate(R.layout.plantillo_item, parent, false), mListener)
    }

    override fun onBindViewHolder(holder: PlatilloHolder, position: Int) {
        holder.render(platillos[position])
    }

    override fun getItemCount(): Int {
        return platillos.size
    }

    class PlatilloHolder(val view : View, listener: onItemClickListener) : RecyclerView.ViewHolder(view){
        val platilloTexto = view.findViewById<TextView>(R.id.platillo_texto_item)
        val platilloImagen = view.findViewById<ImageView>(R.id.platillo_image_item)
        val platilloID = view.findViewById<TextView>(R.id.platillo_id_item)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(platilloID.text.toString())
            }
        }

        fun render(platillos : Platillo){

            platilloTexto.text = platillos.nombrePlatillo
            platilloImagen.setImageBitmap(platillos.imagenPlatilloBitmap)
            platilloID.text = platillos.idPlatillo
        }
    }
}