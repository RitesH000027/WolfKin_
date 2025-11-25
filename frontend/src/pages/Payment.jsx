import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useCart } from '../contexts/CartContext'
import { ordersApi } from '../services/api'
import toast from 'react-hot-toast'
import { CreditCard, Smartphone, Building2, Wallet, Banknote, Tag, CheckCircle } from 'lucide-react'
import axios from 'axios'

export default function Payment() {
  const { items, total, clearCart } = useCart()
  const navigate = useNavigate()
  const location = useLocation()
  const shippingData = location.state?.shippingData

  const [loading, setLoading] = useState(false)
  const [orderComplete, setOrderComplete] = useState(false)
  const [orderId, setOrderId] = useState(null)
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState('CREDIT_CARD')
  const [couponCode, setCouponCode] = useState('')
  const [appliedCoupon, setAppliedCoupon] = useState(null)
  const [couponLoading, setCouponLoading] = useState(false)
  const [discount, setDiscount] = useState(0)

  useEffect(() => {
    if (!shippingData || items.length === 0) {
      navigate('/cart')
    }
  }, [shippingData, items, navigate])

  const paymentMethods = [
    { id: 'CREDIT_CARD', name: 'Credit Card', icon: CreditCard },
    { id: 'DEBIT_CARD', name: 'Debit Card', icon: CreditCard },
    { id: 'UPI', name: 'UPI', icon: Smartphone },
    { id: 'NET_BANKING', name: 'Net Banking', icon: Building2 },
    { id: 'WALLET', name: 'Wallet', icon: Wallet },
    { id: 'COD', name: 'Cash on Delivery', icon: Banknote }
  ]

  const handleApplyCoupon = async () => {
    if (!couponCode.trim()) {
      toast.error('Please enter a coupon code')
      return
    }

    setCouponLoading(true)
    try {
      const response = await axios.post(
        'http://localhost:8080/api/coupons/validate',
        {
          couponCode: couponCode.toUpperCase(),
          orderAmountCents: Math.round(total * 100)
        },
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      if (response.data.valid) {
        setAppliedCoupon(response.data.coupon)
        setDiscount(response.data.discountCents / 100)
        toast.success(response.data.message)
      } else {
        toast.error(response.data.message)
        setAppliedCoupon(null)
        setDiscount(0)
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to apply coupon')
      setAppliedCoupon(null)
      setDiscount(0)
    } finally {
      setCouponLoading(false)
    }
  }

  const handleRemoveCoupon = () => {
    setCouponCode('')
    setAppliedCoupon(null)
    setDiscount(0)
  }

  const loadRazorpay = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script')
      script.src = 'https://checkout.razorpay.com/v1/checkout.js'
      script.onload = () => resolve(true)
      script.onerror = () => resolve(false)
      document.body.appendChild(script)
    })
  }

  const handlePayment = async () => {
    setLoading(true)

    try {
      const orderData = {
        items: items.map(item => ({
          productId: item.id,
          quantity: item.quantity
        })),
        shippingAddress: shippingData,
        couponCode: appliedCoupon?.code || null,
        paymentMethod: selectedPaymentMethod
      }

      const response = await axios.post(
        'http://localhost:8080/api/payments/create-order',
        orderData,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      const paymentData = response.data

      if (selectedPaymentMethod === 'COD') {
        // For COD, confirm directly
        const confirmResponse = await axios.post(
          `http://localhost:8080/api/payments/confirm-cod/${paymentData.orderId}`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        )
        
        setOrderId(confirmResponse.data.id)
        setOrderComplete(true)
        clearCart()
        toast.success('Order placed successfully!')
      } else {
        // Load Razorpay
        const res = await loadRazorpay()

        if (!res) {
          toast.error('Razorpay SDK failed to load')
          return
        }

        const options = {
          key: paymentData.razorpayKey,
          amount: paymentData.amountCents,
          currency: paymentData.currency,
          name: 'WolfKin',
          description: 'Order Payment',
          order_id: paymentData.razorpayOrderId,
          handler: async function (response) {
            try {
              const verifyResponse = await axios.post(
                'http://localhost:8080/api/payments/verify',
                {
                  razorpayOrderId: response.razorpay_order_id,
                  razorpayPaymentId: response.razorpay_payment_id,
                  razorpaySignature: response.razorpay_signature
                },
                {
                  headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                  }
                }
              )

              setOrderId(verifyResponse.data.id)
              setOrderComplete(true)
              clearCart()
              toast.success('Payment successful!')
            } catch (error) {
              toast.error('Payment verification failed')
            }
          },
          prefill: {
            name: paymentData.customerName,
            email: paymentData.customerEmail,
            contact: paymentData.customerPhone
          },
          theme: {
            color: '#f59e0b'
          }
        }

        const paymentObject = new window.Razorpay(options)
        paymentObject.open()
      }
    } catch (error) {
      console.error('Payment failed:', error)
      toast.error(error.response?.data?.message || 'Failed to process payment')
    } finally {
      setLoading(false)
    }
  }

  if (orderComplete) {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center">
          <CheckCircle className="w-20 h-20 text-green-500 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Confirmed!</h1>
          <p className="text-gray-600 mb-4">
            Thank you for your order. Order #{orderId}
          </p>
          <p className="text-gray-600 mb-8">
            We've sent a confirmation email with your order details.
          </p>
          <div className="flex gap-4 justify-center">
            <button
              onClick={() => navigate('/profile')}
              className="btn-primary"
            >
              View Orders
            </button>
            <button
              onClick={() => navigate('/products')}
              className="btn-secondary"
            >
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    )
  }

  const finalTotal = total - discount

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Payment</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Payment Methods & Coupon */}
        <div className="lg:col-span-2 space-y-6">
          {/* Coupon Section */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex items-center mb-4">
              <Tag className="w-5 h-5 text-primary-500 mr-2" />
              <h2 className="text-xl font-bold text-gray-900">Apply Coupon</h2>
            </div>

            {appliedCoupon ? (
              <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-bold text-green-800">{appliedCoupon.code}</p>
                    <p className="text-sm text-green-600">{appliedCoupon.description}</p>
                    <p className="text-sm text-green-600 mt-1">You saved ₹{discount.toFixed(2)}</p>
                  </div>
                  <button
                    onClick={handleRemoveCoupon}
                    className="text-red-600 hover:text-red-700 text-sm font-medium"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex gap-2">
                <input
                  type="text"
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                  placeholder="Enter coupon code"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
                <button
                  onClick={handleApplyCoupon}
                  disabled={couponLoading}
                  className="btn-primary"
                >
                  {couponLoading ? 'Applying...' : 'Apply'}
                </button>
              </div>
            )}

            <div className="mt-4 text-sm text-gray-600">
              <p className="font-medium mb-2">Available Coupons:</p>
              <ul className="space-y-1">
                <li>• WELCOME10 - 10% off on orders above ₹500</li>
                <li>• SAVE500 - Flat ₹500 off on orders above ₹2000</li>
                <li>• MEGA20 - 20% off on orders above ₹3000</li>
              </ul>
            </div>
          </div>

          {/* Payment Methods */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-6">Select Payment Method</h2>

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
              {paymentMethods.map((method) => {
                const Icon = method.icon
                return (
                  <button
                    key={method.id}
                    onClick={() => setSelectedPaymentMethod(method.id)}
                    className={`p-4 border-2 rounded-lg transition-all ${
                      selectedPaymentMethod === method.id
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <Icon className={`w-8 h-8 mx-auto mb-2 ${
                      selectedPaymentMethod === method.id ? 'text-primary-600' : 'text-gray-600'
                    }`} />
                    <p className={`text-sm font-medium text-center ${
                      selectedPaymentMethod === method.id ? 'text-primary-600' : 'text-gray-700'
                    }`}>
                      {method.name}
                    </p>
                  </button>
                )
              })}
            </div>
          </div>
        </div>

        {/* Order Summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 sticky top-4">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Order Summary</h2>

            <div className="space-y-3 mb-6">
              {items.map((item) => (
                <div key={item.id} className="flex justify-between text-sm">
                  <span className="text-gray-600">
                    {item.name} x {item.quantity}
                  </span>
                  <span className="font-medium">₹{(item.price * item.quantity).toFixed(2)}</span>
                </div>
              ))}

              <div className="border-t border-gray-200 pt-3">
                <div className="flex justify-between text-gray-600 mb-2">
                  <span>Subtotal</span>
                  <span>₹{total.toFixed(2)}</span>
                </div>
                {discount > 0 && (
                  <div className="flex justify-between text-green-600 mb-2">
                    <span>Discount</span>
                    <span>-₹{discount.toFixed(2)}</span>
                  </div>
                )}
                <div className="flex justify-between text-gray-600 mb-2">
                  <span>Shipping</span>
                  <span className="text-green-600">FREE</span>
                </div>
                <div className="flex justify-between text-lg font-bold text-gray-900 mt-3">
                  <span>Total</span>
                  <span>₹{finalTotal.toFixed(2)}</span>
                </div>
              </div>
            </div>

            <button
              onClick={handlePayment}
              disabled={loading}
              className="btn-primary w-full"
            >
              {loading ? 'Processing...' : selectedPaymentMethod === 'COD' ? 'Place Order' : 'Proceed to Pay'}
            </button>

            <p className="text-xs text-gray-500 text-center mt-4">
              By placing your order, you agree to our Terms & Conditions
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
