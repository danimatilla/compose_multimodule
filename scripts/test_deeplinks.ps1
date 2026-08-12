# Script de PowerShell para probar Deep Links de forma interactiva.

function Show-Menu {
    param (
        [string]$Title = 'Prueba de Deep Links - Seed App'
    )
    Clear-Host
    Write-Host "=== $Title ===" -ForegroundColor Cyan
    Write-Host "1. Login (Custom Scheme: dxmxp:///auth/login)"
    Write-Host "2. Home (HTTPS: https://seed.dxmxp.com/seed/home)"
    Write-Host "3. Story Detail (Param: dxmxp:///stories/storydetail?id=123)"
    Write-Host "Q. Salir"
}

do {
    Show-Menu
    $selection = Read-Host "Selecciona una opción"

    switch ($selection) {
        '1' {
            Write-Host "🚀 Lanzando Login..." -ForegroundColor Yellow
            adb shell am start -W -a android.intent.action.VIEW -d "dxmxp:///auth/login" com.dxmxp.seed
        }
        '2' {
            Write-Host "🚀 Lanzando Home HTTPS..." -ForegroundColor Yellow
            adb shell am start -W -a android.intent.action.VIEW -d "https://seed.dxmxp.com/seed/home" com.dxmxp.seed
        }
        '3' {
            $id = Read-Host "Introduce ID de la historia (por defecto 123)"
            if ([string]::IsNullOrWhiteSpace($id)) { $id = "123" }
            Write-Host "🚀 Lanzando Story Detail ID: $id..." -ForegroundColor Yellow
            adb shell am start -W -a android.intent.action.VIEW -d "dxmxp:///stories/storydetail?id=$id" com.dxmxp.seed
        }
    }
    if ($selection -ne 'q') {
        pause
    }
} while ($selection -ne 'q')
