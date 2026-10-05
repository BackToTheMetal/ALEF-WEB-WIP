param([string]$JaxenJar='C:\Program Files\JetBrains\MPS 2025.3\lib\module-intellij.libraries.jaxen.jar')
$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
    node scripts/prepare-reference.mjs
    if ($LASTEXITCODE -ne 0) { throw 'Reference source preparation failed.' }
    $workspacePath = (Get-Location).Path
    foreach ($relative in @('out/classes', 'out/reference-classes')) {
        $classesPath = [IO.Path]::GetFullPath((Join-Path $workspacePath $relative))
        $outputPrefix = [IO.Path]::GetFullPath((Join-Path $workspacePath 'out')) + [IO.Path]::DirectorySeparatorChar
        if (!$classesPath.StartsWith($outputPrefix, [StringComparison]::OrdinalIgnoreCase)) { throw 'Unsafe compilation output path' }
        if (Test-Path -LiteralPath $classesPath) { Remove-Item -LiteralPath $classesPath -Recurse -Force }
        New-Item -ItemType Directory -Path $classesPath | Out-Null
    }
    $runtimeSources = @(Get-ChildItem src/main/java,tests/contract,tests/portable,tests/mps-source -Recurse -Filter '*.java' | ForEach-Object FullName)
    & javac -encoding UTF-8 -d out/classes $runtimeSources
    if ($LASTEXITCODE -ne 0) { throw 'Portable JVM compilation failed.' }
    $referenceSources = @(Get-ChildItem out/reference-src,tests/contract -Recurse -Filter '*.java' | ForEach-Object FullName)
    & javac -encoding UTF-8 -cp $JaxenJar -d out/reference-classes $referenceSources
    if ($LASTEXITCODE -ne 0) { throw 'Reference JVM compilation failed.' }
    & java '-Dfile.encoding=UTF-8' -cp out/reference-classes org.mpsxml.ContractScenarios | Set-Content out/reference-contracts.txt
    if ($LASTEXITCODE -ne 0) { throw 'Reference contracts failed.' }
    & java '-Dfile.encoding=UTF-8' -cp out/classes org.mpsxml.ContractScenarios | Set-Content out/portable-contracts.txt
    if ($LASTEXITCODE -ne 0) { throw 'Portable contracts failed.' }
    if ((Get-Content out/reference-contracts.txt -Raw) -cne (Get-Content out/portable-contracts.txt -Raw)) { throw 'Reference and portable observations differ.' }
    & java '-Dfile.encoding=UTF-8' -cp out/classes org.mpsxml.PortableScenarios
    if ($LASTEXITCODE -ne 0) { throw 'Portable boundary tests failed.' }
    Get-Content out/portable-contracts.txt -TotalCount 1
    'Reference and portable contract observations match.'
} finally { Pop-Location }

