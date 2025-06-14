package com.example.tp_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.datos.Persona


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Pongo el tema claro por defecto
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //inicializo la base de datos
        val db = DatabaseProvider.getDatabase(this)

        //validar los inputs
        val btn_login = findViewById<Button>(R.id.button)
        val user_name = findViewById<EditText>(R.id.editTextText)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        //Recupero las personas registradas
        var lista_personas : List<Persona>

        lista_personas = db.personaDao().getAllPersonas()

        //Cuando se intentan loggear
        btn_login.setOnClickListener {

            if(!lista_personas.isEmpty()){
                if(!validar_login(user_name.text.toString(), password.text.toString(),lista_personas)){
                    user_name.error = getString(R.string.user_pass_error)
                    password.error = getString(R.string.user_pass_error)
                    Toast.makeText(this@MainActivity,getString(R.string.user_pass_error), Toast.LENGTH_SHORT).show()
                }else{

                    //se loggea a la cuenta de la persona
                    val intent =Intent(this, HomeActivity::class.java)
                    val prefs = this.getSharedPreferences("sesion", Context.MODE_PRIVATE)
                    prefs.edit().putString("usuario_loggeado", user_name.text.toString()).apply()

                    intent.putExtra("usuario_loggeado",prefs.getString("usuario_loggeado",""))
                    startActivity(intent)

                }
            }else{
                user_name.error = getString(R.string.user_pass_error)
                password.error = getString(R.string.user_pass_error)
                Toast.makeText(this@MainActivity, getString(R.string.user_pass_error), Toast.LENGTH_SHORT).show()
            }

        }

        //Cuando quieren crearse una cuenta
        val btn_registro = findViewById<Button>(R.id.button2)

        btn_registro.setOnClickListener {

            val intent =Intent(this, RegistroActivity::class.java)

            startActivity(intent)
        }
    }


    //función que valida que el username y la contraseña sean correctos
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