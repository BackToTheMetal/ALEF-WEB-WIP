param(
    [string]$ApiInventoryPath = "compatibility/openjdk-awt-api.json",
    [string]$J2clInventoryPath = "compatibility/j2cl-jre-api.json",
    [string]$CompatibilityManifestPath = "compatibility/compatibility-manifest.json",
    [string]$OutputPath = "compatibility/j2cl-dependency-report.json"
)

$ErrorActionPreference = "Stop"
$apiInventory = Get-Content -LiteralPath $ApiInventoryPath -Raw | ConvertFrom-Json
$j2clInventory = Get-Content -LiteralPath $J2clInventoryPath -Raw | ConvertFrom-Json
$compatibilityManifest = Get-Content -LiteralPath $CompatibilityManifestPath -Raw | ConvertFrom-Json
$excludedTypes = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
foreach ($entry in $compatibilityManifest.entries) {
    if ($entry.supportStatus -eq "excluded") {
        [void]$excludedTypes.Add($entry.type)
    }
}
$j2clTypes = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
foreach ($typeName in $j2clInventory.types) {
    [void]$j2clTypes.Add($typeName)
}
$j2clPackages = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
foreach ($typeName in $j2clInventory.types) {
    [void]$j2clPackages.Add($typeName.Substring(0, $typeName.LastIndexOf('.')))
}
$references = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
foreach ($type in $apiInventory.types) {
    if ($excludedTypes.Contains($type.name)) {
        continue
    }
    $signatureText = @($type.declaration) + @($type.members)
    foreach ($signature in $signatureText) {
        foreach ($match in [regex]::Matches($signature, '\b(?:java|javax)\.[A-Za-z_$][A-Za-z0-9_$.]*')) {
            [void]$references.Add($match.Value.TrimEnd('.'))
        }
    }
}
$dependencies = foreach ($reference in $references | Sort-Object) {
    $resolvedType = $reference
    while (-not $j2clTypes.Contains($resolvedType) -and $resolvedType.Contains('.')) {
        $resolvedType = $resolvedType.Substring(0, $resolvedType.LastIndexOf('.'))
    }
    $lastSeparator = $reference.LastIndexOf('.')
    $referencePackage = if ($lastSeparator -gt 0) { $reference.Substring(0, $lastSeparator) } else { "" }
    $classification = "external-unclaimed-package"
    if ($reference.StartsWith("java.awt.")) {
        $classification = "awt-contract"
    } elseif ($j2clTypes.Contains($resolvedType)) {
        $classification = "stock-j2cl"
    } elseif ($j2clPackages.Contains($referencePackage)) {
        $classification = "missing-in-j2cl-owned-package"
    } elseif ($reference.StartsWith("javax.")) {
        $classification = "missing-javax-package"
    }
    [ordered]@{
        type = $reference
        classification = $classification
    }
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    awtInventory = $ApiInventoryPath
    j2clInventory = $J2clInventoryPath
    compatibilityManifest = $CompatibilityManifestPath
    dependencies = @($dependencies)
}
$resolvedOutputPath = Join-Path (Get-Location) $OutputPath
$report | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $resolvedOutputPath -Encoding utf8
$dependencies | Group-Object { $_["classification"] } | Sort-Object Name | Select-Object Name, Count
