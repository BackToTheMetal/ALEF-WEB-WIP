param(
    [string]$Distribution = 'Ubuntu',
    [ValidateSet('all', 'generate', 'j2cl')][string]$Phase = 'all',
    [string]$JdkHome = '/usr/lib/jvm/java-21-openjdk-amd64',
    [string]$SourceZip = '',
    [string]$Browser = ''
)
$ErrorActionPreference = 'Stop'
$linuxRoot = (& wsl -d $Distribution --exec wslpath -a $PSScriptRoot).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Could not resolve the trial folder in WSL.' }
$trialArgs = @('--phase', $Phase, '--jdk-home', $JdkHome)
if ($SourceZip) { $trialArgs += @('--source-zip', $SourceZip) }
if ($Browser) { $trialArgs += @('--browser', $Browser) }
& wsl -d $Distribution --cd $linuxRoot --exec bash run.sh @trialArgs
if ($LASTEXITCODE -ne 0) { throw "Metalava trial failed with exit code $LASTEXITCODE. See the reports folder." }
