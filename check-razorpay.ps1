# Check Razorpay Configuration

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "   Razorpay Configuration Check" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

$allGood = $true

# Check root .env
Write-Host "Checking root .env file..." -ForegroundColor Yellow
if (Test-Path ".env") {
    $rootEnv = Get-Content ".env" -Raw
    if ($rootEnv -match "RAZORPAY_KEY_ID=rzp_") {
        Write-Host "  ✓ Root RAZORPAY_KEY_ID configured" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Root RAZORPAY_KEY_ID not configured" -ForegroundColor Red
        $allGood = $false
    }
    
    if ($rootEnv -match "RAZORPAY_KEY_SECRET=.+") {
        Write-Host "  ✓ Root RAZORPAY_KEY_SECRET configured" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Root RAZORPAY_KEY_SECRET not configured" -ForegroundColor Red
        $allGood = $false
    }
} else {
    Write-Host "  ✗ Root .env file not found" -ForegroundColor Red
    $allGood = $false
}

Write-Host ""

# Check frontend .env
Write-Host "Checking frontend/.env file..." -ForegroundColor Yellow
if (Test-Path "frontend/.env") {
    $frontendEnv = Get-Content "frontend/.env" -Raw
    if ($frontendEnv -match "VITE_RAZORPAY_KEY_ID=rzp_") {
        Write-Host "  ✓ Frontend VITE_RAZORPAY_KEY_ID configured" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Frontend VITE_RAZORPAY_KEY_ID not configured" -ForegroundColor Red
        $allGood = $false
    }
} else {
    Write-Host "  ✗ frontend/.env file not found" -ForegroundColor Red
    $allGood = $false
}

Write-Host ""

# Check backend application files
Write-Host "Checking backend configuration..." -ForegroundColor Yellow
if (Test-Path "backend/src/main/resources/application-dev.yml") {
    Write-Host "  ✓ application-dev.yml exists" -ForegroundColor Green
} else {
    Write-Host "  ✗ application-dev.yml not found" -ForegroundColor Red
    $allGood = $false
}

if (Test-Path "backend/src/main/resources/application-docker.yml") {
    Write-Host "  ✓ application-docker.yml exists" -ForegroundColor Green
} else {
    Write-Host "  ✗ application-docker.yml not found" -ForegroundColor Red
    $allGood = $false
}

Write-Host ""

# Check docker-compose
Write-Host "Checking docker-compose.yml..." -ForegroundColor Yellow
if (Test-Path "docker-compose.yml") {
    $dockerCompose = Get-Content "docker-compose.yml" -Raw
    if ($dockerCompose -match "RAZORPAY_KEY_ID") {
        Write-Host "  ✓ RAZORPAY_KEY_ID variable defined" -ForegroundColor Green
    } else {
        Write-Host "  ✗ RAZORPAY_KEY_ID not in docker-compose.yml" -ForegroundColor Red
        $allGood = $false
    }
    
    if ($dockerCompose -match "RAZORPAY_KEY_SECRET") {
        Write-Host "  ✓ RAZORPAY_KEY_SECRET variable defined" -ForegroundColor Green
    } else {
        Write-Host "  ✗ RAZORPAY_KEY_SECRET not in docker-compose.yml" -ForegroundColor Red
        $allGood = $false
    }
} else {
    Write-Host "  ✗ docker-compose.yml not found" -ForegroundColor Red
    $allGood = $false
}

Write-Host ""
Write-Host "===================================" -ForegroundColor Cyan

if ($allGood) {
    Write-Host "All configuration checks passed! ✓" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now:" -ForegroundColor Cyan
    Write-Host "  1. Build: docker-compose build" -ForegroundColor White
    Write-Host "  2. Start: docker-compose up -d" -ForegroundColor White
    Write-Host "  3. Test: Visit http://localhost:3000" -ForegroundColor White
} else {
    Write-Host "Configuration incomplete! ✗" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please run: .\setup-razorpay.ps1" -ForegroundColor Yellow
    Write-Host "Or manually add your Razorpay keys to:" -ForegroundColor Yellow
    Write-Host "  - .env" -ForegroundColor White
    Write-Host "  - frontend/.env" -ForegroundColor White
}

Write-Host ""
