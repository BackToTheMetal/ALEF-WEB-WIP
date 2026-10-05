param(
    [string]$Distribution = 'Ubuntu',
    [switch]$SkipBuild,
    [switch]$SkipGenerated,
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ValidationArguments
)
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskLinuxRoot = (& wsl.exe -d $Distribution --exec wslpath -a $taskRoot).Trim()
if ($LASTEXITCODE -ne 0) { throw 'WSL could not resolve the workspace path.' }
$taskArguments = @()
if ($SkipBuild) { $taskArguments += '--skip-build' }
if ($SkipGenerated) { $taskArguments += '--skip-generated' }
$taskArguments += $ValidationArguments
& wsl.exe -d $Distribution --exec python3 "$taskLinuxRoot/tools/validate.py" @taskArguments
exit $LASTEXITCODE
