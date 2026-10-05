param(
    [string]$SourceDirectory = "src"
)

$ErrorActionPreference = "Stop"
if (-not (Test-Path -LiteralPath $SourceDirectory)) {
    Write-Output "No Java source directory exists yet: $SourceDirectory"
    exit 0
}
$violations = [System.Collections.Generic.List[string]]::new()
$sourceFiles = Get-ChildItem -LiteralPath $SourceDirectory -Recurse -File -Filter "*.java"
foreach ($sourceFile in $sourceFiles) {
    $lines = @(Get-Content -LiteralPath $sourceFile.FullName)
    $braceDepth = 0
    $methodDepths = [System.Collections.Generic.Stack[int]]::new()
    for ($index = 0; $index -lt $lines.Count; $index++) {
        $line = $lines[$index]
        $lineNumber = $index + 1
        if ($line -match '\t') {
            $violations.Add("$($sourceFile.FullName):$lineNumber contains a tab")
        }
        if ($line -match '\s+$') {
            $violations.Add("$($sourceFile.FullName):$lineNumber contains trailing whitespace")
        }
        if ($line -match '^\s*import\s+(?:static\s+)?[^;]+\.\*\s*;') {
            $violations.Add("$($sourceFile.FullName):$lineNumber uses a wildcard import")
        }
        if ($methodDepths.Count -gt 0 -and [string]::IsNullOrWhiteSpace($line)) {
            $violations.Add("$($sourceFile.FullName):$lineNumber contains a blank line inside a method")
        }
        $trimmedLine = $line.Trim()
        $isControlStatement = $trimmedLine -match '^(if|for|while|switch|catch|synchronized)\s*\('
        $opensMethod = -not $isControlStatement -and $trimmedLine -match '\)\s*(?:throws\s+[^\{]+)?\{\s*$'
        $openingBraces = ([regex]::Matches($line, '\{')).Count
        $closingBraces = ([regex]::Matches($line, '\}')).Count
        $braceDepth += $openingBraces
        if ($opensMethod) {
            $methodDepths.Push($braceDepth)
        }
        $braceDepth -= $closingBraces
        while ($methodDepths.Count -gt 0 -and $braceDepth -lt $methodDepths.Peek()) {
            [void]$methodDepths.Pop()
        }
    }
}
if ($violations.Count -gt 0) {
    $violations | Write-Error
    throw "Java formatting validation failed with $($violations.Count) violation(s)."
}
Write-Output "Java formatting validation passed for $($sourceFiles.Count) file(s)."
