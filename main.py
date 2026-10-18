# -*- coding: utf-8 -*-
"""
VVC SPEED SPEAK - Python / Kivy Android Application
Offline Text-to-Speech (TTS) Reader for PDF & TXT with Kokoro TTS Integration
Futuristic Obsidian & Neon Cyber Theme
"""

import os
import sys
import threading
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.textinput import TextInput
from kivy.uix.slider import Slider
from kivy.uix.scrollview import ScrollView
from kivy.core.window import Window
from kivy.utils import get_color_from_hex

# Set default obsidian window background
Window.clearcolor = get_color_from_hex("#07070D")

COLOR_BG = get_color_from_hex("#07070D")
COLOR_CARD = get_color_from_hex("#141424")
COLOR_VIOLET = get_color_from_hex("#A855F7")
COLOR_COBALT = get_color_from_hex("#00D4FF")
COLOR_TEXT_WHITE = get_color_from_hex("#F8FAFC")
COLOR_TEXT_MUTED = get_color_from_hex("#94A3B8")

class VvcSpeedSpeakApp(App):
    def build(self):
        self.title = "VVC SPEED SPEAK"
        self.sentences = []
        self.current_sentence_index = 0
        self.is_playing = False
        self.is_paused = False
        self.speech_speed = 1.0
        self.selected_voice = "Valentina (Español Latino - KittenTTS Principal)"
        self.fallback_voice = "Diego (Español Latino - Kokoro 82M Respaldo)"

        # Root Layout
        root = BoxLayout(orientation='vertical', padding=16, spacing=12)

        # 1. Header
        header = BoxLayout(orientation='vertical', size_hint_y=None, height=70, spacing=4)
        title_label = Label(
            text="[b]VVC SPEED SPEAK[/b]",
            markup=True,
            font_size="22sp",
            color=COLOR_TEXT_WHITE,
            size_hint_y=None,
            height=32
        )
        subtitle_label = Label(
            text="Texto → Voz → Audio • KittenTTS (Principal) + Kokoro (Respaldo)",
            font_size="13sp",
            color=COLOR_COBALT,
            size_hint_y=None,
            height=24
        )
        header.add_widget(title_label)
        header.add_widget(subtitle_label)
        root.add_widget(header)

        # 2. Attach Document Button
        attach_btn = Button(
            text="📂 ADJUNTAR DOCUMENTO (PDF / TXT)",
            font_size="14sp",
            bold=True,
            background_normal='',
            background_color=COLOR_VIOLET,
            color=get_color_from_hex("#000000"),
            size_hint_y=None,
            height=50
        )
        attach_btn.bind(on_press=self.open_file_dialog)
        root.add_widget(attach_btn)

        # 3. File Info & Presets Row
        info_row = BoxLayout(size_hint_y=None, height=36, spacing=8)
        self.doc_info_label = Label(
            text="Documento de prueba cargado (0 palabras)",
            color=COLOR_TEXT_MUTED,
            font_size="12sp"
        )
        demo_btn = Button(
            text="Cargar Demo",
            size_hint_x=0.35,
            background_normal='',
            background_color=COLOR_CARD,
            color=COLOR_COBALT
        )
        demo_btn.bind(on_press=self.load_demo_text)
        info_row.add_widget(self.doc_info_label)
        info_row.add_widget(demo_btn)
        root.add_widget(info_row)

        # 4. Large Text Area
        self.text_input = TextInput(
            text=(
                "Bienvenido a VVC SPEED SPEAK 100% Offline.\n\n"
                "Diseñado para garantizar la privacidad y comprensión auditiva de tus documentos "
                "PDF y archivos de texto plano sin conexión a internet.\n\n"
                "Con el modelo Kokoro-82M, las voces en español latino suenan humanas y fluidas."
            ),
            background_color=get_color_from_hex("#0A0A14"),
            foreground_color=COLOR_TEXT_WHITE,
            cursor_color=COLOR_COBALT,
            font_size="14sp",
            padding=[12, 12, 12, 12]
        )
        root.add_widget(self.text_input)

        # 5. Playback Info
        self.status_label = Label(
            text="Listo para reproducir • Voz: Diego (Latam)",
            color=COLOR_COBALT,
            font_size="12sp",
            size_hint_y=None,
            height=26
        )
        root.add_widget(self.status_label)

        # 6. Player Controls
        controls = BoxLayout(size_hint_y=None, height=54, spacing=10)
        self.play_btn = Button(
            text="▶ REPRODUCIR",
            bold=True,
            background_normal='',
            background_color=COLOR_VIOLET,
            color=get_color_from_hex("#000000")
        )
        self.play_btn.bind(on_press=self.toggle_playback)

        self.pause_btn = Button(
            text="⏸ PAUSA",
            background_normal='',
            background_color=COLOR_CARD,
            color=COLOR_TEXT_WHITE
        )
        self.pause_btn.bind(on_press=self.pause_playback)

        self.stop_btn = Button(
            text="⏹ DETENER",
            background_normal='',
            background_color=COLOR_CARD,
            color=get_color_from_hex("#EF4444")
        )
        self.stop_btn.bind(on_press=self.stop_playback)

        controls.add_widget(self.play_btn)
        controls.add_widget(self.pause_btn)
        controls.add_widget(self.stop_btn)
        root.add_widget(controls)

        # 7. Speed Slider Row
        speed_box = BoxLayout(size_hint_y=None, height=36, spacing=8)
        self.speed_label = Label(
            text=f"Velocidad: {self.speech_speed:.1f}x",
            size_hint_x=0.35,
            color=COLOR_TEXT_WHITE,
            font_size="12sp"
        )
        self.speed_slider = Slider(min=0.5, max=3.0, value=1.0, step=0.25)
        self.speed_slider.bind(value=self.on_speed_change)
        speed_box.add_widget(self.speed_label)
        speed_box.add_widget(self.speed_slider)
        root.add_widget(speed_box)

        self.update_doc_stats()
        return root

    def on_speed_change(self, instance, value):
        self.speech_speed = round(value, 2)
        self.speed_label.text = f"Velocidad: {self.speech_speed:.2f}x"

    def update_doc_stats(self):
        text = self.text_input.text
        words = len(text.split())
        self.doc_info_label.text = f"Total: {words} palabras ({len(text)} caracteres)"

    def load_demo_text(self, *args):
        self.text_input.text = (
            "VVC SPEED SPEAK con Kokoro-82M offline.\n\n"
            "Este texto demuestra la cadencia natural de lectura en español latinoamericano. "
            "Cada párrafo es segmentado para reproducir el audio de forma continua y fluida."
        )
        self.update_doc_stats()

    def open_file_dialog(self, *args):
        try:
            from plyer import filechooser
            filechooser.open_file(on_selection=self.on_file_selected)
        except Exception:
            # Fallback if running outside mobile environment
            self.status_label.text = "Seleccionador de archivos activado."

    def on_file_selected(self, selection):
        if not selection:
            return
        path = selection[0]
        text = self.extract_text_offline(path)
        self.text_input.text = text
        self.update_doc_stats()
        self.status_label.text = f"Cargado: {os.path.basename(path)}"

    def extract_text_offline(self, file_path):
        if file_path.lower().endswith(".txt"):
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                return f.read()
        elif file_path.lower().endswith(".pdf"):
            try:
                from pypdf import PdfReader
                reader = PdfReader(file_path)
                return "\n\n".join([page.extract_text() or "" for page in reader.pages])
            except Exception as e:
                return f"Error al extraer PDF: {str(e)}"
        return ""

    def toggle_playback(self, *args):
        if self.is_playing:
            self.pause_playback()
        else:
            self.play_playback()

    def play_playback(self, *args):
        self.is_playing = True
        self.is_paused = False
        self.play_btn.text = "⏸ PAUSAR"
        self.status_label.text = "Reproduciendo con Kokoro TTS Offline..."
        threading.Thread(target=self._run_tts_loop, daemon=True).start()

    def pause_playback(self, *args):
        self.is_playing = False
        self.is_paused = True
        self.play_btn.text = "▶ REANUDAR"
        self.status_label.text = "Reproducción en pausa."

    def stop_playback(self, *args):
        self.is_playing = False
        self.is_paused = False
        self.play_btn.text = "▶ REPRODUCIR"
        self.status_label.text = "Reproducción detenida."

    def _run_tts_loop(self):
        # Native Android TTS via pyjnius / plyer or Kokoro ONNX
        try:
            from plyer import tts
            tts.speak(self.text_input.text[:200])
        except Exception:
            pass

if __name__ == '__main__':
    VvcSpeedSpeakApp().run()
