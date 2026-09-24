# Configuración del workflow de VVC SPEED SPEAK

## Archivos que deben copiarse al repositorio

```text
README.md
 gemini_generate.py
 .github/workflows/vvc-speed-speak-build.yml
```

El `README.md` debe ser el README de especificaciones de VVC SPEED SPEAK. El workflow no genera una aplicación web: genera una aplicación Android nativa en **Kotlin + Jetpack Compose**.

## Secreto requerido

En GitHub, abrir:

`Settings → Secrets and variables → Actions → New repository secret`

Crear este secreto:

```text
Name: GEMINI_API_KEY
Value: tu_clave_de_Gemini
```

Nunca escribir la clave dentro de `README.md`, Kotlin, el workflow o cualquier archivo que se suba al repositorio.

## Ejecución

El workflow se ejecuta automáticamente cuando cambia `README.md`, `gemini_generate.py` o el workflow en la rama `main`. También se puede iniciar manualmente desde:

`Actions → VVC SPEED SPEAK - Gemini Android APK → Run workflow`

## Qué hace

1. Lee el `README.md` del repositorio.
2. Envía las especificaciones a Gemini mediante `GEMINI_API_KEY`.
3. Genera el proyecto Android completo en Kotlin.
4. Valida namespace, `applicationId`, Compose, `minSdk` y ausencia de secretos.
5. Comprueba que exista `app/src/main/assets/VVC_SPEED_SPEAK_APPLICATION_REPORT.md`.
6. Ejecuta pruebas unitarias y `lint`.
7. Compila APK debug y release.
8. Sube ambos APK como artefactos de GitHub Actions.
9. Sube el informe y el resultado de generación como artefactos separados.
10. Publica el contenido del informe en el resumen de la ejecución.

## Importante sobre las voces

Gemini genera la estructura y el código, pero no convierte por sí mismo el texto en voz. El código incluye una abstracción TTS para conectar posteriormente un motor natural local o un backend TTS compatible. No se utiliza `SpeechSynthesis` del navegador.

## Verificación esperada

Una ejecución correcta debe terminar con:

- `./gradlew test lint`
- `./gradlew assembleDebug`
- `./gradlew assembleRelease`
- Artefacto `vvc-speed-speak-apk-<commit>` con los APK.
- Artefacto `vvc-speed-speak-report-<commit>` con el informe dentro de `app/src/main/assets`.

## Seguridad

El workflow valida rutas de archivos generadas por Gemini y rechaza rutas fuera del proyecto, archivos binarios y valores que parezcan claves API. Aun así, se debe revisar el diff generado antes de fusionarlo a una rama de producción.
