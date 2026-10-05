param(
    [string]$WslDistribution = "Ubuntu",
    [switch]$SkipModernBrowsers,
    [string]$ReferenceJavaHome,
    [switch]$RequireCompleteApi
)

$ErrorActionPreference = "Stop"
$workspace = (Resolve-Path ".").Path
$wslWorkspace = (& wsl -d $WslDistribution --exec wslpath -a $workspace).Trim()
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($wslWorkspace)) {
    throw "Could not resolve the workspace path in WSL distribution $WslDistribution."
}
if ([string]::IsNullOrWhiteSpace($ReferenceJavaHome)) {
    $ReferenceJavaHome = Split-Path (Split-Path (Get-Command javap -ErrorAction Stop).Source)
}
$wslReferenceJavaHome = (& wsl -d $WslDistribution --exec wslpath -a $ReferenceJavaHome).Trim()
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($wslReferenceJavaHome)) {
    throw "Could not resolve the reference JDK path in WSL."
}
function Invoke-WslValidationCommand {
    param([string]$Command)
    & wsl -d $WslDistribution --exec bash -lc "cd '$wslWorkspace' && $Command"
    if ($LASTEXITCODE -ne 0) {
        throw "WSL validation command failed: $Command"
    }
}
./tools/quality/Check-JavaFormatting.ps1
./tools/quality/Check-JavaFormatting.ps1 -SourceDirectory samples/dual_showcase
Invoke-WslValidationCommand "python3 -m unittest discover -s tools/quality -p 'test_*.py'"
Invoke-WslValidationCommand "python3 -m unittest discover -s tools/release -p 'test_*.py'"
Invoke-WslValidationCommand "python3 tools/quality/check_production_dependencies.py"
Invoke-WslValidationCommand "bazelisk test //:all-tests --nocache_test_results --test_output=errors"
Invoke-WslValidationCommand "bazelisk test //:display-tests --nocache_test_results --test_output=errors --run_under='xvfb-run -a'"
if (-not $SkipModernBrowsers) {
    Invoke-WslValidationCommand "bazelisk test //:modern-browser-tests --nocache_test_results --test_output=errors"
}
Invoke-WslValidationCommand "bazelisk build //..."
Invoke-WslValidationCommand "npm ci --ignore-scripts"
Invoke-WslValidationCommand "python3 tools/validate_runtime.py --skip-build"
Invoke-WslValidationCommand "python3 tools/compatibility/check_net_profile.py"
Invoke-WslValidationCommand "python3 tools/compatibility/check_package_ownership.py"
Invoke-WslValidationCommand "python3 tools/quality/check_bundle_budgets.py"
Invoke-WslValidationCommand "python3 -m unittest discover -s tools/compatibility -p 'test_*.py'"
Invoke-WslValidationCommand "python3 tools/compatibility/export_webawt_api.py"
./tools/compatibility/New-CompatibilityManifest.ps1
./tools/compatibility/New-CompatibilityReport.ps1
Invoke-WslValidationCommand "python3 tools/compatibility/check_profile_lock.py"
Invoke-WslValidationCommand "python3 tools/compatibility/compare_webawt_api.py --fail-on-incomplete-api"
Invoke-WslValidationCommand "python3 tools/compatibility/build_completeness_worklist.py"
Invoke-WslValidationCommand "python3 tools/compatibility/export_extended_api.py --java-home '$wslReferenceJavaHome'"
$extendedAuditOptions = " --require-complete"
Invoke-WslValidationCommand "python3 tools/compatibility/audit_extended_api.py$extendedAuditOptions"
Invoke-WslValidationCommand "python3 tools/compatibility/graphics_showcase_coverage.py$extendedAuditOptions"
Invoke-WslValidationCommand "python3 tools/compatibility/generate_consumer_probes.py --check"
Invoke-WslValidationCommand "python3 tools/compatibility/compile_reference_probes.py --java-home '$wslReferenceJavaHome'"
Write-Output "WebAWT development validation and API-profile completion passed. Remaining release work is recorded in FINALIZATION.md."
