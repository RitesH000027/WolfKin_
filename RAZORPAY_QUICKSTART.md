# Quick Razorpay Setup Guide

## 🚀 Quick Start (5 Minutes)

### Option 1: Automated Setup (Recommended)

Run the setup script:
```powershell
.\setup-razorpay.ps1
```

This will:
- Guide you through getting API keys
- Automatically configure all files
- Optionally rebuild and restart containers

### Option 2: Manual Setup

1. **Get Razorpay Keys**
   - Visit: https://dashboard.razorpay.com/app/keys
   - Sign up / Log in
   - Generate Test API Keys
   - Copy your `Key ID` and `Key Secret`

2. **Configure Root `.env`**
   ```env
   RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
   RAZORPAY_KEY_SECRET=your_secret_key_here
   ```

3. **Configure Frontend `frontend/.env`**
   ```env
   VITE_RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
   ```

4. **Rebuild and Restart**
   ```powershell
   docker-compose build
   docker-compose up -d
   ```

## ✅ Verify Configuration

Run the check script:
```powershell
.\check-razorpay.ps1
```

## 🧪 Testing

### Test Cards (Test Mode Only)
- **Success**: `4111 1111 1111 1111`
- **Failure**: `4000 0000 0000 0002`
- **CVV**: Any 3 digits
- **Expiry**: Any future date
- **OTP**: `123456`

### Test UPI
- **Success**: `success@razorpay`
- **Failure**: `failure@razorpay`

### Test Coupons
- **WELCOME10**: 10% off on orders above ₹500
- **SAVE500**: Flat ₹500 off on orders above ₹2000
- **MEGA20**: 20% off on orders above ₹3000

## 📁 Files Modified

- ✅ `/.env` - Root environment variables
- ✅ `/frontend/.env` - Frontend environment variables
- ✅ `/docker-compose.yml` - Docker service configuration
- ✅ `/backend/src/main/resources/application-docker.yml` - Backend config
- ✅ `/backend/src/main/resources/application-dev.yml` - Dev config

## 🔧 Troubleshooting

**Issue: Keys not working**
- Make sure you copied the full key (no spaces)
- Use Test keys (starting with `rzp_test_`) for development
- Check you're using Key ID in frontend and both in backend

**Issue: Payment modal not opening**
- Check browser console for errors
- Verify Razorpay script is loading (check Network tab)
- Ensure VITE_RAZORPAY_KEY_ID is set in frontend/.env

**Issue: "Invalid signature" error**
- Verify RAZORPAY_KEY_SECRET is correct in backend
- Make sure you rebuilt the backend after adding keys
- Check backend logs: `docker logs ecom-backend`

## 📚 Full Documentation

See [RAZORPAY_SETUP.md](./RAZORPAY_SETUP.md) for:
- Detailed setup instructions
- Security best practices
- Production deployment checklist
- Webhook configuration
- Advanced configuration options

## 🎯 Next Steps

After configuration:

1. **Test the Flow**
   - Add items to cart
   - Proceed to checkout
   - Enter shipping details
   - Apply a coupon
   - Select payment method
   - Complete test payment

2. **Check Razorpay Dashboard**
   - View test transactions
   - Check payment logs
   - Verify webhook calls (if configured)

3. **For Production**
   - Complete KYC verification
   - Generate Live API keys
   - Update keys in production environment
   - Test thoroughly before going live

## 🆘 Support

- **Razorpay Docs**: https://razorpay.com/docs/
- **Razorpay Support**: https://razorpay.com/support/
- **Test Cards**: https://razorpay.com/docs/payment-gateway/test-card-details/

## 🔒 Security Reminders

- ✅ Never commit `.env` files to Git
- ✅ Use environment variables for sensitive data
- ✅ Rotate keys periodically
- ✅ Use Test keys for development only
- ✅ Enable 2FA on Razorpay account
