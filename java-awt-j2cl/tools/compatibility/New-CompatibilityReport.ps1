param(
    [string]$ManifestPath = "compatibility/compatibility-manifest.json",
    [string]$OutputPath = "compatibility/REPORT.md"
)

$ErrorActionPreference = "Stop"
$manifest = Get-Content -LiteralPath $ManifestPath -Raw | ConvertFrom-Json
$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# WebAWT compatibility report")
$lines.Add("")
$lines.Add("This report combines the planning classification with explicitly tracked implementation state. A type is only marked implemented after it transpiles and its current tests pass.")
$lines.Add("")
$lines.Add("Reference inventory: ``$($manifest.referenceInventory)``")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Status | Types | Members |")
$lines.Add("| --- | ---: | ---: |")
foreach ($status in $manifest.statuses) {
    $typeCount = @($manifest.entries | Where-Object supportStatus -eq $status).Count
    $memberCount = @($manifest.entries.members | Where-Object supportStatus -eq $status).Count
    $lines.Add("| $status | $typeCount | $memberCount |")
}
$lines.Add("")
$lines.Add("## Implementation state")
$lines.Add("")
$lines.Add("| State | Types | Members |")
$lines.Add("| --- | ---: | ---: |")
foreach ($state in @("implemented", "partial", "not-started")) {
    $typeCount = @($manifest.entries | Where-Object implementationState -eq $state).Count
    $memberCount = @($manifest.entries.members | Where-Object implementationState -eq $state).Count
    $lines.Add("| $state | $typeCount | $memberCount |")
}
$lines.Add("")
$lines.Add("## Explicitly excluded")
$lines.Add("")
foreach ($entry in $manifest.entries | Where-Object supportStatus -eq "excluded" | Sort-Object type) {
    $lines.Add("- ``$($entry.type)``")
}
$lines.Add("")
$lines.Add("## Unavailable platform APIs")
$lines.Add("")
foreach ($entry in $manifest.entries | Where-Object supportStatus -eq "unavailable" | Sort-Object type) {
    $lines.Add("- ``$($entry.type)``: $($entry.reason)")
}
$lines.Add("")
$lines.Add("## Next audit steps")
$lines.Add("")
$lines.Add("1. Review deferred and limited entries method by method before implementation.")
$lines.Add("2. Expand JVM differential vectors as each portable type is implemented.")
$lines.Add("3. Run SonarQube analysis when a server or CI service is configured.")
$lines.Add("4. Update implementation state only after code, tests, browser verification and quality checks pass.")
$resolvedOutputPath = Join-Path (Get-Location) $OutputPath
$lines | Set-Content -LiteralPath $resolvedOutputPath -Encoding utf8
Write-Output "Wrote compatibility report to $resolvedOutputPath"
