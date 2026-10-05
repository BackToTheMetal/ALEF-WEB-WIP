param([string]$Distribution = 'Ubuntu')
$ErrorActionPreference = 'Stop'
$workspace = Split-Path -Parent $PSScriptRoot
$linuxWorkspace = (& wsl -d $Distribution --exec wslpath -a $workspace).Trim()
if ($LASTEXITCODE -ne 0 -or !$linuxWorkspace) { throw 'Cannot resolve workspace in WSL.' }
& wsl -d $Distribution --cd $linuxWorkspace --exec bash tools/validate.sh
if ($LASTEXITCODE -ne 0) { throw "NIO validation failed with exit code $LASTEXITCODE" }
