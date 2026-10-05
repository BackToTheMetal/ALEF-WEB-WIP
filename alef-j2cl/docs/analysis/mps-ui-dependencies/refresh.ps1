param(
    [Parameter(Mandatory=$true)][string]$Python,
    [Parameter(Mandatory=$true)][string]$Java
)
$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../../mps-j2cl/mps'))
$safe = 'safe.directory=' + $repo.Replace('\', '/')
Push-Location $PSScriptRoot
try {
    & $Java ApiInventory.java 21
    if ($LASTEXITCODE -ne 0) { throw 'API catalog generation failed' }
    git -c $safe -C $repo ls-files '*.java' '*.kt' '*.kts' | Set-Content -Encoding utf8 tracked-sources.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git source inventory failed' }
    git -c $safe -C $repo grep -l -E 'javax[.]swing|java[.]awt' -- '*.java' '*.kt' '*.kts' | Set-Content -Encoding utf8 candidate-sources.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git candidate scan failed' }
    git -c $safe -C $repo rev-parse HEAD | Set-Content -Encoding utf8 commit.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git commit read failed' }
    & $Python analyze.py
    if ($LASTEXITCODE -ne 0) { throw 'Reference analysis failed' }
    & $Python render_report.py
    if ($LASTEXITCODE -ne 0) { throw 'Report generation failed' }
} finally {
    Pop-Location
}
