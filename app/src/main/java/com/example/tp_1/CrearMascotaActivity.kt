package com.example.tp_1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tp_1.RegistroActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CrearMascotaActivity : AppCompatActivity() {

    final lateinit var spinner_color : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_mascota)

        //Configuro los spinners
        spinner_color = findViewById(R.id.spinnerColorMascota)

        val colores = mutableListOf<String>("Seleccione el color de su mascota", "negro", "blanco",
            "manchado","rayado","dorado","gris")

        val adapter_colores = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, colores)
        adapter_colores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_color.adapter = adapter_colores

        //Recupero al usuario
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        val user_name = intent.getStringExtra("usuario_loggeado")

        val personas_guardadas = intent.getStringExtra("personas")

        //Transformo el json en una lista de personas
        val gson = Gson()

        var lista_personas = mutableListOf<Persona>()
        if(personas_guardadas!=null && !personas_guardadas.isEmpty()){

            val type = object : TypeToken<MutableList<Persona>>() {}.type
            lista_personas = Gson().fromJson(personas_guardadas,type)

        }

        var persona_mascota : Persona? = null

        lista_personas.forEach { persona ->
            if(persona.user_name.equals(user_name)){
                persona_mascota = persona
            }
        }

        lista_personas.remove(persona_mascota)

        //crear mascota
        val btn_crear_mascota = findViewById<Button>(R.id.button8)

        btn_crear_mascota.setOnClickListener {

            val mascota = crearMascota()

            //Si se crea la mascota, se guarda en la sharedPreferences
            if(persona_mascota!=null && mascota!=null){
                persona_mascota.mascotas.add(mascota)
                lista_personas.add(persona_mascota)
                val nueva_lista_json = gson.toJson(lista_personas)
                edit.putString("personas", nueva_lista_json)
                edit.apply()
                Toast.makeText(this@CrearMascotaActivity, "Se agregó a la mascota exitosamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("personas", sharedPreferences.getString("personas",""))
                intent.putExtra("usuario_loggeado",user_name)
                startActivity(intent)
            }

        }

        //Regresar
        val btn_volver = findViewById<Button>(R.id.button9)

        btn_volver.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("personas", sharedPreferences.getString("personas",""))
            intent.putExtra("usuario_loggeado",user_name)
            startActivity(intent)
        }


    }

    //funcion de crear mascotas
    private fun crearMascota(): Mascota?{

        var mascota: Mascota? = null

        //Recupero los valores
        val nombre_mascota = findViewById<EditText>(R.id.editTextNombreMascota).text.toString()
        val edad_mascota = findViewById<EditText>(R.id.editTextEdadMascota).text.toString()
        val color_mascota = spinner_color.selectedItem.toString()
        val sexo = when (findViewById<RadioGroup>(R.id.rgSexoMascota).checkedRadioButtonId) {
            R.id.rbMacho -> "Macho"
            R.id.rbHembra -> "Hembra"
            else -> ""
        }

        //Solo creo la mascota si los datos son correctos
        if(validarInfoMascota(nombre_mascota, edad_mascota, color_mascota, sexo)){
            mascota = Mascota(nombre_mascota, edad_mascota.toInt(),color_mascota, sexo)
        }

        return mascota

    }

    //función que valida los datos de la mascota
    private fun validarInfoMascota(nombre_mascota: String, edad_mascota: String, color_mascota: String, sexo: String): Boolean {
        //inicializo las variables
        var esValido = false
        var camposValidos = 0
        val ed_nombre_mascota = findViewById<EditText>(R.id.editTextNombreMascota)
        val ed_edad_mascota = findViewById<EditText>(R.id.editTextEdadMascota)
        val rgSexoMascota = findViewById<RadioGroup>(R.id.rgSexoMascota)
        val sexoSeleccionadoId = rgSexoMascota.checkedRadioButtonId

        //nombre de la mascota
        if(nombre_mascota.trim().isEmpty()){
            ed_nombre_mascota.error = "El nombre de la mascota no puede estar vacío."
        }else{
            if(nombre_mascota.isDigitsOnly()){
                ed_nombre_mascota.error = "Nombre inválido."
            }else{
                camposValidos++
            }
        }

        //Edad de mascota
        if(edad_mascota.trim().isEmpty()){
            ed_edad_mascota.error = "El campo de edad no puede estar vacío."
        }else{
            if(!edad_mascota.isDigitsOnly()){
                ed_edad_mascota.error = "Edad ingresada inválida."
            }else{
                camposValidos++
            }
        }

        //color
        if(color_mascota == "Seleccione el color de su mascota"){
            Toast.makeText(this@CrearMascotaActivity, "Por favor seleccione el color de su mascota",
                Toast.LENGTH_SHORT).show()
        }else{
            camposValidos++
        }

        //Sexo de la mascota
        if (sexoSeleccionadoId == -1) {
            Toast.makeText(this, "Debe seleccionar el sexo de la mascota", Toast.LENGTH_SHORT).show()
        }else{camposValidos++}

        if(camposValidos == 4){
            esValido = true
        }

        return esValido
    }
}
