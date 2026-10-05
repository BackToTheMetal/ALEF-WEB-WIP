param([string]$Distribution = 'Ubuntu')

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$linuxRoot = (& wsl.exe -d $Distribution -- wslpath -a $projectRoot).Trim()
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($linuxRoot)) {
  throw 'Unable to resolve the java-io-j2cl workspace in WSL.'
}

& wsl.exe -d $Distribution -- bash "$linuxRoot/tools/validate.sh"
if ($LASTEXITCODE -ne 0) {
  throw "Validation failed with exit code $LASTEXITCODE"
}
