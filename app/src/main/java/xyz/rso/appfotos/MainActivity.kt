package xyz.rso.appfotos

import android.widget.TextView
import xyz.rso.appfotos.activities.RegistroActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import xyz.rso.appfotos.activities.MenuActivity
import xyz.rso.appfotos.db.BaseDatos


class MainActivity : AppCompatActivity() {

    private lateinit var bd: BaseDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bd = BaseDatos(this)

        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        val btnEntrar = findViewById<Button>(R.id.btnEntrar)



        btnEntrar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Escribe tu correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = bd.validarLogin(correo, password)
            if (usuario == null) {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar el id del usuario para usarlo en las otras pantallas.
                val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                prefs.edit()
                    .putInt("id_usuario", usuario.id)
                    .putString("nombre", usuario.nombre)
                    .putString("apellidos", usuario.apellidos)
                    .apply()

                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val txtIrRegistro = findViewById<TextView>(R.id.txtIrRegistro)
        txtIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

    }
}
