package com.example.tp_1

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PersonaAdapter(private val lista_personas : List<Persona>)
    : RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    inner class PersonaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.lista_personas_nombre_completo)
        val tvUser : TextView = itemView.findViewById(R.id.lista_personas_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_persona, parent, false)
        return PersonaViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        holder.tvNombre.text = "${lista_personas[position].nombre} ${lista_personas[position].apellido}"
        holder.tvUser.text = "Usuario: ${lista_personas[position].user_name}"
    }

    override fun getItemCount(): Int = lista_personas.size
}