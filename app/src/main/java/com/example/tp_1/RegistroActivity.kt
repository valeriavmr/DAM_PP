package com.example.tp_1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.datos.FormValidator
import com.example.tp_1.datos.Persona

class RegistroActivity : AppCompatActivity() {
    final lateinit var spinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //inicializo la base de datos
        val db = DatabaseProvider.getDatabase(this)

        //configuro el spinner de genero

        spinner = findViewById(R.id.spinner)

        val generos = resources.getStringArray(R.array.generos)
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, generos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        //cuando se presiona el boton de guardar, creo a la persona
        val btn_guardar = findViewById<Button>(R.id.button3)

        btn_guardar.setOnClickListener {
            val persona = crear_persona()

            //agrego la persona
            if(persona!=null){
                db.personaDao().InsertPersona(persona)

                //Por ultimo, regreso al login
                Toast.makeText(this@RegistroActivity, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }

        val btn_con_cuenta = findViewById<Button>(R.id.button4)

        btn_con_cuenta.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }

    private fun crear_persona(): Persona?{

        val nombre = findViewById<EditText>(R.id.editTextTextNombre).text.toString()
        val apellido = findViewById<EditText>(R.id.editTextTextApellido).text.toString()
        val user_name = findViewById<EditText>(R.id.editTextTextUser).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassRegistro).text.toString()
        val email = findViewById<EditText>(R.id.editTextTextEmail).text.toString()
        val celular = findViewById<EditText>(R.id.editTextTextCel).text.toString()
        val genero = findViewById<Spinner>(R.id.spinner).selectedItem.toString().trim()

        //Si los datos son válidos, se crea una persona nueva
        if(validar_datos(nombre, apellido, user_name, password, email, celular, genero)){
            var persona = Persona()

            //seteo los valores en persona
            persona.id = 0
            persona.nombre = FormValidator.capitalizeWords(nombre)
            persona.apellido = FormValidator.capitalizeWords(apellido)
            persona.user_name = user_name
            persona.password = password
            persona.email = email
            persona.celular = celular
            persona.genero = genero

            return persona
        }else{
            return null
        }
    }

    //función de validación de datos
    private fun validar_datos(nombre: String, apellido: String, user_name: String, password: String, email: String,
                              celular: String, genero: String) : Boolean {

        //me traigo los editText
        val edit_nombre = findViewById<EditText>(R.id.editTextTextNombre)
        val edit_apellido = findViewById<EditText>(R.id.editTextTextApellido)
        val edit_user_name = findViewById<EditText>(R.id.editTextTextUser)
        val edit_password = findViewById<EditText>(R.id.editTextTextPassRegistro)
        val edit_email = findViewById<EditText>(R.id.editTextTextEmail)
        val edit_celular = findViewById<EditText>(R.id.editTextTextCel)

        val db = DatabaseProvider.getDatabase(this@RegistroActivity)

        var esValido = false
        var camposValidados = 0

        //validamos cada uno de los datos ingresados

        //Nombre
        when{
            nombre.isBlank() -> edit_nombre.error = getString(R.string.err_campo_vacio)
            nombre.isDigitsOnly() -> edit_nombre.error = getString(R.string.err_campo_invalido)
            else -> camposValidados++
        }

        //Apellido
        when{
            apellido.isBlank() -> edit_apellido.error = getString(R.string.err_campo_vacio)
            apellido.isDigitsOnly() -> edit_apellido.error = getString(R.string.err_campo_invalido)
            else -> camposValidados++
        }

        //user_name
        val personaUsername = db.personaDao().getPersonaPorUserName(user_name)
        when{
            user_name.isBlank() -> edit_user_name.error = getString(R.string.err_campo_vacio)
            user_name.isDigitsOnly() -> edit_user_name.error = getString(R.string.err_campo_invalido)
            personaUsername!=null -> edit_user_name.error = getString(R.string.user_existente)
            else -> camposValidados++
        }

        //password
        when{
            password.isBlank() -> edit_password.error = getString(R.string.err_campo_vacio)
            !FormValidator.esPassValida(password) -> edit_password.error = getString(R.string.pass_invalida)
            else -> camposValidados++
        }

        //email
        val personaEmail = db.personaDao().getPersonaByEmail(email)
        when{
            email.isBlank() -> edit_email.error = getString(R.string.err_campo_vacio)
            !FormValidator.esEmailValido(email) -> edit_email.error = getString(R.string.err_campo_invalido)
            personaEmail!=null -> edit_email.error = getString(R.string.email_existente)
            else -> camposValidados++
        }

        //telefono
        when{
            celular.isBlank() -> edit_celular.error = getString(R.string.err_campo_vacio)
            !FormValidator.esCelValid(celular) -> edit_celular.error = getString(R.string.err_campo_invalido)
            else -> camposValidados++
        }

        //genero
        if (genero == getString(R.string.select_gen)) {
            // Mostrar error
            Toast.makeText(this@RegistroActivity, getString(R.string.toast_sel_gen), Toast.LENGTH_SHORT).show()
        }else{
            camposValidados++
        }

        //Solo si todos son válidos
        if(camposValidados == 7){
            esValido = true
        }

        return esValido
    }
}