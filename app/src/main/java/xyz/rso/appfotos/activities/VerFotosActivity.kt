package xyz.rso.appfotos.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.BitmapFactory
import xyz.rso.appfotos.R
import xyz.rso.appfotos.db.BaseDatos
import xyz.rso.appfotos.models.Foto
import java.io.File


class VerFotosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_fotos)

        val bd = BaseDatos(this)
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", 0)

        val lista = bd.listarFotos(idUsuario)

        val recycler = findViewById<RecyclerView>(R.id.recyclerFotos)
        val txtVacio = findViewById<TextView>(R.id.txtVacio)

        if (lista.isEmpty()) {
            txtVacio.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            txtVacio.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = FotosAdapter(lista) { foto ->
                val intent = Intent(this, DetalleFotoActivity::class.java)
                intent.putExtra("id_foto", foto.id)
                startActivity(intent)
            }
        }
    }
}

class FotosAdapter(
    private val fotos: List<Foto>,
    private val alTocar: (Foto) -> Unit
) : RecyclerView.Adapter<FotosAdapter.FotoVH>() {

    class FotoVH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgMiniatura)
        val comentario: TextView = v.findViewById(R.id.txtComentarioItem)
        val fecha: TextView = v.findViewById(R.id.txtFechaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_foto, parent, false)
        return FotoVH(v)
    }

    override fun getItemCount() = fotos.size

    override fun onBindViewHolder(holder: FotoVH, position: Int) {
        val foto = fotos[position]
        val archivo = File(foto.ruta)
        if (archivo.exists()) {
            holder.img.setImageBitmap(BitmapFactory.decodeFile(foto.ruta))
        }
        holder.comentario.text = if (foto.comentario.isEmpty()) "(Sin comentario)" else foto.comentario
        holder.fecha.text = foto.fecha
        holder.itemView.setOnClickListener { alTocar(foto) }
    }
}
