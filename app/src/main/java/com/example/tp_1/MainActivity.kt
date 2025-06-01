package com.example.tp_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit


class MainActivity : AppCompatActivity() {

    private var PREF_NAME: String = "MyPreferences" // Nombre de las preferencias

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inicializo el sharedPreferences
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        //validar los inputs
        val btn_login = findViewById<Button>(R.id.button)
        val user_name = findViewById<EditText>(R.id.editTextText)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        //Cuando se intentan loggear
        btn_login.setOnClickListener {

            //Recupero los datos guardados en las sharedPreferences
            val gson = Gson()
            val type = object : TypeToken<MutableList<Persona>>() {}.type
            var lista_personas : MutableList<Persona>

            val personas_json = sharedPreferences.getString("personas","")

            if(personas_json!=null && !personas_json.isEmpty()){
                lista_personas = Gson().fromJson(sharedPreferences?.getString("personas",""),type)

                if(!validar_login(user_name.text.toString(), password.text.toString(),lista_personas)){
                    user_name.error = getString(R.string.user_pass_error)
                    password.error = getString(R.string.user_pass_error)
                    Toast.makeText(this@MainActivity,getString(R.string.user_pass_error), Toast.LENGTH_SHORT).show()
                }else{
                    //guardo el nombre del usuario loggeado
                    sharedPreferences?.edit {
                        this.putString("usuario_loggeado", user_name.text.toString())
                    }

                    //se loggea a la cuenta de la persona
                    val intent =Intent(this, HomeActivity::class.java)
                    val personas_registradas = sharedPreferences?.getString("personas","")

                    intent.putExtra("personas",personas_registradas)
                    intent.putExtra("usuario_loggeado",sharedPreferences?.getString("usuario_loggeado",""))
                    startActivity(intent)

                }
            }else{
                Toast.makeText(this@MainActivity, getString(R.string.user_pass_error), Toast.LENGTH_SHORT).show()
            }


        }

        //Cuando quieren crearse una cuenta
        val btn_registro = findViewById<Button>(R.id.button2)

        btn_registro.setOnClickListener {

            val intent =Intent(this, RegistroActivity::class.java)
            val personas_registradas = sharedPreferences?.getString("personas","")

            intent.putExtra("personas",personas_registradas)

            startActivity(intent)
        }
    }

    private fun validar_login(user_name: String, password : String, lista : List<Persona>) : Boolean{

        var isCorrect = false

        lista.forEach { persona ->
            if(persona.user_name.equals(user_name) && persona.password.equals(password)){
                isCorrect = true
            }
        }
        return isCorrect
    }
}