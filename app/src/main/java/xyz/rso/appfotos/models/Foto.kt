package xyz.rso.appfotos.models

data class Foto(
    val id: Int = 0,
    val idUsuario: Int,
    val ruta: String,       // ruta del archivo de la foto en la carpeta de la app
    val comentario: String,
    val latitud: Double,
    val longitud: Double,
    val fecha: String
)
