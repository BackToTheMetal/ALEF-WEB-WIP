param([string]$Distribution = 'Ubuntu', [string]$Browser = '')

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$linuxRoot = (& wsl.exe -d $Distribution --exec wslpath -a $projectRoot).Trim()
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($linuxRoot)) {
    throw 'Unable to resolve the java-concurrent-j2cl workspace in WSL.'
}

& wsl.exe -d $Distribution --exec bash "$linuxRoot/tools/validate.sh" $Browser
if ($LASTEXITCODE -ne 0) {
    throw "Validation failed with exit code $LASTEXITCODE"
}
