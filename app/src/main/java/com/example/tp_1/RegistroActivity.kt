package com.example.tp_1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegistroActivity : AppCompatActivity() {
    final lateinit var spinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //configuro el spinner de genero

        spinner = findViewById(R.id.spinner)

        val generos = listOf("Seleccione su género", "Masculino", "Femenino")
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, generos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val personas_guardadas = intent.getStringExtra("personas")

        //Transformo el json en una lista de personas
        val gson = Gson()

        var lista_personas = mutableListOf<Persona>()
        if(personas_guardadas!=null && !personas_guardadas.isEmpty()){

            val type = object : TypeToken<MutableList<Persona>>() {}.type
            lista_personas = Gson().fromJson(personas_guardadas,type)

        }

        //cuando se presiona el boton de guardar, creo a la persona
        val btn_guardar = findViewById<Button>(R.id.button3)

        btn_guardar.setOnClickListener {
            val persona = crear_persona(lista_personas)

            //guardo a la persona en el sharedPreferences
            if (persona != null) {
                lista_personas.add(persona)
                val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                val editar = sharedPreferences.edit()
                val nueva_lista_json = gson.toJson(lista_personas)
                editar.putString("personas", nueva_lista_json)
                editar.apply()

                //Por ultimo, regreso al login
                Toast.makeText(this@RegistroActivity, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("personas",sharedPreferences.getString("personas",""))
                startActivity(intent)
            }

        }

        val btn_con_cuenta = findViewById<Button>(R.id.button4)

        btn_con_cuenta.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("personas", getSharedPreferences("MyPreferences",MODE_PRIVATE).getString("personas",""))

            startActivity(intent)

        }

    }

    private fun crear_persona(personas_guardadas: List<Persona>):Persona ?{

        val nombre = findViewById<EditText>(R.id.editTextTextNombre).text.toString()
        val apellido = findViewById<EditText>(R.id.editTextTextApellido).text.toString()
        val user_name = findViewById<EditText>(R.id.editTextTextUser).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassRegistro).text.toString()
        val email = findViewById<EditText>(R.id.editTextTextEmail).text.toString()
        val celular = findViewById<EditText>(R.id.editTextTextCel).text.toString()
        val genero = findViewById<Spinner>(R.id.spinner).selectedItem.toString().trim()

        //Si los datos son válidos, se crea una persona nueva
        if(validar_datos(nombre, apellido, user_name, password, email, celular, personas_guardadas,genero)){
            var persona = Persona()

            //seteo los valores en persona
            persona.nombre = nombre
            persona.apellido = apellido
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
        celular: String, personas_guardadas: List<Persona>, genero: String) : Boolean {

        //me traigo los editText
        val edit_nombre = findViewById<EditText>(R.id.editTextTextNombre)
        val edit_apellido = findViewById<EditText>(R.id.editTextTextApellido)
        val edit_user_name = findViewById<EditText>(R.id.editTextTextUser)
        val edit_password = findViewById<EditText>(R.id.editTextTextPassRegistro)
        val edit_email = findViewById<EditText>(R.id.editTextTextEmail)
        val edit_celular = findViewById<EditText>(R.id.editTextTextCel)

        var esValido = false
        var camposValidados = 0

        //validamos cada uno de los datos ingresados
        //Nombre
        if(nombre.trim().isEmpty()){
            edit_nombre.error = "El nombre no puede estar vacío"
        }else{
            if(nombre.isDigitsOnly()){
                edit_nombre.error = "Nombre inválido"
            }else{
                camposValidados++
            }
        }

        //Apellido
        if(apellido.trim().isEmpty()){
            edit_apellido.error = "El apellido no puede estar vacío"
        }else{
            if(apellido.isDigitsOnly()){
                edit_apellido.error = "Apellido inválido"
            }else{
                camposValidados++
            }
        }

        //user_name
        if(user_name.trim().isEmpty()){
            edit_user_name.error = "El nombre de usuario no puede estar vacío"
        }else{
            if(user_name.isDigitsOnly()){
                edit_user_name.error = "Nombre de usuario inválido"
            }else{
                if(personas_guardadas.size!=0){
                    personas_guardadas.forEach { persona ->
                        if(persona.user_name.equals(user_name)) {
                            edit_user_name.error = "Nombre de usuario ya existente"
                            return false
                        }else{
                            camposValidados++
                        }

                    }
                }else{camposValidados++}
            }
        }

        //password
        if(password.trim().isEmpty()){
            edit_password.error = "La contraseña no puede estar vacía"
        }else{
            if(!FormValidator.esPassValida(password)){
                edit_password.error = "La contraseña debe contener 8 caracteres y al menos un número"
            }else{
                camposValidados++
            }
        }

        //email
        if(email.trim().isEmpty()){
            edit_email.error = "El email no puede estar vacío"
        }else{
            if(!FormValidator.esEmailValido(email)){
                edit_email.error = "Email inválido"
            }else{
                if(personas_guardadas.size!=0){
                    personas_guardadas.forEach { persona ->
                        if(persona.email.equals(email)) {
                            edit_email.error = "Email ya registrado"
                            return false
                        }else{
                            camposValidados++
                        }

                    }
                }else{camposValidados++}
            }
        }

        //telefono
        if(celular.trim().isEmpty()){
            edit_celular.error = "El campo celular no puede estar vacío"
        }else{
            if(!FormValidator.esCelValid(celular)){
                edit_celular.error = "Teléfono inválido"
            }else{
                camposValidados++
            }
        }

        //genero
        if (genero == "Seleccione su género") {
            // Mostrar error
            Toast.makeText(this@RegistroActivity, "Por favor seleccione su género", Toast.LENGTH_SHORT).show()
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