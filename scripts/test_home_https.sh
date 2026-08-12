#!/bin/bash

# Script para probar el Deep Link de Home usando App Links (HTTPS).
# Esto verifica que el intent-filter de host y scheme http/https funcione.

# Host: seed.dxmxp.com
# Ruta: /seed/home
# Comando: adb shell am start -W -a android.intent.action.VIEW -d "https://seed.dxmxp.com/seed/home"

echo "🚀 Lanzando Deep Link: Home (HTTPS)..."

adb shell am start -W \
    -a android.intent.action.VIEW \
    -d "https://seed.dxmxp.com/seed/home" \
    com.dxmxp.seed

echo "✅ Comando enviado. Revisa el Logcat para confirmar que abrió la pantalla de Home."
