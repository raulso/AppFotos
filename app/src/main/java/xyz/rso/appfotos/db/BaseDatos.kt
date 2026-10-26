package xyz.rso.appfotos.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import xyz.rso.appfotos.models.Foto
import xyz.rso.appfotos.models.Usuario

class BaseDatos(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION) {

    companion object {
        private const val NOMBRE_BD = "appfotos.db"
        private const val VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Tabla de fotos (guarda la ruta del archivo + GPS + comentario)
        db.execSQL(
            """
            CREATE TABLE fotos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                ruta TEXT NOT NULL,
                comentario TEXT,
                latitud REAL,
                longitud REAL,
                fecha TEXT,
                FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO usuarios (nombre, apellidos, correo, password)
            VALUES ('raul', 'Prueba1', 'raul@correo.com', '1234')
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS fotos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }


    /** Registra un usuario. Devuelve el id nuevo, o -1 si el correo ya existe. **/
    fun registrarUsuario(u: Usuario): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", u.nombre)
            put("apellidos", u.apellidos)
            put("correo", u.correo)
            put("password", u.password)
        }
        return try {
            db.insertOrThrow("usuarios", null, valores)
        } catch (e: Exception) {
            -1   // correo duplicado u otro error
        }
    }

    /** Valida login: devuelve el Usuario si correo y password coinciden, o null. **/
    fun validarLogin(correo: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, apellidos, correo, password FROM usuarios WHERE correo = ? AND password = ?",
            arrayOf(correo, password)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                apellidos = cursor.getString(2),
                correo = cursor.getString(3),
                password = cursor.getString(4)
            )
        }
        cursor.close()
        return usuario
    }

    /** ¿Ya existe un correo registrado? **/
    fun correoExiste(correo: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE correo = ?", arrayOf(correo))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }


    /** Guarda una foto con su ubicación y comentario. **/
    fun guardarFoto(f: Foto): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id_usuario", f.idUsuario)
            put("ruta", f.ruta)
            put("comentario", f.comentario)
            put("latitud", f.latitud)
            put("longitud", f.longitud)
            put("fecha", f.fecha)
        }
        return db.insert("fotos", null, valores)
    }

    /** Lista todas las fotos de un usuario, de la más nueva a la más vieja. **/
    fun listarFotos(idUsuario: Int): List<Foto> {
        val lista = mutableListOf<Foto>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, id_usuario, ruta, comentario, latitud, longitud, fecha FROM fotos WHERE id_usuario = ? ORDER BY id DESC",
            arrayOf(idUsuario.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Foto(
                    id = cursor.getInt(0),
                    idUsuario = cursor.getInt(1),
                    ruta = cursor.getString(2),
                    comentario = cursor.getString(3) ?: "",
                    latitud = cursor.getDouble(4),
                    longitud = cursor.getDouble(5),
                    fecha = cursor.getString(6) ?: ""
                )
            )
        }
        cursor.close()
        return lista
    }

    /** Obtiene una foto por su id. **/
    fun obtenerFoto(id: Int): Foto? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, id_usuario, ruta, comentario, latitud, longitud, fecha FROM fotos WHERE id = ?",
            arrayOf(id.toString())
        )
        var foto: Foto? = null
        if (cursor.moveToFirst()) {
            foto = Foto(
                id = cursor.getInt(0),
                idUsuario = cursor.getInt(1),
                ruta = cursor.getString(2),
                comentario = cursor.getString(3) ?: "",
                latitud = cursor.getDouble(4),
                longitud = cursor.getDouble(5),
                fecha = cursor.getString(6) ?: ""
            )
        }
        cursor.close()
        return foto
    }
}
