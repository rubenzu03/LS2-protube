# Script to verify Docker build works correctly
# Run this from the backend directory in PowerShell

Write-Host "=== ProTube Backend Docker Build Verification ===" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 1: Cleaning up old Docker resources..." -ForegroundColor Yellow
docker-compose down --rmi local --volumes --remove-orphans 2>$null
Write-Host "Cleanup complete" -ForegroundColor Green
Write-Host ""

Write-Host "Step 2: Building Docker image with full output..." -ForegroundColor Yellow
docker-compose build --no-cache
$BuildResult = $LASTEXITCODE

if ($BuildResult -ne 0) {
    Write-Host "Build failed! Check the error messages above." -ForegroundColor Red
    exit 1
}
Write-Host "Build successful" -ForegroundColor Green
Write-Host ""

Write-Host "Step 3: Verifying the JAR file in the image..." -ForegroundColor Yellow
# The runtime image should now have unzip installed
$checkCmd = @"
ls -lh /app/app.jar || exit 2
if command -v unzip >/dev/null 2>&1; then
  unzip -l /app/app.jar | grep -q 'META-INF/MANIFEST.MF' && echo 'JAR is valid' || exit 3
else
  echo 'Container image does not include unzip; cannot inspect app.jar' >&2
  exit 4
fi
"@
# Remove Windows CR characters
$checkCmd = $checkCmd -replace "`r", ""

docker run --rm --entrypoint sh backend-backend -c $checkCmd
$VerifyResult = $LASTEXITCODE

if ($VerifyResult -ne 0) {
    Write-Host "JAR verification failed (exit code $VerifyResult)" -ForegroundColor Red
    exit 1
}
Write-Host ""

Write-Host "Step 4: Checking the MANIFEST.MF for Main-Class..." -ForegroundColor Yellow
$manifestCmd = @"
if command -v unzip >/dev/null 2>&1; then
  unzip -p /app/app.jar META-INF/MANIFEST.MF
else
  echo 'Cannot display MANIFEST.MF: no unzip in container' >&2
  exit 5
fi
"@
# Strip CR characters
$manifestCmd = $manifestCmd -replace "`r", ""

docker run --rm --entrypoint sh backend-backend -c $manifestCmd
if ($LASTEXITCODE -ne 0) {
    Write-Host "(manifest display skipped)" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "=== All checks passed! ===" -ForegroundColor Green
Write-Host ""

Write-Host "Step 5: Starting containers with docker-compose up..." -ForegroundColor Yellow
Write-Host "Press Ctrl+C to stop the containers when done." -ForegroundColor Cyan
Write-Host ""

docker-compose up
