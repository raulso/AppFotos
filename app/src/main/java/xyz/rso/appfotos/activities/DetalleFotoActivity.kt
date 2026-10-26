package xyz.rso.appfotos.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xyz.rso.appfotos.R
import xyz.rso.appfotos.db.BaseDatos
import java.io.File


class DetalleFotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_foto)

        val bd = BaseDatos(this)
        val idFoto = intent.getIntExtra("id_foto", 0)
        val foto = bd.obtenerFoto(idFoto)

        val imgFoto = findViewById<ImageView>(R.id.imgDetalle)
        val txtComentario = findViewById<TextView>(R.id.txtComentario)
        val txtUbicacion = findViewById<TextView>(R.id.txtUbicacion)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)

        if (foto == null) {
            txtComentario.text = "No se encontró la foto"
            return
        }

        val archivo = File(foto.ruta)
        if (archivo.exists()) {
            imgFoto.setImageBitmap(BitmapFactory.decodeFile(foto.ruta))
        }
        txtComentario.text = if (foto.comentario.isEmpty()) "(Sin comentario)" else foto.comentario
        txtUbicacion.text = "%.6f, %.6f".format(foto.latitud, foto.longitud)
        txtFecha.text = foto.fecha
    }
}
