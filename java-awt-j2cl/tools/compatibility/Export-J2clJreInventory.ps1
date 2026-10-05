param(
    [Parameter(Mandatory = $true)]
    [string]$J2clJavaSourceDirectory,
    [string]$OutputPath = "compatibility/j2cl-jre-api.json",
    [string]$J2clVersion = "20260402",
    [string]$SourceIdentity
)

$ErrorActionPreference = "Stop"
$resolvedSourceDirectory = (Resolve-Path -LiteralPath $J2clJavaSourceDirectory).Path
$sourceFiles = Get-ChildItem -LiteralPath $resolvedSourceDirectory -Recurse -File -Filter "*.java"
$types = foreach ($sourceFile in $sourceFiles) {
    $content = Get-Content -LiteralPath $sourceFile.FullName -Raw
    if ($content -notmatch '(?m)^package\s+([A-Za-z0-9_.]+)\s*;') {
        continue
    }
    $packageName = $matches[1]
    $declarations = [regex]::Matches($content, '(?m)^public\s+(?:(?:abstract|final|sealed|non-sealed|strictfp)\s+)*(?:class|interface|enum|record|@interface)\s+([A-Za-z0-9_]+)')
    foreach ($declaration in $declarations) {
        "$packageName.$($declaration.Groups[1].Value)"
    }
}
$resolvedOutputPath = Join-Path (Get-Location) $OutputPath
New-Item -ItemType Directory -Force -Path (Split-Path $resolvedOutputPath) | Out-Null
$inventorySourceIdentity = if ($SourceIdentity) { $SourceIdentity } else { $resolvedSourceDirectory }
$inventory = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    j2clVersion = $J2clVersion
    sourceIdentity = $inventorySourceIdentity
    publicTopLevelTypeCount = @($types).Count
    types = @($types | Sort-Object -Unique)
}
$inventory | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $resolvedOutputPath -Encoding utf8
Write-Output "Wrote $(@($types).Count) public top-level J2CL JRE types to $resolvedOutputPath"
