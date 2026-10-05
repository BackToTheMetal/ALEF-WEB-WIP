param([string]$Distribution = 'Ubuntu')
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskLinuxRoot = (& wsl.exe -d $Distribution --exec wslpath -a $taskRoot).Trim()
if ($LASTEXITCODE -ne 0) { throw 'WSL could not resolve the workspace path.' }
& wsl.exe -d $Distribution --exec python3 "$taskLinuxRoot/tools/bootstrap.py"
if ($LASTEXITCODE -ne 0) { throw 'MPS checkout verification failed.' }
