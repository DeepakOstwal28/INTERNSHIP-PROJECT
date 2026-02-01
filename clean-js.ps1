# Script to remove all JavaScript comments
param(
  [string]$filePath
)

$content = Get-Content -Path $filePath -Raw
# Remove multi-line and single-line comments, preserving code
$newContent = $content -replace '\/\*[\s\S]*?\*\/', '' -replace '\/\/.*', ''
# Write back the cleaned content
Set-Content -Path $filePath -Value $newContent -NoNewline
Write-Host " Removed comments from: $filePath"
