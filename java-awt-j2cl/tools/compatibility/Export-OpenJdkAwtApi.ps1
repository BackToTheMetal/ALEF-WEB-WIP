param(
    [string]$OutputDirectory = "compatibility"
)

$ErrorActionPreference = "Stop"
$javaExecutable = (Get-Command java -ErrorAction Stop).Source
$javaHome = Split-Path (Split-Path $javaExecutable)
$jmodExecutable = Join-Path $javaHome "bin\jmod.exe"
$javapExecutable = Join-Path $javaHome "bin\javap.exe"
$desktopModule = Join-Path $javaHome "jmods\java.desktop.jmod"
if (-not (Test-Path -LiteralPath $desktopModule)) {
    throw "The selected Java installation does not contain java.desktop.jmod: $desktopModule"
}
$resolvedOutputDirectory = Join-Path (Get-Location) $OutputDirectory
New-Item -ItemType Directory -Force -Path $resolvedOutputDirectory | Out-Null
$exportedPackages = @(& $jmodExecutable describe $desktopModule | ForEach-Object { if ($_ -match '^exports (java\.awt(?:\.[A-Za-z0-9_.]+)?)$') { $matches[1] } })
$classNames = @(& $jmodExecutable list $desktopModule | ForEach-Object { if ($_ -notlike '*$*' -and $_ -match '^classes/(java/awt(?:/[^/]+)*?)/([^/]+)\.class$') { $packageName = $matches[1].Replace('/', '.'); $simpleName = $matches[2]; if ($exportedPackages -contains $packageName) { "$packageName.$simpleName" } } } | Sort-Object -Unique)
$types = foreach ($className in $classNames) {
    $javapOutput = @(& $javapExecutable --module java.desktop -public $className 2>&1)
    if ($LASTEXITCODE -ne 0) {
        throw "javap failed for $className`n$($javapOutput -join [Environment]::NewLine)"
    }
    $declaration = $javapOutput | Where-Object { $_ -match '^public\s+(?:(?:abstract|final|sealed|non-sealed|strictfp)\s+)*(?:class|interface|enum|record|@interface)\s+' } | Select-Object -First 1
    if ($null -eq $declaration) {
        continue
    }
    $members = @($javapOutput | Where-Object { $_ -match '^  (public|protected) ' } | ForEach-Object { $_.Trim() })
    [ordered]@{
        name = $className
        package = $className.Substring(0, $className.LastIndexOf('.'))
        declaration = $declaration.Trim()
        members = $members
    }
}
$javaVersion = (& $javaExecutable -version 2>&1 | Select-Object -First 1).ToString()
$inventory = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    javaVersion = $javaVersion
    module = "java.desktop"
    exportedPackages = $exportedPackages
    publicTopLevelTypeCount = @($types).Count
    types = @($types)
}
$inventoryPath = Join-Path $resolvedOutputDirectory "openjdk-awt-api.json"
$inventory | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $inventoryPath -Encoding utf8
Write-Output "Wrote $(@($types).Count) public top-level AWT types to $inventoryPath"
