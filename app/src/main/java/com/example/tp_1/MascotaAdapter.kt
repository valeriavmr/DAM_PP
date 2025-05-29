package com.example.tp_1

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MascotaAdapter(private val lista_mascotas : List<Mascota>)
: RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int
    ) {
        holder.tvNombre.text = "Nombre: ${lista_mascotas[position].nombre}"
        holder.tvEdad.text = "Edad: ${lista_mascotas[position].edad} años"
        holder.tvColor.text = "Color: ${lista_mascotas[position].color}"
        holder.tvSexoMascota.text = "Sexo: ${lista_mascotas[position].sexo}"

    }

    override fun getItemCount(): Int = lista_mascotas.size

    inner class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.lista_nombre_mascota)
        val tvEdad : TextView = itemView.findViewById(R.id.lista_mascota_edad)
        val tvColor : TextView = itemView.findViewById(R.id.lista_mascota_color)
        val tvSexoMascota : TextView = itemView.findViewById(R.id.lista_mascota_sexo)
    }

}