param([string]$JaxenJar='C:\Program Files\JetBrains\MPS 2025.3\lib\module-intellij.libraries.jaxen.jar')
$ErrorActionPreference='Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
  # Include interfaces as well as concrete classes; names and members come from the pinned API dump.
  Get-Content out/upstream/api-dump.txt | Where-Object { $_ -match '^([a-z]+:)?org\.jdom' } | ForEach-Object { $_ -replace '^[a-z]+:', '' } | Set-Content out/api-types.txt
  & javac -encoding UTF-8 -d out/api-tools tests/reference/ApiInventory.java
  if($LASTEXITCODE -ne 0){throw 'API tool compilation failed'}
  & java '-Dfile.encoding=UTF-8' -cp "out/api-tools;out/reference-classes;$JaxenJar" ApiInventory out/api-types.txt | Set-Content out/api-reference.tsv
  if($LASTEXITCODE -ne 0){throw 'Reference API inventory failed'}
  & java '-Dfile.encoding=UTF-8' -cp 'out/api-tools;out/classes' ApiInventory out/api-types.txt | Set-Content out/api-portable.tsv
  if($LASTEXITCODE -ne 0){throw 'Portable API inventory failed'}
  & javap -c -verbose -cp out/classes jetbrains.mps.persistence.MementoUtil jetbrains.mps.persistence.MementoImpl jetbrains.mps.util.xml.XMLSAXHandler jetbrains.mps.util.xml.BreakParseSAXException org.jetbrains.mps.openapi.persistence.Memento | Set-Content out/mps-consumer-bytecode.txt
  if($LASTEXITCODE -ne 0){throw 'MPS consumer member inventory failed'}
  node scripts/check-api.mjs
  if($LASTEXITCODE -ne 0){throw 'API compatibility gate failed'}
} finally {Pop-Location}
