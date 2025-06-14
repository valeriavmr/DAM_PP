package com.example.tp_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.datos.FormValidator
import com.example.tp_1.datos.Mascota

class CrearMascotaActivity : AppCompatActivity() {

    final lateinit var spinner_color : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_mascota)

        val db = DatabaseProvider.getDatabase(this)

        //Configuro los spinners
        spinner_color = findViewById(R.id.spinnerColorMascota)

        val colores = resources.getStringArray(R.array.colores_mascota)

        val adapter_colores = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, colores)
        adapter_colores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_color.adapter = adapter_colores

        //Recupero al usuario
        val prefs = this.getSharedPreferences("sesion", Context.MODE_PRIVATE)

        val user_name = prefs.getString("usuario_loggeado","")

        val persona_mascota = db.personaDao().getPersonaPorUserName(user_name.toString())

        //crear mascota
        val btn_crear_mascota = findViewById<Button>(R.id.button8)

        btn_crear_mascota.setOnClickListener {

            val mascota = crearMascota(persona_mascota!!.id)

            //Si se crea la mascota, se guarda en la sharedPreferences
            if(mascota!=null){
                //agrego la mascota
                db.mascotaDao().insertMascota(mascota)
                Toast.makeText(this@CrearMascotaActivity, "Se agregó a la mascota exitosamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("usuario_loggeado",user_name)
                startActivity(intent)
            }

        }

        //Regresar
        val btn_volver = findViewById<Button>(R.id.button9)

        btn_volver.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("usuario_loggeado",user_name)
            startActivity(intent)
        }


    }

    //funcion de crear mascotas
    private fun crearMascota(personaId:Int): Mascota?{

        var mascota: Mascota? = null

        //Recupero los valores
        val nombre_mascota = findViewById<EditText>(R.id.editTextNombreMascota).text.toString()
        val edad_mascota = findViewById<EditText>(R.id.editTextEdadMascota).text.toString()
        val color_mascota = spinner_color.selectedItem.toString()
        val sexo = when (findViewById<RadioGroup>(R.id.rgSexoMascota).checkedRadioButtonId) {
            R.id.rbMacho -> getString(R.string.mascota_M)
            R.id.rbHembra -> getString(R.string.mascota_F)
            else -> ""
        }

        //Solo creo la mascota si los datos son correctos
        if(validarInfoMascota(personaId, nombre_mascota, edad_mascota, color_mascota)){
            mascota =
                Mascota(0, FormValidator.capitalizeWords(nombre_mascota), edad_mascota.toInt(),
                    color_mascota, sexo, personaId)
        }

        return mascota

    }

    //función que valida los datos de la mascota
    private fun validarInfoMascota(personaId: Int,nombre_mascota: String, edad_mascota: String,
                                   color_mascota: String): Boolean {

        //base de datos
        val db = DatabaseProvider.getDatabase(this@CrearMascotaActivity)

        //inicializo las variables
        var esValido = false
        var camposValidos = 0
        val ed_nombre_mascota = findViewById<EditText>(R.id.editTextNombreMascota)
        val ed_edad_mascota = findViewById<EditText>(R.id.editTextEdadMascota)
        val rgSexoMascota = findViewById<RadioGroup>(R.id.rgSexoMascota)
        val sexoSeleccionadoId = rgSexoMascota.checkedRadioButtonId

        //nombre de la mascota
        val mascotaNombre = db.mascotaDao().getMascotaByNombre(nombre_mascota,personaId)
        when{
            nombre_mascota.isBlank() -> ed_nombre_mascota.error = getString(R.string.err_campo_vacio)
            nombre_mascota.isDigitsOnly() -> ed_nombre_mascota.error = getString(R.string.err_campo_invalido)
            mascotaNombre!=null -> ed_nombre_mascota.error = getString(R.string.err_mascota_existente)
            else -> camposValidos++
        }

        //Edad de mascota
        when{
            edad_mascota.isBlank() -> ed_edad_mascota.error = getString(R.string.err_campo_vacio)
            (!edad_mascota.isDigitsOnly() || edad_mascota.toInt() < 1) -> ed_edad_mascota.error = getString(R.string.err_campo_invalido)
            else -> camposValidos++
        }

        //color
        if(color_mascota == resources.getStringArray(R.array.colores_mascota).get(0)){
            Toast.makeText(this@CrearMascotaActivity, getString(R.string.toast_sel_color),
                Toast.LENGTH_SHORT).show()
        }else{
            camposValidos++
        }

        //Sexo de la mascota
        if (sexoSeleccionadoId == -1) {
            Toast.makeText(this, getString(R.string.toast_sel_sex_mascota), Toast.LENGTH_SHORT).show()
        }else{camposValidos++}

        if(camposValidos == 4){
            esValido = true
        }

        return esValido
    }
}
