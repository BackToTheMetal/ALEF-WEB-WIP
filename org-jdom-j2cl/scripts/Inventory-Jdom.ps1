param(
    [string]$MpsRoot = 'C:\Work\MPS-Web',
    [string]$OutputDirectory = (Join-Path $PSScriptRoot '..\analysis')
)

$ErrorActionPreference = 'Stop'
$mpsPath = (Resolve-Path -LiteralPath $MpsRoot).Path
$safePath = $mpsPath.Replace('\', '/')
$revision = & git -c "safe.directory=$safePath" -C $mpsPath rev-parse HEAD
if ($LASTEXITCODE -ne 0) { throw 'Cannot identify the MPS revision.' }
$imports = @(& git -c "safe.directory=$safePath" -C $mpsPath grep -n -E 'import (static )?org\.jdom' -- '*.java' '*.kt')
if ($LASTEXITCODE -gt 1) { throw 'Cannot scan MPS imports.' }
$references = @(& git -c "safe.directory=$safePath" -C $mpsPath grep -n 'org\.jdom' -- '*.java' '*.kt')
if ($LASTEXITCODE -gt 1) { throw 'Cannot scan MPS qualified references.' }
$records = @($imports | ForEach-Object {
    if ($_ -match '^(.+?):(\d+):.*import (?:static )?(org\.jdom[^; ]*)') {
        [pscustomobject]@{ path = $matches[1]; line = [int]$matches[2]; importedName = $matches[3] }
    }
})
$files = @($records.path | Sort-Object -Unique)
$summary = [ordered]@{
    generatedAt = (Get-Date).ToUniversalTime().ToString('o')
    sourceRoot = $mpsPath
    revision = $revision.Trim()
    method = 'git grep over tracked working-tree Java/Kotlin files; includes generated sources, tests, tools, and samples'
    limitation = 'Textual inventory, not resolved member usage or runtime reachability; excludes dependency jars, untracked files, and MPS model inspection'
    importLineCount = $imports.Count
    importingFileCount = $files.Count
    qualifiedReferenceLineCount = $references.Count
    importsByName = @($records | Group-Object importedName | Sort-Object Count -Descending | ForEach-Object {
        [ordered]@{ name = $_.Name; count = $_.Count }
    })
    filesByArea = @($files | Group-Object { ($_ -split '/')[0] } | Sort-Object Count -Descending | ForEach-Object {
        [ordered]@{ area = $_.Name; count = $_.Count }
    })
}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$imports | Set-Content -LiteralPath (Join-Path $OutputDirectory 'mps-jdom-imports.txt') -Encoding utf8
$references | Set-Content -LiteralPath (Join-Path $OutputDirectory 'mps-jdom-references.txt') -Encoding utf8
$summary | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath (Join-Path $OutputDirectory 'mps-jdom-summary.json') -Encoding utf8
$summary | ConvertTo-Json -Depth 6
