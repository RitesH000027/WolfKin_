# Razorpay Setup Script for Windows PowerShell

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "   WolfKin Razorpay Setup" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "This script will help you configure Razorpay for your e-commerce platform." -ForegroundColor Yellow
Write-Host ""

# Check if .env file exists in root
if (!(Test-Path ".env")) {
    Write-Host "Creating .env file from .env.example..." -ForegroundColor Green
    Copy-Item ".env.example" ".env"
}

# Check if frontend/.env exists
if (!(Test-Path "frontend/.env")) {
    Write-Host "Creating frontend/.env file..." -ForegroundColor Green
    Copy-Item "frontend/.env.example" "frontend/.env"
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Step 1: Get Razorpay API Keys" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Go to: https://dashboard.razorpay.com/app/keys" -ForegroundColor White
Write-Host "2. Sign up or log in to your Razorpay account" -ForegroundColor White
Write-Host "3. Generate Test API Keys (for development)" -ForegroundColor White
Write-Host "4. Copy your Key ID and Key Secret" -ForegroundColor White
Write-Host ""

$openBrowser = Read-Host "Open Razorpay Dashboard in browser? (Y/N)"
if ($openBrowser -eq "Y" -or $openBrowser -eq "y") {
    Start-Process "https://dashboard.razorpay.com/app/keys"
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Step 2: Enter Your API Keys" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$keyId = Read-Host "Enter your Razorpay Key ID (starts with rzp_test_ or rzp_live_)"
$keySecret = Read-Host "Enter your Razorpay Key Secret" -MaskInput

if ([string]::IsNullOrWhiteSpace($keyId) -or [string]::IsNullOrWhiteSpace($keySecret)) {
    Write-Host ""
    Write-Host "Error: Both Key ID and Key Secret are required!" -ForegroundColor Red
    Write-Host "You can manually add them later to:" -ForegroundColor Yellow
    Write-Host "  - Root: .env file" -ForegroundColor Yellow
    Write-Host "  - Frontend: frontend/.env file" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Step 3: Updating Configuration Files" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Update root .env
Write-Host "Updating root .env file..." -ForegroundColor Green
$rootEnv = Get-Content ".env" -Raw
$rootEnv = $rootEnv -replace "RAZORPAY_KEY_ID=.*", "RAZORPAY_KEY_ID=$keyId"
$rootEnv = $rootEnv -replace "RAZORPAY_KEY_SECRET=.*", "RAZORPAY_KEY_SECRET=$keySecret"
$rootEnv | Set-Content ".env" -NoNewline

# Update frontend .env
Write-Host "Updating frontend/.env file..." -ForegroundColor Green
$frontendEnv = Get-Content "frontend/.env" -Raw
$frontendEnv = $frontendEnv -replace "VITE_RAZORPAY_KEY_ID=.*", "VITE_RAZORPAY_KEY_ID=$keyId"
$frontendEnv | Set-Content "frontend/.env" -NoNewline

# Update backend application-dev.yml
Write-Host "Updating backend/src/main/resources/application-dev.yml..." -ForegroundColor Green
$devYml = Get-Content "backend/src/main/resources/application-dev.yml" -Raw
$devYml = $devYml -replace "id: .*", "id: $keyId"
$devYml = $devYml -replace "secret: .*", "secret: $keySecret"
$devYml | Set-Content "backend/src/main/resources/application-dev.yml" -NoNewline

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   Setup Complete!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Your Razorpay API keys have been configured." -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Rebuild containers: docker-compose build" -ForegroundColor White
Write-Host "  2. Start services: docker-compose up -d" -ForegroundColor White
Write-Host "  3. Check logs: docker-compose logs -f backend" -ForegroundColor White
Write-Host "  4. Visit: http://localhost:3000" -ForegroundColor White
Write-Host ""

Write-Host "Test Payment:" -ForegroundColor Cyan
Write-Host "  - Use test card: 4111 1111 1111 1111" -ForegroundColor White
Write-Host "  - CVV: Any 3 digits" -ForegroundColor White
Write-Host "  - Expiry: Any future date" -ForegroundColor White
Write-Host "  - OTP: 123456" -ForegroundColor White
Write-Host ""

Write-Host "For more details, see RAZORPAY_SETUP.md" -ForegroundColor Yellow
Write-Host ""

$rebuild = Read-Host "Rebuild and restart containers now? (Y/N)"
if ($rebuild -eq "Y" -or $rebuild -eq "y") {
    Write-Host ""
    Write-Host "Rebuilding containers..." -ForegroundColor Green
    docker-compose build
    Write-Host ""
    Write-Host "Starting services..." -ForegroundColor Green
    docker-compose up -d
    Write-Host ""
    Write-Host "Done! Your application is starting..." -ForegroundColor Green
    Write-Host "Check status with: docker-compose ps" -ForegroundColor Yellow
}
