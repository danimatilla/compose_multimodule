#!/bin/bash

# Script para probar el Deep Link de Login usando el esquema personalizado (Custom Scheme).
# Requisitos: Tener el dispositivo/emulador conectado y la app instalada.

# Esquema: dxmxp
# Ruta: /auth/login
# Comando: adb shell am start -W -a android.intent.action.VIEW -d "dxmxp:///auth/login"

echo "🚀 Lanzando Deep Link: Login (Custom Scheme)..."

adb shell am start -W \
    -a android.intent.action.VIEW \
    -d "dxmxp:///auth/login" \
    com.dxmxp.seed

echo "✅ Comando enviado. Revisa el Logcat (tag: RouteRegistry) para verificar la resolución."
