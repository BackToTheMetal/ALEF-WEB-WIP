param([string]$Distribution = 'Ubuntu')
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskLinuxRoot = (& wsl.exe -d $Distribution -- wslpath -a $taskRoot).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Unable to resolve workspace in WSL.' }
& wsl.exe -d $Distribution -- bash "$taskLinuxRoot/tools/validate.sh"
if ($LASTEXITCODE -ne 0) { throw "Validation failed with exit code $LASTEXITCODE" }
