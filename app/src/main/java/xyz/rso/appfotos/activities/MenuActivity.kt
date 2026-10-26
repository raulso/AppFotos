package xyz.rso.appfotos.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xyz.rso.appfotos.MainActivity
import xyz.rso.appfotos.R

/**
 * Pantalla principal: opciones de Tomar foto y Ver fotos.
 */
class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "usuario")
        val apellidos = prefs.getString("apellidos", "")

        findViewById<TextView>(R.id.txtBienvenida).text = "Hola, $nombre $apellidos"

        findViewById<Button>(R.id.btnTomarFoto).setOnClickListener {
            startActivity(Intent(this, TomarFotoActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerFotos).setOnClickListener {
            startActivity(Intent(this, VerFotosActivity::class.java))
        }

        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
