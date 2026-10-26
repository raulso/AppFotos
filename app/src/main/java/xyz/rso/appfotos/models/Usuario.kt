package xyz.rso.appfotos.models

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String
)
