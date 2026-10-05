param([string]$MpsRoot='C:\Work\MPS-Web')
$ErrorActionPreference='Stop'
$root=Split-Path $PSScriptRoot
$expectedRevision='1ae803e4f8c52e470c94563059eca4b4b8397c8c'
$trustedRoot=(Resolve-Path -LiteralPath $MpsRoot).Path.Replace('\','/')
$actualRevision=(& git -c "safe.directory=$trustedRoot" -C $MpsRoot rev-parse HEAD)
if ($LASTEXITCODE -ne 0 -or $actualRevision -ne $expectedRevision) { throw 'MPS checkout revision differs from the consumer baseline.' }
$paths=@(
  'core/openapi/source/org/jetbrains/mps/openapi/persistence/Memento.java',
  'core/kernel/source/jetbrains/mps/persistence/MementoImpl.java',
  'core/kernel/source/jetbrains/mps/persistence/MementoUtil.java',
  'core/kernel/kernelSolution/source_gen/jetbrains/mps/util/xml/XMLSAXHandler.java',
  'core/kernel/kernelSolution/source_gen/jetbrains/mps/util/xml/BreakParseSAXException.java'
)
$records=@(foreach($relative in $paths) {
  $original=Join-Path $MpsRoot $relative
  $text=Get-Content -LiteralPath $original -Raw
  $generatedAnnotation=$text -match '@GeneratedClass\('
  $text=$text -replace '(?m)^import jetbrains.mps.annotations.GeneratedClass;\r?\n','' -replace '(?m)^@GeneratedClass\([^\r\n]*\)\r?\n',''
  $transformation='Verbatim source copy'
  if($generatedAnnotation) {
    $text="// Adapted for J2CL tests: GeneratedClass annotation/import removed; executable code unchanged.`n"+$text
    $transformation='GeneratedClass annotation/import removed; adaptation notice added; executable code unchanged'
  }
  if($text -notmatch 'package ([\w.]+);'){throw 'Missing package'}
  $destination=Join-Path $root ('tests/mps-source/'+$matches[1].Replace('.','/')+'/'+(Split-Path $original -Leaf))
  New-Item -ItemType Directory -Force (Split-Path $destination) | Out-Null
  [IO.File]::WriteAllText($destination,$text)
  @{source=$relative;sha256=(Get-FileHash $original).Hash.ToLowerInvariant();transformation=$transformation}
})
@{revision='1ae803e4f8c52e470c94563059eca4b4b8397c8c';consumers=$records} | ConvertTo-Json -Depth 4 | Set-Content (Join-Path $root 'compatibility/mps-consumers.json')
