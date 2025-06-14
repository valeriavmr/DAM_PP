package com.example.tp_1.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.ui.EditarMascotaDialog
import com.example.tp_1.datos.Mascota
import com.example.tp_1.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetalleMascotaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetalleMascotaFragment : Fragment() {

    private var nombre: String? = null
    private var usuario_loggeado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            nombre = arguments?.getString("nombre")
            usuario_loggeado = arguments?.getString("usuario_loggeado")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_mascota, container, false)
    }

    companion object {

        fun newInstance(nombre: String, usuario_loggeado: String): DetalleMascotaFragment {
            val fragment = DetalleMascotaFragment()
            val args = Bundle()
            args.putString("nombre", nombre)
            args.putString("usuario_loggeado", usuario_loggeado)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inicializo la base de datos
        val db = DatabaseProvider.getDatabase(requireContext())

        //Me traigo los tv
        val tvNombreMascota = view.findViewById<TextView>(R.id.MascotaName)
        val tvEdadMascota = view.findViewById<TextView>(R.id.MascotaEdad)
        val tvSexoMascota = view.findViewById<TextView>(R.id.MascotaGenero)
        val tvMascotaColor = view.findViewById<TextView>(R.id.MascotaColor)

        val nombreMascota = nombre

        val prefs = requireContext().getSharedPreferences("sesion",Context.MODE_PRIVATE)
        val user_name = prefs.getString("usuario_loggeado","")

       val persona = db.personaDao().getPersonaPorUserName(user_name.toString())
       var mascota = db.mascotaDao().getMascotaByNombre(nombreMascota.toString(), persona!!.id)

        tvNombreMascota.text = "Nombre: ${mascota?.nombre}"
        tvEdadMascota.text = "Edad: ${mascota?.edad.toString()} año(s)"
        tvSexoMascota.text = "Sexo: ${mascota?.sexo}"
        tvMascotaColor.text = "Color: ${mascota?.color}"

        //Logica de los botones
        val btnEditar = view.findViewById<Button>(R.id.btn_editar)
        val btnEliminar = view.findViewById<Button>(R.id.btn_eliminar)

        btnEditar.setOnClickListener {
            //Aquí tengo que poner la logica de update
            val dialog = EditarMascotaDialog(mascota!!) { mascotaEditada ->
                //llamo al dialog, recupero los campos modificados y hago el update
                db.mascotaDao().updateMascota(mascotaEditada)
                actualizarUI(mascotaEditada)

                //Muestro el mensaje de que se actualizo y muestro los datos nuevos
                Toast.makeText(requireContext(), "Mascota actualizada", Toast.LENGTH_SHORT).show()
            }

            dialog.show(parentFragmentManager, "EditarMascotaDialog")
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar mascota")
                .setMessage("¿Estás seguro de que querés eliminar esta mascota?")
                .setPositiveButton("Sí") { _, _ ->

                    //Elimino la mascota
                    db.mascotaDao().deleteMascota(mascota!!)

                    //Me regreso a home y muestro una noti de que se elimino
                    Toast.makeText(requireContext(),"Mascota eliminada exitosamente", Toast.LENGTH_SHORT).show()

                    requireActivity().supportFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarUI(mascota: Mascota) {
        view?.findViewById<TextView>(R.id.MascotaName)?.text = mascota.nombre
        view?.findViewById<TextView>(R.id.MascotaEdad)?.text = "${mascota.edad} año(s)"
        view?.findViewById<TextView>(R.id.MascotaColor)?.text = mascota.color
        view?.findViewById<TextView>(R.id.MascotaGenero)?.text = mascota.sexo
    }
}