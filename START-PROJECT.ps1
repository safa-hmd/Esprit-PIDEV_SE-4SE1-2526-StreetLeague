
# ============================================================
#  StreetLeague - Script de démarrage rapide
#  Lance Backend (Spring Boot) + Frontend (Angular) en parallèle
# ============================================================

$ROOT = Split-Path -Parent $MyInvocation.MyCommand.Definition
$BACKEND = Join-Path $ROOT "StreetLeague"
$FRONTEND = Join-Path $ROOT "StreetLeagueFront"

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   STREETLEAGUE - DÉMARRAGE DU PROJET" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host " Backend  → http://localhost:8085" -ForegroundColor Green
Write-Host " Frontend → http://localhost:4200" -ForegroundColor Yellow
Write-Host " Swagger  → http://localhost:8085/swagger-ui/index.html" -ForegroundColor Magenta
Write-Host ""
Write-Host " Lancement des services..." -ForegroundColor White
Write-Host "--------------------------------------------" -ForegroundColor DarkGray
Write-Host ""

# --- Lancer le Backend dans une nouvelle fenêtre ---
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Write-Host '=== BACKEND Spring Boot ===' -ForegroundColor Green; Write-Host 'Port: 8085' -ForegroundColor Cyan; Write-Host ''; Set-Location '$BACKEND'; .\mvnw.cmd spring-boot:run '-Dmaven.test.skip=true'"
) -WindowStyle Normal

# Attendre 2s pour ne pas surcharger le terminal
Start-Sleep -Seconds 2

# --- Lancer le Frontend dans une nouvelle fenêtre ---
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "Write-Host '=== FRONTEND Angular ===' -ForegroundColor Yellow; Write-Host 'Port: 4200' -ForegroundColor Cyan; Write-Host ''; Set-Location '$FRONTEND'; npm start"
) -WindowStyle Normal

Write-Host ""
Write-Host " ✅ Les deux services ont été lancés dans des fenêtres séparées." -ForegroundColor Green
Write-Host ""
Write-Host " 📌 Attends ~30-60s le démarrage complet du backend." -ForegroundColor White
Write-Host " 📌 Angular sera disponible en ~10-20s." -ForegroundColor White
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " Appuie sur ENTRÉE pour ouvrir le navigateur" -ForegroundColor White
Write-Host "============================================" -ForegroundColor Cyan
Read-Host

# Ouvrir le navigateur automatiquement
Start-Process "http://localhost:4200"
