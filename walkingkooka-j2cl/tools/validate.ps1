$ErrorActionPreference = 'Stop'
$linuxPath = (wsl -d Ubuntu -- wslpath -a $PSScriptRoot).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Cannot resolve WSL path' }
wsl -d Ubuntu -- bash "$linuxPath/validate.sh"
if ($LASTEXITCODE -ne 0) { throw 'java.time validation failed' }
