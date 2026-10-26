# App de Fotos — Android (Kotlin + SQLite)

App local para registrar usuarios, tomar fotos con ubicación GPS y comentario,
y consultarlas después. Todo se guarda en el teléfono (sin internet).

## Cómo abrirla
1. Abre Android Studio → "Open" → selecciona esta carpeta `AppFotos`.
2. Espera a que sincronice Gradle (bajará dependencias).
3. Conecta un celular o usa un emulador y pulsa Run.

## Qué hace cada pantalla
1. **Login** (MainActivity): entra comparando correo y contraseña contra SQLite.
2. **Registro**: nombre, apellidos, correo y contraseña → guarda en SQLite.
3. **Menú**: botones "Tomar foto" y "Ver fotos".
4. **Tomar foto**: abre la cámara, muestra la foto, la guarda en la carpeta de la
   app, captura el GPS y, con un comentario, guarda todo en SQLite.
5. **Ver fotos**: lista las fotos; al tocar una, muestra la foto con su comentario
   y la ubicación GPS registrada.

## Dónde se guarda cada cosa
- **Base de datos:** SQLite (`appfotos.db`), tablas `usuarios` y `fotos`.
- **Archivos de foto:** carpeta privada de la app (`Android/data/xyz.rso.appfotos/files/fotos/`).
- **La BD guarda** la ruta del archivo + comentario + latitud + longitud + fecha.

## Permisos
La app pide en tiempo de ejecución: Cámara y Ubicación. Acéptalos al tomar la primera foto.

## Estructura del código (paquete xyz.rso.appfotos)
- `MainActivity.kt` — login
- `activities/RegistroActivity.kt`
- `activities/MenuActivity.kt`
- `activities/TomarFotoActivity.kt`
- `activities/VerFotosActivity.kt` (incluye el adaptador de la lista)
- `activities/DetalleFotoActivity.kt`
- `db/BaseDatos.kt` — SQLite (usuarios y fotos)
- `models/Usuario.kt`, `models/Foto.kt`

## Notas
- compileSdk 34, minSdk 24 (compatible con la mayoría de dispositivos).
- Si Android Studio te pide actualizar Gradle o AGP, acepta; el proyecto es estándar.
