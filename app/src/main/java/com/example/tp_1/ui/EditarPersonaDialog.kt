package com.example.tp_1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.example.tp_1.R
import com.example.tp_1.RegistroActivity
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.datos.FormValidator
import com.example.tp_1.datos.Persona

class EditarPersonaDialog(private val persona: Persona,
                          private val onGuardar: (Persona) -> Unit): DialogFragment() {

    final lateinit var spinner_genero_edit: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_personas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //configuro el spinner
        spinner_genero_edit = view.findViewById(R.id.spinnerGenero_edit)

        val generos = resources.getStringArray(R.array.generos)

        val adapter_genero =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, generos)

        adapter_genero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_genero_edit.adapter = adapter_genero

        //Relleno los campos con los valores actuales

        val nombre = view.findViewById<EditText>(R.id.editTextNombre_edit)
        val apellido = view.findViewById<EditText>(R.id.editTextApellido_edit)
        val user_name = view.findViewById<EditText>(R.id.editTextUserName_edit)
        val password = view.findViewById<EditText>(R.id.editTextPassword_edit)
        val email = view.findViewById<EditText>(R.id.editTextEmail_edit)
        val cel = view.findViewById<EditText>(R.id.editTextCel_edit)
        val genero = spinner_genero_edit

        nombre.setText(persona.nombre)
        apellido.setText(persona.apellido)
        user_name.setText(persona.user_name)
        password.setText(persona.password)
        email.setText(persona.email)
        cel.setText(persona.celular)
        genero.setSelection(generos.indexOf(persona.genero))

        //Me traigo los botones
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar_persona)
        val btnCancelar = view.findViewById<Button>(R.id.btn_cancelar_edit_persona)

        btnGuardar.setOnClickListener {
            //recupero los valores nuevos
            val nuevo_nombre = nombre.text.toString()
            val nuevo_apellido = apellido.text.toString()
            val nuevo_user = user_name.text.toString()
            val nueva_password = password.text.toString()
            val nuevo_email = email.text.toString()
            val nuevo_cel = cel.text.toString()
            val nuevo_genero = spinner_genero_edit.selectedItem.toString()

            if(validarCampos(view,nuevo_nombre,nuevo_apellido,nuevo_user,nueva_password,nuevo_email,nuevo_cel,nuevo_genero)){

                val personaActualizada = persona.copy(
                    id = persona.id,
                    nombre = FormValidator.capitalizeWords(nuevo_nombre),
                    apellido = FormValidator.capitalizeWords(nuevo_apellido),
                    user_name = nuevo_user,
                    password = nueva_password,
                    email = nuevo_email,
                    celular = nuevo_cel,
                    genero = nuevo_genero
                )

                onGuardar(personaActualizada)
                dismiss()

            }
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    //Funcion que valida lo ingresado en los campos

    private fun validarCampos(view : View,nuevo_nombre : String,nuevo_apellido : String ,nuevo_user : String,
                              nueva_password: String, nuevo_email:String,nuevo_cel:String,nuevo_genero : String) : Boolean{

        val db = DatabaseProvider.getDatabase(requireContext())

        val nombre = view.findViewById<EditText>(R.id.editTextNombre_edit)
        val apellido = view.findViewById<EditText>(R.id.editTextApellido_edit)
        val user_name = view.findViewById<EditText>(R.id.editTextUserName_edit)
        val password = view.findViewById<EditText>(R.id.editTextPassword_edit)
        val email = view.findViewById<EditText>(R.id.editTextEmail_edit)
        val cel = view.findViewById<EditText>(R.id.editTextCel_edit)
        val genero = spinner_genero_edit

        var ingresoValido = false
        var camposValidos = 0


        //nombre
        when{
            nuevo_nombre.isBlank() -> nombre.error = getString(R.string.err_campo_vacio)
            nuevo_nombre.isDigitsOnly() -> nombre.error = getString(R.string.err_campo_invalido)
            else -> camposValidos++
        }

        //apellido
        when{
            nuevo_apellido.isBlank() -> apellido.error = getString(R.string.err_campo_vacio)
            nuevo_apellido.isDigitsOnly() -> apellido.error = getString(R.string.err_campo_invalido)
            else -> camposValidos++
        }

        //username
        val personaUser = db.personaDao().getPersonaPorUserName(nuevo_user)
        when{
            nuevo_user.isBlank() -> user_name.error = getString(R.string.err_campo_vacio)
            nuevo_user.isDigitsOnly() -> user_name.error = getString(R.string.err_campo_invalido)
            (personaUser!=null && persona.id != personaUser.id) -> user_name.error = getString(R.string.user_existente)
            else -> camposValidos++
        }

        //password
        when{
            nueva_password.isBlank() -> password.error = getString(R.string.err_campo_vacio)
            !FormValidator.esPassValida(nueva_password) -> password.error = getString(R.string.pass_invalida)
            else -> camposValidos++
        }

        //email
        val personaEmail = db.personaDao().getPersonaByEmail(nuevo_email)
        when{
            nuevo_email.isBlank() -> email.error = getString(R.string.err_campo_vacio)
            !FormValidator.esEmailValido(nuevo_email) -> email.error = getString(R.string.err_campo_invalido)
            (personaEmail!=null && persona.id!=personaEmail.id) -> email.error = getString(R.string.email_existente)
            else -> camposValidos++
        }

        //celular
        when{
            nuevo_cel.isBlank() -> cel.error = getString(R.string.err_campo_vacio)
            !FormValidator.esCelValid(nuevo_cel) -> getString(R.string.err_campo_invalido)
            else -> camposValidos++
        }

        //genero
        if (nuevo_genero == getString(R.string.select_gen)) {
            // Mostrar error
            Toast.makeText(requireContext(), getString(R.string.toast_sel_gen), Toast.LENGTH_SHORT).show()
        }else{
            camposValidos++
        }

        if(camposValidos==7){ingresoValido =true}

        return ingresoValido
    }
}