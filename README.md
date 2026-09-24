# VVC SPEED SPEAK

## Documento de especificaciones para Emily

**VVC SPEED SPEAK** será una aplicación Android distribuible como APK para convertir texto pegado por la usuaria en audio mediante voces de inteligencia artificial naturales. La primera versión debe ser deliberadamente sencilla en su interfaz, pero estar preparada para crecer mediante un backend o motor de síntesis más avanzado.

> **Objetivo de la versión inicial:** la usuaria pega un texto, selecciona una voz y escucha el contenido convertido en audio. No se deben incluir todavía editores complejos, cuentas de usuario, redes sociales ni funciones que no sean necesarias para este flujo.

---

## 1. Decisión de plataforma

La aplicación se desarrollará como una **aplicación nativa Android en formato APK**, escrita en **Kotlin**. Kotlin no es una alternativa: es el lenguaje oficial y obligatorio de este proyecto.

La razón principal es que el producto debe poder publicarse e instalarse como aplicación móvil, sin depender de que la usuaria abra una página web. La aplicación debe priorizar el funcionamiento en teléfonos Android y mantener una experiencia visual consistente.

### Tecnologías y parámetros obligatorios

- **Android Studio**.
- **Kotlin** como único lenguaje de aplicación. No crear la aplicación principal en Java, Flutter, React Native ni JavaScript.
- **Jetpack Compose** para la interfaz. No mezclar XML con Compose en la primera versión salvo que una dependencia Android lo exija.
- **Material 3**, utilizando componentes personalizados para respetar la identidad visual.
- **Gradle Kotlin DSL** (`build.gradle.kts` y `settings.gradle.kts`), no Groovy.
- **Namespace:** `com.vvc.speedspeak`.
- **Application ID:** `com.vvc.speedspeak`.
- **Min SDK:** Android 8.0 / API 26.
- **Compile SDK y Target SDK:** la versión estable más reciente soportada por la versión de Android Studio instalada. Ambos valores deben ser iguales y deben quedar documentados en el repositorio.
- **JVM target:** la versión LTS compatible con la versión estable de Kotlin y Android Gradle Plugin usada por el proyecto.
- **Arquitectura:** MVVM, con `ViewModel`, `StateFlow` y coroutines de Kotlin.
- **Estado de UI:** flujo unidireccional; la pantalla no debe contener lógica de generación de audio.
- **Almacenamiento de recursos:** recursos de marca en `app/src/main/assets`; recursos Android compilables también pueden copiarse a `res/drawable`, `res/mipmap` o `res/font` cuando sea necesario.

### Configuración mínima esperada

El módulo `app` debe configurarse con estos valores conceptuales. Las versiones exactas de Kotlin, Android Gradle Plugin, Compose BOM y Gradle Wrapper deben ser compatibles entre sí y fijarse en el repositorio mediante `libs.versions.toml` o una configuración equivalente.

```kotlin
android {
    namespace = "com.vvc.speedspeak"

    defaultConfig {
        applicationId = "com.vvc.speedspeak"
        minSdk = 26
        // compileSdk y targetSdk: última versión estable compatible instalada
    }
}

kotlin {
    // Usar el target JVM LTS compatible con Kotlin y AGP del proyecto
}
```

El proyecto debe compilar con `./gradlew assembleDebug` y generar el APK en `app/build/outputs/apk/debug/`. Antes de publicar, también debe validarse `./gradlew assembleRelease`.

El repositorio podrá administrarse en GitHub. GitHub será utilizado para almacenar el código, controlar versiones y documentar el proyecto; no será considerado el motor de generación de voces.

---

## 2. Nombre e identidad de la aplicación

### Nombre oficial

**VVC SPEED SPEAK**

El nombre debe conservarse exactamente en mayúsculas en:

- Pantalla de inicio.
- Encabezado principal.
- Nombre de la aplicación en Android.
- Documentación.
- Metadatos del proyecto.
- Elementos de marca, cuando el diseño lo permita.

### Concepto de marca

VVC SPEED SPEAK debe comunicar:

- Tecnología avanzada.
- Velocidad.
- Lectura inteligente.
- Control del audio.
- Estética cyberpunk retrofuturista.
- Una herramienta premium, sobria y poderosa.

La interfaz debe sentirse como un **panel tecnológico de alto nivel**, no como una aplicación genérica de notas o un reproductor convencional.

---

## 3. Dirección estética

La interfaz debe ser minimalista en estructura, pero impecable en acabado visual. La sencillez se refiere a la cantidad de funciones visibles, no a una apariencia básica o improvisada.

### Paleta principal

| Uso | Color | Referencia sugerida |
|---|---|---|
| Fondo principal | Negro obsidiana profundo | `#050509` |
| Superficies | Negro grafito | `#0D0D16` |
| Violeta principal | Violeta neón intenso | `#8A2BE2` o `#A020F0` |
| Cian de interacción | Cian neón | `#00E5FF` |
| Rojo de alerta | Rojo neón | `#FF1744` |
| Texto principal | Blanco frío | `#F4F7FF` |
| Texto secundario | Gris azulado | `#8C91A8` |
| Bordes sutiles | Violeta/cian translúcido | Entre 20 % y 45 % de opacidad |

Los valores anteriores son referencias iniciales. Emily puede ajustar los tonos para mejorar contraste y accesibilidad, pero no debe abandonar la combinación conceptual de **obsidiana, violeta neón, cian neón y rojo neón**.

### Fondo

- Fondo general en negro obsidiana profundo.
- Se puede utilizar un degradado muy sutil entre negro, violeta oscuro y azul oscuro.
- No usar fondos blancos ni grises claros.
- No saturar la pantalla con líneas, partículas o efectos decorativos.
- Los efectos cyberpunk deben apoyar la legibilidad, no competir con el texto.

### Elementos visuales

- Bordes finos con brillo neón controlado.
- Esquinas ligeramente redondeadas, sin aspecto infantil.
- Sombras profundas y halos de luz discretos.
- Estados activos en cian o violeta.
- Acciones destructivas o de interrupción en rojo neón.
- Animaciones rápidas, suaves y opcionales.
- Evitar animaciones excesivas que consuman batería o distraigan durante la lectura.

### Tipografía

Los títulos deben utilizar una tipografía futurista, tecnológica y legible. El texto largo debe utilizar una tipografía altamente legible en pantalla móvil.

Recomendación:

- Títulos: una familia futurista o geométrica, por ejemplo Orbitron, Rajdhani, Audiowide o equivalente con licencia compatible.
- Texto de lectura y controles: Inter, Space Grotesk, Roboto o equivalente.
- No utilizar tipografías decorativas para bloques largos de texto.
- Verificar la licencia de cualquier fuente incluida en el APK.

---

## 4. Logotipos, iconos y carpeta `assets`

Todos los recursos visuales proporcionados por la marca deben estar dentro de una carpeta llamada `assets`, ubicada dentro del módulo principal de la aplicación.

### Estructura requerida

```text
VVC-SPEED-SPEAK/
├── app/
│   └── src/
│       └── main/
│           ├── assets/
│           │   ├── branding/
│           │   │   ├── vvc_brand_logo.png
│           │   │   ├── vvc_brand_logo.svg
│           │   │   └── vvc_brand_logo_monochrome.png
│           │   ├── app_logo/
│           │   │   ├── vvc_speed_speak_logo.png
│           │   │   ├── vvc_speed_speak_logo.svg
│           │   │   └── vvc_speed_speak_logo_light.png
│           │   ├── app_icon/
│           │   │   ├── icon_foreground.png
│           │   │   ├── icon_background.png
│           │   │   └── icon_preview.png
│           │   ├── fonts/
│           │   ├── sounds/
│           │   └── models/
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
└── README.md
```

### Diferencia entre los recursos

- **Logo de marca:** identifica la marca principal VVC.
- **Logo de aplicación:** identifica específicamente a VVC SPEED SPEAK dentro de la interfaz.
- **Icono de aplicación:** imagen adaptada al sistema Android, lanzador, pantalla de inicio y listado de aplicaciones.

La aplicación no debe depender de rutas absolutas. Todos los recursos deben cargarse desde el proyecto y empaquetarse correctamente en el APK.

Si Android exige que ciertos recursos visuales se ubiquen además en `res/drawable`, `res/mipmap` o `res/font`, se podrán mantener copias optimizadas allí, pero la fuente organizada y editable de los recursos debe conservarse dentro de `app/src/main/assets`.

### Requisitos del icono

- Debe funcionar en iconos adaptativos de Android.
- Debe conservar legibilidad en tamaños pequeños.
- Debe utilizar la identidad obsidiana con detalles violeta, cian o rojo neón.
- No incluir texto pequeño ilegible dentro del icono.
- Preparar versiones para densidades y formatos requeridos por Android Studio.

---

## 5. Alcance funcional de la primera versión

La primera versión debe contener únicamente las funciones necesarias para el flujo principal.

### Funciones obligatorias

1. Abrir la aplicación.
2. Mostrar el logo de VVC SPEED SPEAK.
3. Pegar o escribir texto en un campo amplio.
4. Admitir textos largos sin limitar artificialmente el campo de entrada.
5. Mostrar contador de caracteres o palabras, si no afecta la simplicidad visual.
6. Elegir el tipo de voz disponible.
7. Elegir la velocidad de lectura.
8. Generar o iniciar la lectura del texto.
9. Reproducir el audio.
10. Pausar la reproducción.
11. Reanudar la reproducción.
12. Detener la reproducción.
13. Limpiar el contenido del campo de texto.
14. Mostrar estados claros: preparado, procesando, reproduciendo, pausado, detenido y error.

### Funciones fuera del alcance inicial

No implementar todavía:

- Registro o inicio de sesión.
- Perfiles de usuario.
- Sincronización en la nube.
- Biblioteca de proyectos.
- Publicación en redes sociales.
- Editor avanzado de libros.
- Corrección gramatical.
- Traducción automática.
- Generación de contenido mediante IA.
- Clonación de voz.
- Marketplace de voces.
- Efectos musicales o de sonido.
- Sistema de pagos.
- Publicidad invasiva.

Estas funciones pueden considerarse en futuras versiones, pero no deben complicar el primer APK.

---

## 6. Flujo de usuario

### Flujo principal

```text
Abrir aplicación
      ↓
Pegar o escribir texto
      ↓
Seleccionar voz
      ↓
Seleccionar velocidad
      ↓
Presionar “GENERAR AUDIO” o “REPRODUCIR”
      ↓
Procesar el texto
      ↓
Reproducir la voz natural
      ↓
Pausar, reanudar o detener
```

### Comportamiento esperado

- Si el campo está vacío, el botón principal debe permanecer desactivado o mostrar una indicación clara.
- Si el texto es muy largo, se debe dividir internamente en fragmentos sin que la usuaria tenga que hacerlo manualmente.
- La lectura debe mantener continuidad entre fragmentos.
- El procesamiento debe mostrar una señal visual de actividad.
- La aplicación no debe congelarse mientras se genera el audio.
- Si ocurre un error, debe mostrarse un mensaje comprensible y una acción para reintentar.
- El texto pegado no debe perderse por cambiar de orientación o regresar temporalmente a otra aplicación.

---

## 7. Diseño de la pantalla principal

La pantalla principal debe ser la experiencia central de la aplicación.

### Composición sugerida

1. **Encabezado**
   - Logo de aplicación.
   - Nombre `VVC SPEED SPEAK`.
   - Indicador de estado discreto, si resulta útil.

2. **Área de texto**
   - Panel grande con borde tenue cian o violeta.
   - Texto claro sobre fondo oscuro.
   - Placeholder sugerido: `Pega aquí el texto que deseas escuchar...`.
   - Botón pequeño para limpiar.
   - El campo debe ocupar la mayor parte de la pantalla.

3. **Controles de voz**
   - Selector de voz.
   - Selector de idioma, únicamente si el motor lo requiere.
   - Control de velocidad.
   - Los controles deben permanecer sencillos y fáciles de entender.

4. **Acción principal**
   - Botón destacado: `GENERAR AUDIO` o `REPRODUCIR`.
   - Color primario: cian neón o violeta neón.
   - Brillo controlado al tocarlo.

5. **Reproductor**
   - Aparece o se activa después de iniciar el procesamiento.
   - Mostrar reproducción, pausa, reanudar y detener.
   - La acción de detener puede utilizar rojo neón.
   - Mostrar progreso cuando el motor lo permita.

### Diseño responsivo

La pantalla debe funcionar correctamente en:

- Teléfonos Android pequeños.
- Teléfonos Android grandes.
- Orientación vertical como prioridad.
- Orientación horizontal como soporte deseable.
- Modo con fuente del sistema aumentada.

---

## 8. Motor de voces naturales

Las voces sintéticas predeterminadas del navegador **no forman parte de los requisitos**. No se debe implementar `SpeechSynthesis` del navegador como solución principal.

La aplicación debe utilizar un motor de texto a voz con voces de calidad natural y licencia compatible con la distribución del APK.

### Alternativas técnicas a evaluar

#### Opción A: motor TTS local dentro del dispositivo

Usar un motor de código abierto, por ejemplo una implementación compatible con **Piper TTS**, **Kokoro** u otro motor equivalente que pueda ejecutarse en Android.

Ventajas:

- No requiere enviar el texto a un servidor.
- Puede funcionar sin conexión.
- No depende de una suscripción mensual.
- Ofrece mayor control sobre privacidad.

Consideraciones:

- Los modelos pueden aumentar el tamaño del APK.
- Es necesario revisar el rendimiento en teléfonos de gama baja.
- Cada voz y modelo debe tener una licencia compatible.
- Puede ser preferible descargar modelos por separado en una futura versión.

#### Opción B: backend propio con motor de código abierto

La aplicación Android enviaría el texto a un backend que generaría el audio y lo devolvería al teléfono.

Ventajas:

- Se pueden utilizar modelos más pesados.
- Es más sencillo actualizar voces desde el servidor.
- El APK puede ser más pequeño.

Consideraciones:

- Requiere servidor y mantenimiento.
- El costo de infraestructura no es necesariamente cero.
- Se deben proteger los datos enviados.
- La lectura depende de una conexión a Internet.
- Se deben aplicar límites y controles para evitar abusos.

### Recomendación para el prototipo

Emily debe evaluar primero una arquitectura que permita cambiar entre motor local y backend sin reescribir la interfaz. La interfaz debe comunicarse con una abstracción similar a:

```kotlin
interface TextToSpeechEngine {
    suspend fun synthesize(
        text: String,
        voiceId: String,
        speed: Float
    ): AudioResult

    fun stop()
    fun pause()
    fun resume()
}
```

La selección final del motor debe hacerse después de comprobar:

- Naturalidad de las voces.
- Idiomas disponibles.
- Licencia comercial o de distribución.
- Tamaño del modelo.
- Tiempo de generación.
- Consumo de memoria.
- Compatibilidad con Android.

No se deben prometer voces idénticas a ElevenLabs. El objetivo es obtener voces naturales, agradables y claramente superiores a las voces sintéticas básicas del navegador, utilizando una solución gratuita o de código abierto cuando sea viable.

---

## 9. Tratamiento de textos largos

El sistema debe aceptar textos extensos. Para evitar errores de memoria o límites del motor, el texto debe procesarse en fragmentos.

### Reglas de segmentación

- Dividir preferentemente por párrafos.
- Si un párrafo excede el límite del motor, dividir por oraciones.
- Evitar cortar una palabra.
- Mantener el orden original.
- Conservar pausas naturales entre párrafos.
- Permitir cancelar el proceso en cualquier momento.
- Reproducir fragmentos de manera secuencial.
- Liberar memoria de fragmentos ya reproducidos cuando sea posible.

### Pseudoflujo

```text
Texto completo
   ↓
Normalizar espacios y saltos de línea
   ↓
Dividir por párrafos y oraciones
   ↓
Crear cola de fragmentos
   ↓
Generar audio del fragmento actual
   ↓
Reproducir fragmento
   ↓
Pasar al siguiente fragmento
   ↓
Finalizar o permitir detener
```

La usuaria no debe ver la complejidad de este proceso. Para ella, la acción debe sentirse como una sola lectura continua.

---

## 10. Privacidad y seguridad

- No enviar texto a Internet sin informarlo claramente.
- Si se usa backend, utilizar HTTPS.
- No registrar textos completos en logs de producción.
- No guardar el contenido del texto permanentemente en la primera versión, salvo que la usuaria lo solicite explícitamente.
- No incluir claves secretas dentro del APK.
- Las credenciales de cualquier servicio deben permanecer en el backend seguro.
- Incluir una política de privacidad antes de una publicación pública si la aplicación procesa contenido del usuario en servidores.

---

## 11. Estados de la interfaz

La interfaz debe representar claramente estos estados:

| Estado | Comportamiento visual |
|---|---|
| Inicial | Campo vacío y botón principal inactivo o listo para recibir texto |
| Texto listo | Botón principal activo en cian/violeta |
| Procesando | Indicador de progreso y controles temporalmente limitados |
| Reproduciendo | Estado activo, progreso y botón de pausa |
| Pausado | Botón de reanudar claramente visible |
| Detenido | Reproductor listo para iniciar de nuevo |
| Completado | Mensaje discreto de finalización |
| Error | Mensaje breve, visible y botón de reintento |

Los mensajes deben estar escritos en español en la primera versión, salvo que Emily prepare desde el inicio una estructura de internacionalización.

---

## 12. Estructura de código sugerida

```text
app/src/main/java/com/vvc/speedspeak/
├── MainActivity.kt
├── VvcSpeedSpeakApp.kt
├── data/
│   ├── model/
│   │   ├── VoiceOption.kt
│   │   ├── AudioResult.kt
│   │   └── PlaybackState.kt
│   ├── repository/
│   │   └── SpeechRepository.kt
│   └── tts/
│       ├── TextToSpeechEngine.kt
│       ├── LocalTtsEngine.kt
│       └── RemoteTtsEngine.kt
├── domain/
│   ├── SplitLongTextUseCase.kt
│   └── GenerateAudioUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── components/
│   │   ├── NeonButton.kt
│   │   ├── VoiceSelector.kt
│   │   ├── TextInputPanel.kt
│   │   └── AudioPlayerControls.kt
│   └── home/
│       ├── HomeScreen.kt
│       └── HomeViewModel.kt
└── util/
    ├── TextChunker.kt
    └── FileUtils.kt
```

Esta estructura es orientativa. Puede modificarse si Emily utiliza otra arquitectura, siempre que se mantengan separadas la interfaz, la lógica de negocio y el motor TTS.

---

## 13. Requisitos de calidad

Antes de considerar lista la primera versión, la aplicación debe cumplir lo siguiente:

- Compilar correctamente como APK de lanzamiento.
- No cerrarse al pegar textos largos.
- Mantener una interfaz fluida durante la generación del audio.
- Reproducir, pausar, reanudar y detener correctamente.
- Mostrar un mensaje claro cuando no haya conexión, si el motor es remoto.
- Mostrar un mensaje claro cuando falte un modelo de voz, si el motor es local.
- Respetar la estética definida en dispositivos de distintos tamaños.
- Tener contraste suficiente para leer el texto con comodidad.
- No depender de las voces sintéticas del navegador.
- No incluir claves API en el código público de GitHub.
- Mantener la carpeta `assets` organizada y documentada.
- Verificar las licencias de modelos, voces, tipografías, iconos y bibliotecas.

---

## 14. Criterios de aceptación de la primera entrega

La primera entrega será aceptable cuando Emily pueda demostrar este flujo:

1. Instalar el APK en un teléfono Android.
2. Abrir VVC SPEED SPEAK.
3. Ver la identidad visual cyberpunk retrofuturista.
4. Pegar un texto corto y uno largo.
5. Elegir una voz disponible.
6. Elegir una velocidad.
7. Iniciar la generación o reproducción.
8. Escuchar una voz natural mediante el motor seleccionado.
9. Pausar y reanudar.
10. Detener la lectura.
11. Repetir el proceso con otro texto.
12. Confirmar que la aplicación no pierde el contenido durante una rotación o cambio temporal de pantalla, cuando el sistema lo permita.

---

## 15. Roadmap posterior

Después de validar la primera versión, se pueden estudiar estas extensiones:

- Más idiomas y voces.
- Descarga del audio en MP3 o WAV.
- Historial local de textos.
- Favoritos.
- Importación de archivos `.txt`, `.pdf` y `.epub`.
- Lectura desde el portapapeles.
- Temporizador de apagado.
- Marcadores de lectura.
- Ajustes avanzados de pausas y pronunciación.
- PWA complementaria.
- Backend escalable.
- Sistema de actualización de modelos de voz.
- Publicación en Google Play.

Ninguna de estas funciones debe agregarse antes de probar y aprobar el flujo esencial de pegar texto y escucharlo.

---

## 16. Nota final para Emily

Construir primero una experiencia visual pulida y un flujo funcional mínimo. La pantalla debe ser sencilla, elegante y rápida de entender. La complejidad debe permanecer dentro de la arquitectura: procesamiento de textos largos, cola de fragmentos, generación de audio, manejo de errores y posibilidad de cambiar de motor TTS.

La prioridad de VVC SPEED SPEAK es:

1. **Voces naturales.**
2. **Interfaz impecable.**
3. **Funcionamiento estable con textos largos.**
4. **APK instalable y publicable.**
5. **Arquitectura preparada para crecer.**

La aplicación no debe parecer un prototipo genérico. Debe sentirse como una herramienta tecnológica premium con identidad propia, aunque la primera versión tenga solamente una función principal.

---

## Licencias y recursos de terceros

Antes de publicar el APK, documentar en este repositorio:

- Licencia del motor TTS.
- Licencia de cada modelo de voz.
- Licencia de las tipografías.
- Licencia de iconos y bibliotecas externas.
- Política de privacidad, si se utiliza un backend.
- Avisos de terceros requeridos por Android o por las dependencias utilizadas.

**No publicar modelos, voces o recursos de terceros sin verificar sus condiciones de redistribución.**

---

**Proyecto:** VVC SPEED SPEAK  
**Plataforma inicial:** Android APK  
**Versión de alcance:** MVP — pegar texto y escucharlo con voz natural  
**Dirección visual:** Cyberpunk retrofuturista, negro obsidiana, violeta neón, cian neón y rojo neón
