# Razorpay Setup Guide

## Prerequisites
1. Sign up for Razorpay account at https://razorpay.com/
2. Complete KYC verification (required for production)
3. Get your API keys from Razorpay Dashboard

## Razorpay Account Setup

### Step 1: Create Razorpay Account
1. Go to https://razorpay.com/
2. Click "Sign Up" and create your account
3. Verify your email address

### Step 2: Generate API Keys
1. Login to Razorpay Dashboard: https://dashboard.razorpay.com/
2. Go to Settings → API Keys
3. Generate Test Keys (for development)
4. Generate Live Keys (for production - after KYC)

### Step 3: Configure Payment Methods
1. Go to Settings → Configuration
2. Enable payment methods you want to support:
   - Credit/Debit Cards
   - UPI
   - Net Banking
   - Wallets
   - EMI options (optional)

### Step 4: Setup Webhooks (Optional but Recommended)
1. Go to Settings → Webhooks
2. Add webhook URL: `https://yourdomain.com/api/webhooks/razorpay`
3. Select events to subscribe:
   - payment.authorized
   - payment.captured
   - payment.failed
   - order.paid
   - refund.created

### Step 5: Configure Backend

Update `backend/src/main/resources/application-docker.yml`:

```yaml
razorpay:
  key:
    id: ${RAZORPAY_KEY_ID:your_test_key_id}
    secret: ${RAZORPAY_KEY_SECRET:your_test_key_secret}
```

Or set environment variables in `docker-compose.yml`:

```yaml
backend:
  environment:
    - RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
    - RAZORPAY_KEY_SECRET=your_secret_key_here
```

### Step 6: Configure Frontend

Update `frontend/.env`:

```env
VITE_RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
```

## Testing Payment Integration

### Test Mode
- Use Test API Keys (starts with `rzp_test_`)
- No real money transactions
- Use test card numbers provided by Razorpay

### Test Card Numbers
For testing in Test Mode:
- **Success**: 4111 1111 1111 1111
- **Failure**: 4000 0000 0000 0002
- CVV: Any 3 digits
- Expiry: Any future date
- OTP: 123456 (if required)

### Test UPI IDs
- success@razorpay
- failure@razorpay

## Production Checklist

Before going live:

### 1. Complete KYC
- Submit business documents
- Wait for Razorpay approval
- Get Live API keys

### 2. Security
- ✅ Never expose Key Secret in frontend
- ✅ Always verify payment signature on backend
- ✅ Use HTTPS in production
- ✅ Implement proper error handling
- ✅ Add logging for payment transactions

### 3. Update Configuration
- Replace Test API keys with Live keys
- Update webhook URLs to production domain
- Enable production payment methods
- Set up proper email notifications

### 4. Legal Requirements
- Add Terms & Conditions
- Add Privacy Policy
- Add Refund & Cancellation Policy
- Display company information

### 5. Testing
- Test all payment methods
- Test failure scenarios
- Test webhook handling
- Test refund process
- Load testing

## Important Notes

### Security Best Practices
1. **Never commit API keys to Git**
   - Add `.env` files to `.gitignore`
   - Use environment variables
   - Rotate keys periodically

2. **Backend Verification**
   - Always verify Razorpay signature on backend
   - Don't trust frontend payment status
   - Log all transactions

3. **Error Handling**
   - Handle payment failures gracefully
   - Show user-friendly error messages
   - Implement retry mechanism
   - Send email notifications

### Payment Flow
1. User selects products and proceeds to checkout
2. User enters shipping details
3. User selects payment method and applies coupon (optional)
4. Frontend calls backend `/api/payments/create-order`
5. Backend creates Razorpay order and returns order ID
6. Frontend opens Razorpay checkout modal
7. User completes payment on Razorpay
8. Razorpay returns payment details to frontend
9. Frontend sends details to backend `/api/payments/verify`
10. Backend verifies signature with Razorpay
11. Backend updates order status and reduces inventory
12. Success page shown to user

### COD Flow
1. User selects Cash on Delivery
2. No Razorpay interaction needed
3. Order placed directly
4. Payment collected on delivery

## Current Implementation Status

✅ Backend payment service with Razorpay integration
✅ Frontend payment page with multiple payment methods
✅ Coupon system integrated
✅ COD support
✅ Payment signature verification
✅ Order creation and inventory management
✅ Error handling
✅ Success/failure notifications

⏳ Pending (requires Razorpay account):
- API keys configuration
- Production deployment URL
- Webhook setup
- Email notifications

## Support

For Razorpay integration issues:
- Documentation: https://razorpay.com/docs/
- Support: https://razorpay.com/support/
- Test Mode: https://razorpay.com/docs/payment-gateway/test-card-details/

## Quick Start Commands

```bash
# Backend - Add environment variables
cd backend
# Update application-docker.yml with your keys

# Frontend - Add environment variables
cd frontend
cp .env.example .env
# Update .env with your VITE_RAZORPAY_KEY_ID

# Rebuild containers
docker-compose build
docker-compose up -d

# Check logs
docker-compose logs -f backend
docker-compose logs -f frontend
```

## Troubleshooting

### Issue: "Razorpay SDK failed to load"
- Check internet connection
- Verify Razorpay script URL
- Check browser console for errors

### Issue: "Invalid payment signature"
- Verify Key Secret is correct
- Check signature generation logic
- Ensure data matches between frontend and backend

### Issue: "Payment failed"
- Check Razorpay Dashboard for error details
- Verify test card numbers in test mode
- Check payment method is enabled

### Issue: "Order not found"
- Verify order creation was successful
- Check database for order entry
- Review backend logs
