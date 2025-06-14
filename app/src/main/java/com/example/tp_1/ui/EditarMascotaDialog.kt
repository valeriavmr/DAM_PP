package com.example.tp_1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.example.tp_1.R
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.datos.FormValidator
import com.example.tp_1.datos.Mascota

class EditarMascotaDialog(private val mascota: Mascota,
                          private val onGuardar: (Mascota) -> Unit) : DialogFragment(){

    final lateinit var spinner_color_edit: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Configuro los spinners
        spinner_color_edit = view.findViewById(R.id.spinnerColorMascota_edit)

        val colores = resources.getStringArray(R.array.colores_mascota)

        val adapter_colores = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, colores
        )
        adapter_colores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_color_edit.adapter = adapter_colores

        //me traigo los valores actuales de los campos
        val nombre_nuevo = view.findViewById<EditText>(R.id.editTextNombreMascota_edit)
        val edad_nueva = view.findViewById<EditText>(R.id.editTextEdadMascota_edit)
        val color_nuevo = spinner_color_edit
        val rgSexo_nuevo = view.findViewById<RadioGroup>(R.id.rgSexoMascota_edit)

        when (mascota.sexo) {
            R.string.mascota_M.toString() -> rgSexo_nuevo.check(R.id.rbMacho_edit)
            R.string.mascota_F.toString() -> rgSexo_nuevo.check(R.id.rbHembra_edit)
        }

        nombre_nuevo.setText(mascota.nombre)
        edad_nueva.setText(mascota.edad.toString())
        color_nuevo.setSelection(colores.indexOf(mascota.color))

        //Me traigo los botones
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar)
        val btnCancelar = view.findViewById<Button>(R.id.btn_cancelar)

        btnGuardar.setOnClickListener {
            val nuevoNombre = nombre_nuevo.text.toString()
            val nuevaEdad = edad_nueva.text.toString()
            val nuevoColor = spinner_color_edit.selectedItem.toString()
            val sexoId = rgSexo_nuevo.checkedRadioButtonId
            val nuevoSexo = view.findViewById<RadioButton>(sexoId)?.text.toString()

            if (validarInfoMascota(view, nuevoNombre, nuevaEdad, nuevoColor, mascota.personaId)) {
                val mascotaActualizada = mascota.copy(
                    nombre = FormValidator.capitalizeWords(nuevoNombre),
                    edad = nuevaEdad.toIntOrNull() ?: mascota.edad,
                    color = nuevoColor,
                    sexo = nuevoSexo,
                    personaId = mascota.personaId
                )

                onGuardar(mascotaActualizada)
                dismiss() // Cierra el diálogo
            }
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    //función que valida los datos de la mascota
    private fun validarInfoMascota(view: View, nombre_mascota: String, edad_mascota: String, color_mascota: String,
                                   personaId:Int): Boolean {
        //Base de datos
        val db = DatabaseProvider.getDatabase(requireContext())

        //inicializo las variables
        var esValido = false
        var camposValidos = 0
        val ed_nombre_mascota = view.findViewById<EditText>(R.id.editTextNombreMascota_edit)
        val ed_edad_mascota = view.findViewById<EditText>(R.id.editTextEdadMascota_edit)
        val rgSexoMascota = view.findViewById<RadioGroup>(R.id.rgSexoMascota_edit)
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
        if (color_mascota == resources.getStringArray(R.array.colores_mascota)[0]) {
            Toast.makeText(requireContext(), getString(R.string.toast_sel_color), Toast.LENGTH_SHORT).show()
        } else {
            camposValidos++
        }

        //Sexo de la mascota
        if (sexoSeleccionadoId == -1) {
            Toast.makeText(requireContext(), getString(R.string.toast_sel_sex_mascota), Toast.LENGTH_SHORT).show()
        } else {
            camposValidos++
        }

        if (camposValidos == 4) {
            esValido = true
        }

        return esValido
    }
}