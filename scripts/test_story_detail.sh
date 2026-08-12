#!/bin/bash

# Script para probar el Deep Link de Detalle de Historia con parámetros.
# Este ejemplo prueba la resolución de rutas parametrizadas [P] y el parseo de argumentos.

# ID de prueba: 123
# Ruta: /stories/storydetail
# Comando: adb shell am start -W -a android.intent.action.VIEW -d "dxmxp:///stories/storydetail?id=123"

STORY_ID=${1:-"123"}

echo "🚀 Lanzando Deep Link: Story Detail (ID: $STORY_ID)..."

adb shell am start -W \
    -a android.intent.action.VIEW \
    -d "dxmxp:///stories/storydetail?id=$STORY_ID" \
    com.dxmxp.seed

echo "✅ Comando enviado. Verifica en Logcat el parseo de parámetros:"
echo "   Busca: 'Params: {id=$STORY_ID}'"
