import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, Package, Truck, CheckCircle, XCircle, Clock } from 'lucide-react'
import api from '../services/api'
import toast from 'react-hot-toast'

export default function OrderDetail() {
  const { orderId } = useParams()
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchOrder()
  }, [orderId])

  const fetchOrder = async () => {
    try {
      setLoading(true)
      const response = await api.get(`/orders/${orderId}`)
      setOrder(response.data)
    } catch (error) {
      console.error('Error fetching order:', error)
      toast.error('Order not found')
      navigate('/profile')
    } finally {
      setLoading(false)
    }
  }

  const getStatusIcon = (status) => {
    const icons = {
      PENDING_PAYMENT: Clock,
      PAID: CheckCircle,
      PROCESSING: Package,
      SHIPPED: Truck,
      DELIVERED: CheckCircle,
      CANCELLED: XCircle,
      REFUNDED: XCircle
    }
    return icons[status] || Clock
  }

  const getStatusColor = (status) => {
    const colors = {
      PENDING_PAYMENT: 'text-yellow-600 bg-yellow-50',
      PAID: 'text-blue-600 bg-blue-50',
      PROCESSING: 'text-purple-600 bg-purple-50',
      SHIPPED: 'text-indigo-600 bg-indigo-50',
      DELIVERED: 'text-green-600 bg-green-50',
      CANCELLED: 'text-red-600 bg-red-50',
      REFUNDED: 'text-gray-600 bg-gray-50'
    }
    return colors[status] || 'text-gray-600 bg-gray-50'
  }

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const parseShippingAddress = (addressJson) => {
    try {
      return JSON.parse(addressJson)
    } catch {
      return {}
    }
  }

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-1/4 mb-8"></div>
          <div className="bg-white shadow rounded-lg p-6">
            <div className="h-6 bg-gray-200 rounded w-1/2 mb-4"></div>
            <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
            <div className="h-4 bg-gray-200 rounded w-2/3"></div>
          </div>
        </div>
      </div>
    )
  }

  if (!order) {
    return null
  }

  const StatusIcon = getStatusIcon(order.status)
  const shippingAddress = parseShippingAddress(order.shippingAddress)

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link
        to="/profile"
        className="inline-flex items-center text-primary-600 hover:text-primary-700 mb-6"
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        Back to Profile
      </Link>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        {/* Order Header */}
        <div className="px-6 py-6 border-b border-gray-200">
          <div className="flex items-start justify-between">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Order #{order.id}</h1>
              <p className="text-sm text-gray-500 mt-1">
                Placed on {formatDate(order.createdAt)}
              </p>
            </div>
            <div className={`flex items-center px-4 py-2 rounded-full ${getStatusColor(order.status)}`}>
              <StatusIcon className="w-5 h-5 mr-2" />
              <span className="font-medium">{order.status.replace('_', ' ')}</span>
            </div>
          </div>
        </div>

        {/* Order Items */}
        <div className="px-6 py-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Order Items</h2>
          <div className="space-y-4">
            {order.items?.map((item) => (
              <div key={item.id} className="flex items-center space-x-4">
                <div className="flex-shrink-0 w-20 h-20 bg-gray-200 rounded-md overflow-hidden">
                  {item.product?.imageUrl ? (
                    <img
                      src={item.product.imageUrl}
                      alt={item.product.name}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-400">
                      <Package className="w-8 h-8" />
                    </div>
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {item.product?.name || 'Product'}
                  </p>
                  <p className="text-sm text-gray-500">
                    Quantity: {item.quantity}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">
                    ${item.price?.toFixed(2)}
                  </p>
                  <p className="text-xs text-gray-500">
                    each
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-bold text-gray-900">
                    ${(item.price * item.quantity).toFixed(2)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Shipping Address */}
        <div className="px-6 py-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Shipping Address</h2>
          <div className="text-sm text-gray-700">
            {shippingAddress.fullName && (
              <p className="font-medium">{shippingAddress.fullName}</p>
            )}
            {shippingAddress.address && <p>{shippingAddress.address}</p>}
            {shippingAddress.phone && (
              <p className="mt-2">Phone: {shippingAddress.phone}</p>
            )}
            {shippingAddress.email && (
              <p>Email: {shippingAddress.email}</p>
            )}
          </div>
        </div>

        {/* Order Summary */}
        <div className="px-6 py-6 bg-gray-50">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Order Summary</h2>
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Subtotal</span>
              <span className="text-gray-900">${order.total?.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Shipping</span>
              <span className="text-gray-900">Free</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Tax</span>
              <span className="text-gray-900">$0.00</span>
            </div>
            <div className="border-t border-gray-200 pt-2 mt-2">
              <div className="flex justify-between">
                <span className="text-base font-semibold text-gray-900">Total</span>
                <span className="text-lg font-bold text-primary-600">
                  ${order.total?.toFixed(2)}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Actions */}
      {order.status === 'PENDING_PAYMENT' && (
        <div className="mt-6 text-center">
          <button className="btn-primary">
            Complete Payment
          </button>
        </div>
      )}
    </div>
  )
}
