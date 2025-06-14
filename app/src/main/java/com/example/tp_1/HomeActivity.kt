package com.example.tp_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.example.tp_1.datos.DatabaseProvider
import com.example.tp_1.fragments.AboutFragment
import com.example.tp_1.fragments.AdiestramientoFragment
import com.example.tp_1.fragments.BaniosFragment
import com.example.tp_1.fragments.HomeFragment
import com.example.tp_1.fragments.MascotasFragment
import com.example.tp_1.fragments.PaseosFragment
import com.example.tp_1.fragments.PerfilFragment
import com.example.tp_1.fragments.UsersFragment
import com.example.tp_1.fragments.VeterinarioFragment

private lateinit var drawerLayout : DrawerLayout
private lateinit var navView: NavigationView

class HomeActivity : AppCompatActivity() {
    val usuario_loggeado = "usuario_loggeado"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //inicializo la base de datos
        val db = DatabaseProvider.getDatabase(this)

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

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)

        val user_name = prefs.getString(usuario_loggeado,"")

        user_name_nav.text = user_name

        //logica para personalizar segun genero
        val persona_loggeada = db.personaDao().getPersonaPorUserName(user_name.toString())

        //Recupero el imageView del nav
        val nav_imagen = headerView.findViewById<ImageView>(R.id.nav_imagen)

        //personalizo segun genero
        if (persona_loggeada?.genero == resources.getString(R.string.genero_F)){
            nav_imagen.setImageResource(R.drawable.mascota)
        }else{
            nav_imagen.setImageResource(R.drawable.mascota__1)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_frameLayout, HomeFragment())
                .commit()
        }

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

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(usuario_loggeado,"")

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

    //Funcion que actualiza los datos del NavigationView cuando se cambia el username o genero
    fun actualizarHeaderNavigationView(userName: String) {
        val db = DatabaseProvider.getDatabase(this@HomeActivity)
        val headerView = findViewById<NavigationView>(R.id.nav_view_home).getHeaderView(0)

        val textViewNombre = headerView.findViewById<TextView>(R.id.text_nombre_nav)
        val imgViewGenero = headerView.findViewById<ImageView>(R.id.nav_imagen)

        val persona = db.personaDao().getPersonaPorUserName(userName)

        textViewNombre.text = persona?.user_name ?: "Sin nombre"

        if (persona?.genero == resources.getString(R.string.genero_F)){
            imgViewGenero.setImageResource(R.drawable.mascota)
        }else{
            imgViewGenero.setImageResource(R.drawable.mascota__1)
        }
    }
}