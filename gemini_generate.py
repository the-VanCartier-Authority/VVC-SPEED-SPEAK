#!/usr/bin/env python3
"""Generate an Android/Kotlin project in 2 light phases using Gemini REST API."""
from __future__ import annotations

import json
import os
import pathlib
import sys
import time
import urllib.error
import urllib.request

ROOT = pathlib.Path.cwd().resolve()
README = ROOT / "README.md"
API_KEY = os.environ.get("GEMINI_API_KEY")
MODEL = os.environ.get("GEMINI_MODEL", "gemini-3.6-flash")

if not API_KEY:
    raise SystemExit("GEMINI_API_KEY is required")
if not README.exists():
    raise SystemExit("README.md was not found at the repository root")

readme = README.read_text(encoding="utf-8")
if len(readme) > 180_000:
    raise SystemExit("README.md is too large for this workflow; reduce it below 180,000 characters")

ALLOWED_ROOTS = ("app/", "gradle/", "buildSrc/", "scripts/")
ALLOWED_EXACT = {"README.md", "settings.gradle.kts", "build.gradle.kts", "gradle.properties", ".gitignore"}


def call_gemini_api(prompt: str, max_tokens: int = 32000) -> dict:
    """Envía la solicitud a la API con esquema estricto y backoff respetuoso."""
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{MODEL}:generateContent"
    payload = {
        "contents": [{"role": "user", "parts": [{"text": prompt}]}],
        "generationConfig": {
            "temperature": 0.15,
            "responseMimeType": "application/json",
            "maxOutputTokens": max_tokens,
            "responseSchema": {
                "type": "OBJECT",
                "properties": {
                    "summary": {"type": "STRING"},
                    "files": {
                        "type": "ARRAY",
                        "items": {
                            "type": "OBJECT",
                            "properties": {
                                "path": {"type": "STRING"},
                                "content": {"type": "STRING"},
                            },
                            "required": ["path", "content"],
                        },
                    },
                },
                "required": ["summary", "files"],
            },
        },
    }
    headers = {
        "Content-Type": "application/json",
        "X-Goog-Api-Key": API_KEY,
    }

    max_retries = 3
    base_delay = 10

    for attempt in range(1, max_retries + 1):
        try:
            request = urllib.request.Request(
                url,
                data=json.dumps(payload).encode("utf-8"),
                headers=headers,
                method="POST",
            )
            with urllib.request.urlopen(request, timeout=300) as response:
                body = json.load(response)
                text = body["candidates"][0]["content"]["parts"][0]["text"]
                return json.loads(text)
        except (urllib.error.HTTPError, KeyError, json.JSONDecodeError) as exc:
            if attempt < max_retries:
                sleep_time = base_delay * attempt
                print(f"[Aviso] Reintento suave ({attempt}/{max_retries}) en {sleep_time}s por: {exc}", file=sys.stderr)
                time.sleep(sleep_time)
            else:
                raise SystemExit(f"Falla crítica en comunicación con Gemini API: {exc}") from exc


def write_files(files_list: list) -> list[str]:
    """Valida la seguridad de las rutas y escribe los archivos en disco."""
    written = []
    for item in files_list:
        relative = item["path"].replace("\\", "/")
        target = (ROOT / relative).resolve()

        try:
            target.relative_to(ROOT)
        except ValueError:
            raise SystemExit(f"Ruta insegura detectada fuera de ROOT: {relative}")

        if relative.startswith("/") or ".." in pathlib.PurePosixPath(relative).parts:
            raise SystemExit(f"Ruta relativa no permitida: {relative}")

        if relative not in ALLOWED_EXACT and not relative.startswith(ALLOWED_ROOTS):
            raise SystemExit(f"Ruta fuera de los límites permitidos del proyecto: {relative}")

        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(item["content"], encoding="utf-8")
        written.append(relative)
    return written


# ==========================================
# FASE 1: Estructura base y Configuración
# ==========================================
print("Ejecutando Fase 1: Generación de infraestructura y scripts de build...", file=sys.stderr)
prompt_phase_1 = f"""
You are the senior Android engineer for the VVC SPEED SPEAK project.
PHASE 1 TASK: Generate ONLY the project foundation and build setup.

Requirements for Phase 1:
- Root files: settings.gradle.kts, build.gradle.kts, gradle.properties, .gitignore.
- App files: app/build.gradle.kts, app/src/main/AndroidManifest.xml.
- Application ID / Package: com.vvc.speedspeak
- minSdk: 26, targetSdk/compileSdk: 35.
- Enable Jetpack Compose and Kotlin DSL. Include dependencies for Compose UI, Material3, Lifecycle ViewModel, and Coroutines.

Return ONLY valid JSON with 'summary' and 'files'.

Repository spec:
--- README.md ---
{readme}
--- END README.md ---
"""

result_p1 = call_gemini_api(prompt_phase_1, max_tokens=8000)
written_files = write_files(result_p1.get("files", []))

time.sleep(5)

# ==========================================
# FASE 2: Lógica Kotlin, UI Compose y Tests
# ==========================================
print("Ejecutando Fase 2: Generación de código Kotlin, UI Compose y Reporte...", file=sys.stderr)
prompt_phase_2 = f"""
You are the senior Android engineer for the VVC SPEED SPEAK project.
PHASE 2 TASK: Generate Essential Kotlin source code, UI Compose, unit tests, and report.

Keep implementation clean, precise, and modular. Do not write oversized boilerplate.

Requirements:
- Main screen with Jetpack Compose (cyberpunk style: deep black, neon violet, neon cyan).
- ViewModel with state handling (text input, voice/speed, play/pause/stop).
- TTS abstraction & mock implementation.
- Text splitting logic + 1 Unit Test file.
- Report at: app/src/main/assets/VVC_SPEED_SPEAK_APPLICATION_REPORT.md

Return ONLY valid JSON with 'summary' and 'files'.
"""

result_p2 = call_gemini_api(prompt_phase_2, max_tokens=32000)
written_files.extend(write_files(result_p2.get("files", [])))

report = ROOT / "app/src/main/assets/VVC_SPEED_SPEAK_APPLICATION_REPORT.md"
if not report.exists():
    report.parent.mkdir(parents=True, exist_ok=True)
    report.write_text(
        "# VVC SPEED SPEAK — Informe de generación\n\n"
        + str(result_p2.get("summary", "Generación de proyecto completada en 2 fases."))
        + "\n",
        encoding="utf-8",
    )

print(
    json.dumps(
        {
            "model": MODEL,
            "status": "success",
            "files_written": sorted(set(written_files)),
            "report": str(report),
        },
        ensure_ascii=False,
        indent=2,
    )
                )
