package com.example.tp_1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MascotasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MascotasFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mascotas, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MascotasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MascotasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lista_mascotas_view = view.findViewById<RecyclerView>(R.id.lista_mascotas)
        val tvMascotas = view.findViewById<TextView>(R.id.textView4)
        lista_mascotas_view.layoutManager = LinearLayoutManager(requireContext())

        //Busco al dueño de las mascotas
        val sharedPref = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        var lista_personas = mutableListOf<Persona>()
        val type = object : TypeToken<MutableList<Persona>>() {}.type
        lista_personas = Gson().fromJson(sharedPref.getString("personas",""),type)
        val dueno_mascotas = sharedPref.getString("usuario_loggeado","")

        //configuro el boton de agregar mascotas
        val btn_add_mascota = view.findViewById<Button>(R.id.button7)

        btn_add_mascota.setOnClickListener {
            val intent = Intent(requireContext(), CrearMascotaActivity()::class.java)
            intent.putExtra("personas",sharedPref.getString("personas",""))
            intent.putExtra("usuario_loggeado",sharedPref.getString("usuario_loggeado",""))

            startActivity(intent)
        }

        var lista_mascotas = mutableListOf<Mascota>()

        lista_personas.forEach { persona ->
            if(persona.user_name.equals(dueno_mascotas)){
                lista_mascotas = persona.mascotas
                tvMascotas.text = "Mascotas de ${persona.nombre}"
            }
        }
        lista_mascotas_view.adapter = MascotaAdapter(lista_mascotas)
    }
}