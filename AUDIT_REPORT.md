# Informe de auditoría — VVC SPEED SPEAK

**Repositorio auditado:** `the-VanCartier-Authority/VVC-SPEED-SPEAK`  
**Rama:** `main`  
**Fecha:** 2026-10-18  
**Referencia:** ejecución fallida de GitHub Actions `39112257865`

## Resumen ejecutivo

El repositorio contiene dos rutas de aplicación Android: una aplicación nativa Kotlin/Jetpack Compose en `app/` y una aplicación alternativa Python/Kivy/Buildozer en la raíz. El README describe principalmente la segunda, pero el workflow de CI compila la primera. La ejecución falló antes de llegar a Gradle porque `android-actions/setup-android@v3` instala por defecto el paquete SDK legado `tools`, que ya no está disponible. También faltaba el Gradle Wrapper (`gradlew`), aunque el workflow lo invocaba indirectamente mediante un Gradle global no garantizado.

Se corrigió el workflow para no solicitar el paquete obsoleto, instalar explícitamente los paquetes SDK actuales, configurar Gradle mediante `gradle/actions/setup-gradle@v4` y compilar con el wrapper versionado de Gradle 9.3.1. Se añadieron comprobaciones de artefactos para que el job falle si no se genera una APK.

## Hallazgos

### 1. Error bloqueante de CI — corregido

El log de Actions muestra:

```text
Warning: Failed to find package 'tools'
Error: The process ... sdkmanager failed with exit code 1
```

La acción `android-actions/setup-android@v3` usa `tools platform-tools` por defecto. `tools` es un paquete antiguo retirado del SDK moderno. El workflow ahora establece `packages: ''` y ejecuta explícitamente:

- `platform-tools`
- `platforms;android-36`
- `build-tools;36.0.0`

### 2. Gradle no reproducible — corregido

El repositorio no tenía `gradlew` ni `gradle/wrapper/gradle-wrapper.jar`, pero el workflow ejecutaba `gradle assembleRelease assembleDebug`. Esto depende de que el runner tenga un Gradle global compatible. Se generó y versionó el wrapper para Gradle 9.3.1, coherente con `gradle/wrapper/gradle-wrapper.properties`, y el workflow usa ahora:

```bash
./gradlew --no-daemon assembleRelease assembleDebug --stacktrace
```

### 3. Ruta de keystore incorrecta — corregido

Tras corregir el SDK y el wrapper, la ejecución `39113034013` llegó a Gradle y falló con:

```text
Keystore file '/home/runner/work/VVC-SPEED-SPEAK/VVC-SPEED-SPEAK/app/my-upload-key.jks' not found
```

El workflow crea `my-upload-key.jks` en la raíz del checkout, pero el script del módulo usaba `rootDir`, que dentro de `app/build.gradle.kts` apunta al directorio `app/`. La configuración ahora usa `rootProject.file(...)` para release y debug, y el workflow genera también `debug.keystore` en la raíz.

### 4. Diferencia entre README y pipeline — pendiente de aclaración

El README presenta Python, Kivy y Buildozer como la tecnología principal, mientras que el job activo compila Kotlin/Compose. La raíz contiene `main.py` y `buildozer.spec`, pero el APK que publica el workflow procede de `app/`. Conviene decidir si el producto oficial será:

1. la app nativa Kotlin/Compose;
2. la app Python/Kivy/Buildozer; o
3. ambas, con workflows separados y nombres de artefacto inequívocos.

### 5. Desalineación de motores TTS — pendiente de aclaración

El README menciona KittenTTS. La aplicación Python documenta Kokoro como respaldo y la implementación nativa contiene catálogos/clases de ambos motores, pero el flujo real debe verificarse funcionalmente en un dispositivo. La compilación correcta no demuestra que los modelos de voz estén incluidos ni que la reproducción sea completamente offline.

### 6. Nombre y activos — corregido

El repositorio correcto es `VVC-SPEED-SPEAK` y la marca oficial es `VVC SPEED SPEAK`. Se corrigieron las referencias visibles que usaban `WVC` o `VVC Speed Speak`. El icono del sistema se integró en `assets/ic_launcher.png`, `app/src/main/res/drawable/ic_app_logo.png` y los mipmap por densidad. El logo horizontal se integró en `assets/logo.png` y se muestra en el encabezado Compose mediante `R.drawable.logo`. La composición de referencia quedó disponible como `assets/branding.png`.

### 7. Nomenclatura interna — corregido

La configuración Android ya no usa los valores generados `com.example`, `MyApplication` ni el `applicationId` temporal de AI Studio. El namespace y el `applicationId` oficiales son `com.vvc.speedspeak`; el tema es `VvcSpeedSpeakTheme`; las fuentes Kotlin y pruebas están bajo `com/vvc/speedspeak`; y el proyecto Gradle se llama `VVC SPEED SPEAK`. También se renombraron el ViewModel y las pruebas para eliminar nombres genéricos. El identificador Buildozer queda como slug técnico `vvcspeedspeak`.

## Cambios realizados

- Se modificó `.github/workflows/main.yml`.
- Se añadió `permissions: contents: read`.
- Se desactivó la instalación automática del paquete SDK obsoleto `tools`.
- Se añadió instalación explícita de paquetes Android actuales.
- Se añadió `gradle/actions/setup-gradle@v4`.
- Se cambió la compilación para usar `./gradlew`.
- Se generó el Gradle Wrapper de Gradle 9.3.1.
- Se configuró `if-no-files-found: error` para los artefactos release y debug.
- Se corrigió la resolución de las keystores desde el proyecto raíz y se añadió la generación de `debug.keystore` en CI.
- Se unificó la nomenclatura visible a `VVC SPEED SPEAK`.
- Se integraron el icono del sistema, el logo horizontal de interfaz y la composición de branding proporcionados.

## Verificación

- El Gradle Wrapper se generó correctamente con Gradle 9.3.1.
- La corrección del workflow elimina exactamente la causa observada en la ejecución `39112257865`.
- La ejecución `39113034013` confirmó que el SDK y Gradle ya funcionan; su único fallo restante fue la ruta de firma, que quedó corregida después.
- La compilación completa de APK tras la integración visual debe confirmarse en el runner de GitHub Actions porque el sandbox de auditoría no tiene Android SDK local instalado.

## Recomendaciones

1. Ejecutar el workflow mediante `workflow_dispatch` después del push y conservar la URL de la nueva ejecución como evidencia de éxito.
2. Si `android-36.1` resulta necesario por `minorApiLevel = 1`, añadir ese paquete concreto o fijar `compileSdk` a una plataforma instalada y soportada por el runner.
3. Separar el workflow Buildozer alternativo del workflow nativo cuando se decida mantener ambos productos.
4. Actualizar el README para documentar comandos locales, versión de Java, versión de Gradle, variante oficial y ubicación de las APK.
5. Añadir pruebas de importación/compilación y una prueba instrumental mínima antes de publicar releases.
