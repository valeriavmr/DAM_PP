package com.example.tp_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private lateinit var drawerLayout : DrawerLayout
private lateinit var navView: NavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    //configuro la toolbar y nav
        drawerLayout = findViewById(R.id.drawer_layout_home)
         val toolbar = findViewById<Toolbar>(R.id.toolbar_home)

        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.nombre_negocio)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView = findViewById(R.id.nav_view_home)

        //hago que se vea el usuario en nav_header
        val headerView = navView.getHeaderView(0)

        val user_name_nav = headerView.findViewById<TextView>(R.id.text_nombre_nav)

        user_name_nav.text = intent.getStringExtra("usuario_loggeado")

        //logica para personalizar segun genero
        val personas_guardadas = intent.getStringExtra("personas")

        //Transformo el json en una lista de personas
        val gson = Gson()

        var lista_personas = mutableListOf<Persona>()
        if(personas_guardadas!=null && !personas_guardadas.isEmpty()){

            val type = object : TypeToken<MutableList<Persona>>() {}.type
            lista_personas = Gson().fromJson(personas_guardadas,type)

        }

        //Busco a la persona por user_name
        var user_genero = ""
        lista_personas.forEach { persona ->
            if(persona.user_name.equals(intent.getStringExtra("usuario_loggeado"))){
                user_genero = persona.genero
            }
        }

        //Recupero el imageView del nav
        val nav_imagen = headerView.findViewById<ImageView>(R.id.nav_imagen)

        //personalizo segun genero
        if (user_genero.equals("Femenino")){
            nav_imagen.setImageResource(R.drawable.mascota)
        }else{
            nav_imagen.setImageResource(R.drawable.mascota__1)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_frameLayout, HomeFragment())
                .commit()
        }

        val sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("personas", personas_guardadas)
        editor.putString("usuario_loggeado",intent.getStringExtra("usuario_loggeado"))
        editor.apply()

        navView.setNavigationItemSelectedListener { item ->
            //llamo a los fragments
            when(item.itemId){
                R.id.menu_home_perfil -> {openFragment(PerfilFragment())}
                R.id.menu_home_mascotas ->{openFragment(MascotasFragment())}
                R.id.menu_home_banio ->{openFragment(BaniosFragment())}
                R.id.menu_home_paseos ->{openFragment(PaseosFragment())}
                R.id.menu_home_about ->{openFragment(AboutFragment())}
                R.id.menu_home_miembros ->{openFragment(UsersFragment())}
                R.id.menu_home_adiestramiento ->{openFragment(AdiestramientoFragment())}
                R.id.menu_home_veterinario ->{openFragment(VeterinarioFragment())}
                R.id.menu_home_logout ->{

                    //borro la data sobre el usuario loggeado y me voy a login
                    val sharedPreferences = getSharedPreferences("MyPreferences",MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.remove("usuario_loggeado")
                    editor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("personas",sharedPreferences.getString("personas",""))

                    startActivity(intent)

                }
            }
            true
        }

        //configuro el bottommenu
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottom_menu.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_bottom_home ->{
                    openFragment(HomeFragment())
                }
            }
            true
        }
    }

    private fun openFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction().replace(
            R.id.home_frameLayout, fragment).commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.isDrawerOpen(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}