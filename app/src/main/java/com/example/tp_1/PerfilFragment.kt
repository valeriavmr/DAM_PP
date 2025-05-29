package com.example.tp_1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        savedInstanceState: Bundle?
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

        //llenar la información del perfil
        val sharedPref = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val user_name = sharedPref.getString("usuario_loggeado","")

        //Busco al usuario en la lista de personas
        if(user_name!=null && !user_name.isEmpty()){

            val perfilNombre = view.findViewById<TextView>(R.id.profileName)
            val userName = view.findViewById<TextView>(R.id.profileUserName)
            val password = view.findViewById<TextView>(R.id.profilePassword)
            val email = view.findViewById<TextView>(R.id.profileEmail)
            val cel = view.findViewById<TextView>(R.id.profileCel)
            val genero = view.findViewById<TextView>(R.id.profileGenero)
            val mascotas_tv = view.findViewById<TextView>(R.id.profile_mascotas)

            val imagen = view.findViewById<ImageView>(R.id.profileImage)

            val gson = Gson()
            var lista_personas = mutableListOf<Persona>()
            val type = object : TypeToken<MutableList<Persona>>() {}.type
            lista_personas = Gson().fromJson(sharedPref.getString("personas",""),type)

            lista_personas.forEach { persona ->
                if(persona.user_name.equals(user_name)){

                    if(persona.genero.equals("Femenino")){
                        imagen.setImageResource(R.drawable.mascota)
                    }else{
                        imagen.setImageResource(R.drawable.mascota__1)
                    }

                    perfilNombre.text = "${persona.nombre} ${persona.apellido}"
                    userName.text = "Nombre de usuario: ${persona.user_name}"
                    genero.text = "Género: ${persona.genero}"
                    password.text = "Contraseña: ${persona.password}"
                    email.text = "e-mail: ${persona.email}"
                    cel.text = "Celular de contacto: ${persona.celular}"
                    //las mascotas
                    var mascotas =""
                    persona.mascotas.forEach { mascota ->
                        mascotas+="${mascota.nombre} - ${mascota.edad} año(s) - ${mascota.sexo} - ${mascota.color}\n"
                    }
                    mascotas_tv.text = mascotas
                }
            }

        }
    }
}