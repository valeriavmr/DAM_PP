package com.example.tp_1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_1.R
import com.example.tp_1.datos.Mascota

class MascotaAdapter(private var lista_mascotas : List<Mascota>, private val onClick: (Mascota) -> Unit)
: RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int
    ) {
        val mascota = lista_mascotas[position]
        holder.tvPosicion.text = (position+1).toString()
        holder.tvNombre.text = lista_mascotas[position].nombre
        holder.itemView.setOnClickListener { onClick(mascota) }

    }

    override fun getItemCount(): Int = lista_mascotas.size

    inner class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.lista_nombre_mascota)
        val tvPosicion : TextView = itemView.findViewById(R.id.lista_mascota_posicion)
    }

    fun actualizarLista(nuevaLista: List<Mascota>) {
        this.lista_mascotas = nuevaLista
        notifyDataSetChanged()
    }

}