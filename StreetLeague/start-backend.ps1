# Libere le port 8086 (ancienne instance Spring Boot) puis demarre l'API.
$ErrorActionPreference = "Stop"
$port = 8086

$listeners = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
if ($listeners) {
    $pids = $listeners | Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($pid in $pids) {
        try {
            $p = Get-Process -Id $pid -ErrorAction SilentlyContinue
            if ($p) {
                Write-Host "Arret du processus $($p.ProcessName) (PID $pid) sur le port $port..."
                Stop-Process -Id $pid -Force
            }
        } catch {
            Write-Warning $_
        }
    }
    Start-Sleep -Seconds 1
}

Set-Location $PSScriptRoot
Write-Host "Demarrage StreetLeague sur http://localhost:${port}/StreetLeague ..."
& .\mvnw.cmd spring-boot:run
