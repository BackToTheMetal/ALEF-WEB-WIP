param([string]$Destination = (Join-Path $PSScriptRoot '../out/upstream'))
$ErrorActionPreference = 'Stop'
$tag = 'idea/262.8665.258'
$revision = '1822802cd32c2268237bc2734326fc8d51cf1f67'
$tree = Invoke-RestMethod 'https://api.github.com/repos/JetBrains/intellij-community/git/trees/1d885766394b747b6e99bd36af06bc317393959f?recursive=1'
New-Item -ItemType Directory -Force $Destination | Out-Null
$destinationPath = (Resolve-Path $Destination).Path
$tree.tree | Where-Object type -eq blob | ForEach-Object -Parallel {
    $path = Join-Path $using:destinationPath $_.path
    New-Item -ItemType Directory -Force (Split-Path $path) | Out-Null
    Invoke-WebRequest "https://raw.githubusercontent.com/JetBrains/intellij-community/$using:revision/platform/util/jdom/src/$($_.path)" -OutFile $path
} -ThrottleLimit 8
Invoke-WebRequest "https://raw.githubusercontent.com/JetBrains/intellij-community/$revision/platform/util/jdom/api-dump.txt" -OutFile (Join-Path $Destination 'api-dump.txt')
$manifest = [ordered]@{
    repository = 'https://github.com/JetBrains/intellij-community'
    tag = $tag
    commit = $revision
    sourceTree = '1d885766394b747b6e99bd36af06bc317393959f'
    sourcePath = 'platform/util/jdom/src'
    files = @(Get-ChildItem $Destination -Filter '*.java' -Recurse | ForEach-Object {
        [ordered]@{ path = [IO.Path]::GetRelativePath($destinationPath, $_.FullName).Replace('\','/'); sha256 = (Get-FileHash $_.FullName).Hash.ToLowerInvariant() }
    })
}
New-Item -ItemType Directory -Force (Join-Path $PSScriptRoot '../compatibility') | Out-Null
$manifest | ConvertTo-Json -Depth 6 | Set-Content (Join-Path $PSScriptRoot '../compatibility/upstream.json') -Encoding utf8
"Fetched $($manifest.files.Count) files from $revision"
