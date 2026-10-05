param([string]$MpsHome='C:\Program Files\JetBrains\MPS 2025.3', [string]$Distribution='Ubuntu')
$ErrorActionPreference='Stop'
$root=Split-Path $PSScriptRoot
Push-Location $root
try {
  if(!(Test-Path out/upstream/org/jdom/Element.java)){& ./scripts/Fetch-Upstream.ps1}
  & ./scripts/Test-Jvm.ps1 -JaxenJar (Join-Path $MpsHome 'lib/module-intellij.libraries.jaxen.jar')
  & ./scripts/Inventory-Api.ps1 -JaxenJar (Join-Path $MpsHome 'lib/module-intellij.libraries.jaxen.jar')
  node scripts/write-evidence.mjs
  if($LASTEXITCODE -ne 0){throw 'Evidence generation failed'}
  $linuxRoot=(& wsl -d $Distribution -e wslpath -a $root).Trim()
  if($LASTEXITCODE -ne 0){throw 'Cannot resolve workspace in WSL'}
  & wsl -d $Distribution -e flock /tmp/j2cl-build.lock bash -lc 'cd -- "$1" || exit; npm ci --no-audit --no-fund && bazelisk build //tests:browser //tests:browser_dev --jobs=1 --local_test_jobs=1 --worker_max_instances=1 --worker_max_multiplex_instances=1 --noshow_progress > out/j2cl-build.log 2>&1 && npm run test:browser; result=$?; bazelisk shutdown; exit "$result"' _ $linuxRoot
  if($LASTEXITCODE -ne 0){throw 'J2CL/browser verification failed; see out/j2cl-build.log'}
  & ./scripts/Test-MpsReference.ps1 -MpsHome $MpsHome
  node scripts/prepare-release.mjs
  if($LASTEXITCODE -ne 0){throw 'Packaging failed'}
  & wsl -d $Distribution -e flock /tmp/j2cl-build.lock bash -lc 'cd -- "$1/out/fresh-consumer" || exit; bazelisk build //:browser --jobs=1 --local_test_jobs=1 --worker_max_instances=1 --worker_max_multiplex_instances=1 --noshow_progress > ../consumer-build.log 2>&1 && node "$1/scripts/test-consumer.mjs"; result=$?; bazelisk shutdown; exit "$result"' _ $linuxRoot
  if($LASTEXITCODE -ne 0){throw 'Independent consumer failed; see out/consumer-build.log'}
  'All JVM, API, browser, MPS reference and independent consumer checks passed.'
} finally {Pop-Location}
