$ErrorActionPreference = "Stop"

function Check-Command {
    param (
        [string]$Command
    )

    if (-not (Get-Command $Command -ErrorAction SilentlyContinue)) {
        Write-Host "$Command is required but not installed. Aborting."
        exit 1
    }
}

Check-Command "mvn"
Check-Command "java"
Check-Command "jpackage"

if (-not (Test-Path "pom.xml")) {
    Write-Host "Error: Run this script from the project root directory (where pom.xml is located)."
    exit 1
}

Write-Host "Building with Maven..."
mvn clean package
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven build failed. Aborting."
    exit 1
}

New-Item -ItemType Directory -Force -Path "build\Windows"
New-Item -ItemType Directory -Force -Path "build\temp"

$JAR_FILE = Get-ChildItem -Recurse -Filter "*SHADED.jar" -Path "target" | Select-Object -First 1

if (-not $JAR_FILE) {
    Write-Host "Error: No SHADED JAR file found. Maven build might have failed."
    exit 1
}

Write-Host "Found JAR file: $($JAR_FILE.FullName)"

Copy-Item $JAR_FILE.FullName -Destination "build\temp"

if (Test-Path "target\generated-resources") {
    Copy-Item "target\generated-resources\*" -Destination "build\temp" -Recurse
} else {
    Write-Host "Warning: No generated resources found. Proceeding without them."
}

$command = "jpackage --name PictureComparerFX --input build\temp --main-jar PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar --type msi --icon images\thumbnail.ico --main-class pl.magzik.picture_comparer_fx.Main --dest build\Windows"

Write-Host "Running: $command"
Invoke-Expression $command

Remove-Item "build\temp" -Recurse -Force

Write-Host "Build completed successfully."
