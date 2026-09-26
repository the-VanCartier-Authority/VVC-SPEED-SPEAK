#!/usr/bin/env python3
"""Generate an Android/Kotlin project from README instructions using Gemini REST API."""
from __future__ import annotations

import json
import os
import pathlib
import sys
import urllib.error
import urllib.request

ROOT = pathlib.Path.cwd().resolve()
README = ROOT / "README.md"
API_KEY = os.environ.get("GEMINI_API_KEY")
MODEL = os.environ.get("GEMINI_MODEL", "gemini-3.6-flash")
MAX_OUTPUT = int(os.environ.get("GEMINI_MAX_OUTPUT_TOKENS", "60000"))

if not API_KEY:
    raise SystemExit("GEMINI_API_KEY is required")
if not README.exists():
    raise SystemExit("README.md was not found at the repository root")

readme = README.read_text(encoding="utf-8")
if len(readme) > 180_000:
    raise SystemExit("README.md is too large for this workflow; reduce it below 180,000 characters")

prompt = f"""
You are the senior Android engineer for the VVC SPEED SPEAK project.
Read the complete repository specification below and generate a complete, buildable Android APK project.
The application MUST be native Android written in Kotlin, using Jetpack Compose and Gradle Kotlin DSL.
Do not generate a web app, Flutter app, React Native app, Java app, or JavaScript app.

Requirements:
- package/namespace/applicationId: com.vvc.speedspeak
- minSdk: 26
- Compose UI with cyberpunk retrofuturist design: deep obsidian black, intense neon violet,
  neon cyan, neon red; accessible contrast; polished mobile-first UI.
- First MVP flow: paste long text, choose a voice, choose speed, generate/play audio,
  pause, resume, stop, clear, and show processing/error states.
- Do not use browser SpeechSynthesis. Implement a TTS abstraction and a clearly marked
  local/placeholder engine that compiles without proprietary credentials. Keep the code
  ready for a real local or remote natural TTS engine.
- Split long text safely by paragraphs/sentences.
- Include unit tests for text splitting and ViewModel state transitions.
- Include AndroidManifest, Gradle files, source code, tests, resources, and a Gradle wrapper
  configuration if possible. Never include binary files or secrets.
- Put the application report at app/src/main/assets/VVC_SPEED_SPEAK_APPLICATION_REPORT.md.

Rules for files:
- Every file must be complete, not a patch and not an ellipsis.
- Paths must be relative and use forward slashes.
- Allowed generated files are README.md, settings.gradle.kts, build.gradle.kts,
  gradle.properties, gradle/libs.versions.toml, app/**, buildSrc/**, scripts/**,
  and .gitignore.
- Do not return secrets, API keys, certificates, keystores, binaries, or files outside the repository.
- Preserve the requested report path and make the report explain architecture, features,
  design, TTS decision, tests, build instructions, and known limitations.

Repository specification:
--- README.md ---
{readme}
--- END README.md ---
"""

# Se elimina el API key de la URL para evitar exposición en logs de red
url = f"https://generativelanguage.googleapis.com/v1beta/models/{MODEL}:generateContent"

payload = {
    "contents": [{"role": "user", "parts": [{"text": prompt}]}],
    "generationConfig": {
        "temperature": 0.15,
        "responseMimeType": "application/json",
        "maxOutputTokens": MAX_OUTPUT,
        # Esquema forzado para asegurar validez estricta del JSON
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

# Se envía la API key en los headers de forma segura
headers = {
    "Content-Type": "application/json",
    "X-Goog-Api-Key": API_KEY,
}

request = urllib.request.Request(
    url,
    data=json.dumps(payload).encode("utf-8"),
    headers=headers,
    method="POST",
)

try:
    with urllib.request.urlopen(request, timeout=900) as response:
        body = json.load(response)
except urllib.error.HTTPError as exc:
    detail = exc.read().decode("utf-8", errors="replace")
    raise SystemExit(f"Gemini API HTTP {exc.code}: {detail}") from exc
except urllib.error.URLError as exc:
    raise SystemExit(f"Gemini API connection failed: {exc}") from exc

try:
    text = body["candidates"][0]["content"]["parts"][0]["text"]
except (KeyError, IndexError, TypeError) as exc:
    raise SystemExit(f"Gemini response did not contain generated content: {body}") from exc

try:
    result = json.loads(text)
except json.JSONDecodeError as exc:
    raise SystemExit(f"Gemini returned invalid JSON: {exc}\n{text[:2000]}") from exc

files = result.get("files")
if not isinstance(files, list) or not files:
    raise SystemExit("Gemini returned no files")

allowed_roots = ("app/", "gradle/", "buildSrc/", "scripts/")
allowed_exact = {"README.md", "settings.gradle.kts", "build.gradle.kts", "gradle.properties", ".gitignore"}
written = []

for item in files:
    if not isinstance(item, dict) or not isinstance(item.get("path"), str) or not isinstance(item.get("content"), str):
        raise SystemExit("Gemini returned a malformed file entry")
    
    relative = item["path"].replace("\\", "/")
    target = (ROOT / relative).resolve()
    
    # Validaciones estricta de Path Traversal
    try:
        target.relative_to(ROOT)
    except ValueError:
        raise SystemExit(f"Refusing unsafe generated path outside root: {relative}")

    if relative.startswith("/") or ".." in pathlib.PurePosixPath(relative).parts:
        raise SystemExit(f"Refusing unsafe generated path: {relative}")
        
    if relative not in allowed_exact and not relative.startswith(allowed_roots):
        raise SystemExit(f"Refusing file outside allowed project paths: {relative}")

    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(item["content"], encoding="utf-8")
    written.append(relative)

report = ROOT / "app/src/main/assets/VVC_SPEED_SPEAK_APPLICATION_REPORT.md"
if not report.exists():
    report.parent.mkdir(parents=True, exist_ok=True)
    report.write_text(
        "# VVC SPEED SPEAK — Informe de generación\n\n"
        + str(result.get("summary", "Generación completada por Gemini."))
        + "\n",
        encoding="utf-8",
    )

print(json.dumps({"model": MODEL, "files_written": sorted(set(written)), "report": str(report)}, ensure_ascii=False, indent=2))
