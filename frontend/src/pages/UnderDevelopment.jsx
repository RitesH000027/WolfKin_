import { useNavigate } from 'react-router-dom'
import { Construction } from 'lucide-react'

export default function UnderDevelopment() {
  const navigate = useNavigate()

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="text-center">
        <Construction className="w-24 h-24 text-primary-500 mx-auto mb-6" />
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Under Development
        </h1>
        <p className="text-lg text-gray-600 mb-8 max-w-md mx-auto">
          We're working hard to bring you this feature. Check back soon!
        </p>
        <div className="flex gap-4 justify-center">
          <button
            onClick={() => navigate('/')}
            className="btn-primary"
          >
            Go Home
          </button>
          <button
            onClick={() => navigate(-1)}
            className="btn-secondary"
          >
            Go Back
          </button>
        </div>
      </div>
    </div>
  )
}
