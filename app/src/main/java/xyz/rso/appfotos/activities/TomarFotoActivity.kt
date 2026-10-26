package xyz.rso.appfotos.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
import xyz.rso.appfotos.R
import xyz.rso.appfotos.db.BaseDatos
import xyz.rso.appfotos.models.Foto
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TomarFotoActivity : AppCompatActivity() {

    private lateinit var bd: BaseDatos
    private lateinit var imgFoto: ImageView
    private lateinit var edtComentario: EditText
    private lateinit var txtUbicacion: TextView
    private lateinit var btnGuardar: Button

    private var rutaFoto: String? = null     // ruta del archivo tomado
    private var latitud = 0.0
    private var longitud = 0.0

    // Lanzador de la cámara
    private val lanzadorCamara =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
            if (exito && rutaFoto != null) {
                mostrarFoto(rutaFoto!!)
                obtenerUbicacion()
                btnGuardar.isEnabled = true
            } else {
                Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show()
            }
        }

    // Lanzador de permisos
    private val lanzadorPermisos =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
            val camaraOk = permisos[Manifest.permission.CAMERA] == true
            if (camaraOk) abrirCamara()
            else Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tomar_foto)

        bd = BaseDatos(this)
        imgFoto = findViewById(R.id.imgFoto)
        edtComentario = findViewById(R.id.edtComentario)
        txtUbicacion = findViewById(R.id.txtUbicacion)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnGuardar.isEnabled = false

        findViewById<Button>(R.id.btnAbrirCamara).setOnClickListener {
            pedirPermisosYAbrir()
        }

        btnGuardar.setOnClickListener { guardar() }
    }

    private fun pedirPermisosYAbrir() {
        val permisos = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val faltan = permisos.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltan) lanzadorPermisos.launch(permisos)
        else abrirCamara()
    }

    private fun abrirCamara() {
        val nombre = "FOTO_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        val carpeta = File(getExternalFilesDir(null), "fotos")
        if (!carpeta.exists()) carpeta.mkdirs()
        val archivo = File(carpeta, nombre)
        rutaFoto = archivo.absolutePath

        val uri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", archivo)
        lanzadorCamara.launch(uri)
    }

    private fun mostrarFoto(ruta: String) {
        val bitmap = BitmapFactory.decodeFile(ruta)
        imgFoto.setImageBitmap(bitmap)
    }

    private fun obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            txtUbicacion.text = "Ubicación: sin permiso"
            return
        }
        val cliente = LocationServices.getFusedLocationProviderClient(this)
        cliente.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                latitud = loc.latitude
                longitud = loc.longitude
                txtUbicacion.text = "Ubicación: %.6f, %.6f".format(latitud, longitud)
            } else {
                txtUbicacion.text = "Ubicación: no disponible"
            }
        }.addOnFailureListener {
            txtUbicacion.text = "Ubicación: error al obtener"
        }
    }

    private fun guardar() {
        if (rutaFoto == null) {
            Toast.makeText(this, "Primero toma una foto", Toast.LENGTH_SHORT).show()
            return
        }
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", 0)
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val foto = Foto(
            idUsuario = idUsuario,
            ruta = rutaFoto!!,
            comentario = edtComentario.text.toString().trim(),
            latitud = latitud,
            longitud = longitud,
            fecha = fecha
        )
        val id = bd.guardarFoto(foto)
        if (id > 0) {
            Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show()
        }
    }
}
