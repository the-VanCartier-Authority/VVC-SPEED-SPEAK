# Informe de auditoría — VVC Speed Speak

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

### 3. Diferencia entre README y pipeline — pendiente de aclaración

El README presenta Python, Kivy y Buildozer como la tecnología principal, mientras que el job activo compila Kotlin/Compose. La raíz contiene `main.py` y `buildozer.spec`, pero el APK que publica el workflow procede de `app/`. Conviene decidir si el producto oficial será:

1. la app nativa Kotlin/Compose;
2. la app Python/Kivy/Buildozer; o
3. ambas, con workflows separados y nombres de artefacto inequívocos.

### 4. Desalineación de motores TTS — pendiente de aclaración

El README menciona KittenTTS. La aplicación Python documenta Kokoro como respaldo y la implementación nativa contiene catálogos/clases de ambos motores, pero el flujo real debe verificarse funcionalmente en un dispositivo. La compilación correcta no demuestra que los modelos de voz estén incluidos ni que la reproducción sea completamente offline.

### 5. Nombre y activos — observación

El repositorio accesible coincide con `VVC-SPEED-SPEAK`, mientras que la solicitud utiliza `VVC-SPEAD-SPEAK`. El README también comienza con `WVC Speed Speak`. La nomenclatura debería unificarse para evitar confusiones en enlaces, artefactos y documentación. Los recursos Android usan `ic_launcher.png` dentro de los directorios `app/src/main/res/mipmap-*`; si se añade un flujo de activos Python, debe respetarse la ruta indicada por el proyecto (`assets/images/`) y el nombre `ic_launcher.png`.

## Cambios realizados

- Se modificó `.github/workflows/main.yml`.
- Se añadió `permissions: contents: read`.
- Se desactivó la instalación automática del paquete SDK obsoleto `tools`.
- Se añadió instalación explícita de paquetes Android actuales.
- Se añadió `gradle/actions/setup-gradle@v4`.
- Se cambió la compilación para usar `./gradlew`.
- Se generó el Gradle Wrapper de Gradle 9.3.1.
- Se configuró `if-no-files-found: error` para los artefactos release y debug.

## Verificación

- El Gradle Wrapper se generó correctamente con Gradle 9.3.1.
- La corrección del workflow elimina exactamente la causa observada en la ejecución `39112257865`.
- La compilación completa de APK debe confirmarse en el runner de GitHub Actions porque el sandbox de auditoría no tiene Android SDK local instalado.

## Recomendaciones

1. Ejecutar el workflow mediante `workflow_dispatch` después del push y conservar la URL de la nueva ejecución como evidencia de éxito.
2. Si `android-36.1` resulta necesario por `minorApiLevel = 1`, añadir ese paquete concreto o fijar `compileSdk` a una plataforma instalada y soportada por el runner.
3. Separar el workflow Buildozer alternativo del workflow nativo cuando se decida mantener ambos productos.
4. Actualizar el README para documentar comandos locales, versión de Java, versión de Gradle, variante oficial y ubicación de las APK.
5. Añadir pruebas de importación/compilación y una prueba instrumental mínima antes de publicar releases.
