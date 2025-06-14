package com.example.tp_1.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tp_1.HomeActivity
import com.example.tp_1.MainActivity
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.ui.EditarPersonaDialog
import com.example.tp_1.datos.Persona
import com.example.tp_1.R
import androidx.core.content.edit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {
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
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inicializo la base de datos
        val db = DatabaseProvider.getDatabase(requireContext())

        //llenar la información del perfil

        val prefs = requireContext().getSharedPreferences("sesion",Context.MODE_PRIVATE)
        val user_name = prefs.getString("usuario_loggeado","")

        //Busco al usuario en la lista de personas
        if(!user_name?.isEmpty()!!){

            val perfilNombre = view.findViewById<TextView>(R.id.profileName)
            val userName = view.findViewById<TextView>(R.id.profileUserName)
            val password = view.findViewById<TextView>(R.id.profilePassword)
            val email = view.findViewById<TextView>(R.id.profileEmail)
            val cel = view.findViewById<TextView>(R.id.profileCel)
            val genero = view.findViewById<TextView>(R.id.profileGenero)
            val mascotas_tv = view.findViewById<TextView>(R.id.profile_mascotas)

            val imagen = view.findViewById<ImageView>(R.id.profileImage)

            var persona = db.personaDao().getPersonaPorUserName(user_name)

            if(persona?.genero == resources.getString(R.string.genero_F)){
                imagen.setImageResource(R.drawable.mascota)
            }else{
                imagen.setImageResource(R.drawable.mascota__1)
            }

            perfilNombre.text = "${persona?.nombre} ${persona?.apellido}"
            userName.text = "Nombre de usuario: ${persona?.user_name}"
            genero.text = "Género: ${persona?.genero}"
            password.text = "Contraseña: ${persona?.password}"
            email.text = "e-mail: ${persona?.email}"
            cel.text = "Celular de contacto: ${persona?.celular}"

            //mascotas
            var mascotas =""
            var lista_mascotas = db.personaDao().obtenerPersonaConMascotas(persona!!.id).mascotas

            lista_mascotas.forEach { mascota ->
                mascotas+="${mascota.nombre} - ${mascota.edad} año(s) - ${mascota.sexo} - ${mascota.color}\n"
            }
            mascotas_tv.text = mascotas

            //Logica de los botones
            val btnEditar = view.findViewById<Button>(R.id.btn_editar_persona)
            val btnEliminar = view.findViewById<Button>(R.id.btn_eliminar_persona)

            //opcion editar
            btnEditar.setOnClickListener {
                //Aquí tengo que poner la logica de update
                val dialog = EditarPersonaDialog(persona) { personaEditada ->
                    //llamo al dialog, recupero los campos modificados y hago el update
                    db.personaDao().actualizarPersona(personaEditada)
                    actualizarUI(personaEditada)

                    //Muestro el mensaje de que se actualizo y muestro los datos nuevos
                    Toast.makeText(requireContext(), "Persona actualizada", Toast.LENGTH_SHORT)
                        .show()
                    val prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
                    prefs.edit().putString("usuario_loggeado", personaEditada.user_name).apply()
                    (activity as? HomeActivity)?.actualizarHeaderNavigationView(personaEditada.user_name)
                }

                dialog.show(parentFragmentManager, "EditarPersonaDialog")
            }

            //Opcion eliminar
            btnEliminar.setOnClickListener {

                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Cuenta")
                    .setMessage("¿Estás seguro de que querés eliminar tu cuenta?")
                    .setPositiveButton("Sí") { _, _ ->

                        //Borro primero las mascotas de la persona
                        db.mascotaDao().borrarMascotasPersona(persona.id)

                        //borro a la persona y me voy al login
                        db.personaDao().DeletePersona(persona)
                        Toast.makeText(requireContext(), "Persona borrada exitosamente", Toast.LENGTH_LONG)
                            .show()

                        //Me voy al login
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarUI(persona: Persona) {
        val nombreTextView = view?.findViewById<TextView>(R.id.profileName)
        val generoTextView = view?.findViewById<TextView>(R.id.profileGenero)
        val userNameTextView = view?.findViewById<TextView>(R.id.profileUserName)
        val passwordTextView = view?.findViewById<TextView>(R.id.profilePassword)
        val emailTextView = view?.findViewById<TextView>(R.id.profileEmail)
        val celTextView = view?.findViewById<TextView>(R.id.profileCel)

        val imagen = view?.findViewById<ImageView>(R.id.profileImage)

        if(persona.genero == resources.getString(R.string.genero_F)){
            imagen?.setImageResource(R.drawable.mascota)
        }else{
            imagen?.setImageResource(R.drawable.mascota__1)
        }

        nombreTextView?.text = "${persona.nombre} ${persona.apellido}"
        generoTextView?.text = "Género: ${persona.genero}"
        userNameTextView?.text = "Usuario: ${persona.user_name}"
        passwordTextView?.text = "Contraseña: ${persona.password}"
        emailTextView?.text = "e-mail: ${persona.email}"
        celTextView?.text = "Celular de contacto: ${persona.celular}"
    }}