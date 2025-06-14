package com.example.tp_1.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_1.CrearMascotaActivity
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.ui.MascotaAdapter
import com.example.tp_1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    private lateinit var adapter: MascotaAdapter
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

        val db = DatabaseProvider.getDatabase(requireContext())

        val lista_mascotas_view = view.findViewById<RecyclerView>(R.id.lista_mascotas)
        val tvMascotas = view.findViewById<TextView>(R.id.textView4)
        lista_mascotas_view.layoutManager = LinearLayoutManager(requireContext())

        //Busco al dueño de las
        val prefs = requireContext().getSharedPreferences("sesion",Context.MODE_PRIVATE)
        val dueno_mascotas = prefs.getString("usuario_loggeado","")

        val persona_dueno = db.personaDao().getPersonaPorUserName(dueno_mascotas.toString())

        //configuro los botones
        val btn_add_mascota = view.findViewById<FloatingActionButton>(R.id.floating_btn_add_mascota)
        val btn_del_mascotas = view.findViewById<FloatingActionButton>(R.id.floating_btn_delete_all)

        //agregar mascota
        btn_add_mascota.setOnClickListener {
            val intent = Intent(requireContext(), CrearMascotaActivity()::class.java)
            intent.putExtra("usuario_loggeado",dueno_mascotas)

            startActivity(intent)
        }

        var lista_mascotas = db.personaDao().obtenerPersonaConMascotas(persona_dueno!!.id).mascotas

        tvMascotas.text = "Mascotas de ${persona_dueno.nombre}"


        adapter = MascotaAdapter(lista_mascotas) { mascotaSeleccionada ->
            val fragment = DetalleMascotaFragment.Companion.newInstance(
                mascotaSeleccionada.nombre,
                dueno_mascotas.toString()
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
        lista_mascotas_view.adapter = adapter

        //borrar todas las mascotas de la persona
        btn_del_mascotas.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar todas las mascotas")
                .setMessage("¿Estás seguro de que querés eliminar todas tus mascotas?")
                .setPositiveButton("Sí") { _, _ ->

                    //Elimino las mascotas
                    db.mascotaDao().borrarMascotasPersona(persona_dueno.id)

                    //Me regreso a home y muestro una noti de que se elimino
                    Toast.makeText(requireContext(),"Mascotas eliminadas exitosamente", Toast.LENGTH_SHORT).show()

                    adapter.actualizarLista(db.mascotaDao().getAllMascotasDePersona(persona_dueno.id))

                    requireActivity().supportFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMascotas()
    }

    private fun cargarMascotas() {
        //Busco al dueño de las mascotas
        val dueno_mascotas = arguments?.getString("usuario_loggeado")?: return
        val db = DatabaseProvider.getDatabase(requireContext())
        val persona_dueno = db.personaDao().getPersonaPorUserName(dueno_mascotas.toString())
        val mascotas = db.mascotaDao().getAllMascotasDePersona(persona_dueno!!.id)
        adapter.actualizarLista(mascotas)
    }
}