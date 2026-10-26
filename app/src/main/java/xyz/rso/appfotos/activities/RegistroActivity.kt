package xyz.rso.appfotos.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import xyz.rso.appfotos.R
import xyz.rso.appfotos.db.BaseDatos
import xyz.rso.appfotos.models.Usuario

class RegistroActivity : AppCompatActivity() {

    private lateinit var bd: BaseDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        bd = BaseDatos(this)

        val edtNombre = findViewById<EditText>(R.id.edtNombre)
        val edtApellidos = findViewById<EditText>(R.id.edtApellidos)
        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val apellidos = edtApellidos.text.toString().trim()
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString()

            // Validaciones básicas
            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "El correo no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bd.correoExiste(correo)) {
                Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = bd.registrarUsuario(Usuario(0, nombre, apellidos, correo, password))
            if (id > 0) {
                Toast.makeText(this, "Registro exitoso, inicia sesión", Toast.LENGTH_LONG).show()
                finish()   // regresa al login
            } else {
                Toast.makeText(this, "No se pudo registrar", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnRegresar).setOnClickListener {
            finish()   // cierra el registro y regresa al login
        }

    }
}
