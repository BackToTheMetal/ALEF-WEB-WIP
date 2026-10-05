param([string]$MpsHome='C:\Program Files\JetBrains\MPS 2025.3', [switch]$Generate)
$ErrorActionPreference='Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
  if((Get-Content (Join-Path $MpsHome 'build.txt') -Raw).Trim() -ne 'MPS-253.28294.432'){throw 'Reference MPS build differs from the fixture baseline'}
  $compiler=Join-Path $MpsHome 'jbr/bin/javac.exe'
  $java=Join-Path $MpsHome 'jbr/bin/java.exe'
  $libraries=Join-Path $MpsHome 'lib/*'
  New-Item -ItemType Directory -Force out/mps-reference | Out-Null
  & $compiler -encoding UTF-8 -cp "out/classes;$libraries" -d out/mps-reference tests/reference/MpsFixtureGenerator.java tests/reference/MpsFixtureReload.java
  if($LASTEXITCODE -ne 0){throw 'MPS reference harness compilation failed'}
  if($Generate) {
    & $java '-Dfile.encoding=UTF-8' -cp "out/mps-reference;$libraries" MpsFixtureGenerator tests/fixtures/mps-2025.3
  } else {
    & $java '-Dfile.encoding=UTF-8' -cp "out/classes;out/mps-reference;$libraries" MpsFixtureReload out/mps-browser
  }
  if($LASTEXITCODE -ne 0){throw 'MPS reference harness failed'}
} finally {Pop-Location}
