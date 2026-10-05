param(
    [Parameter(Mandatory=$true)][string]$Python,
    [Parameter(Mandatory=$true)][string]$Javac
)
$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../../mps-j2cl/mps'))
$safe = 'safe.directory=' + $repo.Replace('\', '/')
Push-Location $PSScriptRoot
try {
    git -c $safe -C $repo ls-files '*.java' '*.kt' '*.kts' | Set-Content -Encoding utf8 tracked-sources.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git source inventory failed' }
    git -c $safe -C $repo grep -l -E 'gnu[.]trove' -- '*.java' '*.kt' '*.kts' | Set-Content -Encoding utf8 candidate-sources.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git candidate scan failed' }
    git -c $safe -C $repo rev-parse HEAD | Set-Content -Encoding utf8 commit.txt
    if ($LASTEXITCODE -ne 0) { throw 'Git commit read failed' }
    & $Python analyze_trove.py
    if ($LASTEXITCODE -ne 0) { throw 'Reference analysis failed' }
    $data = Get-Content inventory.json -Raw | ConvertFrom-Json
    $port = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../../gnu-trove-j2cl/src/main/java')).Replace('\', '/')
    New-Item -ItemType Directory -Force -Path validation/empty-classpath | Out-Null
    $data.port_source_closure | ForEach-Object { '"' + $port + '/' + $_.Replace('.', '/') + '.java"' } | Set-Content -Encoding utf8 validation/sources.txt
    & $Javac --release 21 -proc:none -encoding UTF-8 -classpath validation/empty-classpath -sourcepath validation/empty-classpath -d validation/classes '@validation/sources.txt' 2>&1 | Tee-Object -FilePath validation/javac.log
    $taskTroveJavacExit = $LASTEXITCODE
    $taskTroveJavacExit | Set-Content validation/exit-code.txt
    if ($taskTroveJavacExit -ne 0) { throw 'Trove subset compilation failed' }
    & $Python render_report.py
    if ($LASTEXITCODE -ne 0) { throw 'Report generation failed' }
} finally {
    Pop-Location
}
