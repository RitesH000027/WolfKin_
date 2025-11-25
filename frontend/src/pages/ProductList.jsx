import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useProducts } from '../services/products'
import { useCart } from '../contexts/CartContext'
import { Filter, Grid, List, X } from 'lucide-react'
import api from '../services/api'

export default function ProductList() {
  const { addToCart } = useCart()
  const [searchParams] = useSearchParams()
  const searchQuery = searchParams.get('q') || ''
  
  const [filters, setFilters] = useState({
    page: 0,
    size: 20,
    sortBy: 'createdAt',
    sortDir: 'desc',
    q: searchQuery
  })
  const [viewMode, setViewMode] = useState('grid')
  const [showFilters, setShowFilters] = useState(false)
  const [categories, setCategories] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [inStockOnly, setInStockOnly] = useState(false)
  const [sortOption, setSortOption] = useState('newest')
  
  const { data, isLoading, error } = useProducts(filters)

  useEffect(() => {
    fetchCategories()
  }, [])

  useEffect(() => {
    // Update filters when search query changes
    setFilters(prev => ({ ...prev, q: searchQuery, page: 0 }))
  }, [searchQuery])

  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories')
      setCategories(response)
    } catch (error) {
      console.error('Error fetching categories:', error)
      setCategories([])
    }
  }

  const clearFilters = () => {
    setSelectedCategory('')
    setMinPrice('')
    setMaxPrice('')
    setInStockOnly(false)
    setSortOption('newest')
    setFilters({
      page: 0,
      size: 20,
      sortBy: 'createdAt',
      sortDir: 'desc',
      q: searchQuery
    })
  }

  useEffect(() => {
    // Apply sort option to API filters
    let sortBy = 'createdAt'
    let sortDir = 'desc'
    
    switch (sortOption) {
      case 'price-low':
        sortBy = 'price'
        sortDir = 'asc'
        break
      case 'price-high':
        sortBy = 'price'
        sortDir = 'desc'
        break
      case 'name':
        sortBy = 'name'
        sortDir = 'asc'
        break
      case 'newest':
      default:
        sortBy = 'createdAt'
        sortDir = 'desc'
        break
    }
    
    console.log('Sort changed:', { sortOption, sortBy, sortDir })
    setFilters(prev => ({ ...prev, sortBy, sortDir, page: 0 }))
  }, [sortOption])

  const filteredProducts = data?.content?.filter(product => {
    if (selectedCategory && product.category?.id !== parseInt(selectedCategory)) {
      return false
    }
    if (minPrice && product.price < parseFloat(minPrice)) {
      return false
    }
    if (maxPrice && product.price > parseFloat(maxPrice)) {
      return false
    }
    if (inStockOnly && product.stockQuantity <= 0) {
      return false
    }
    return true
  }) || []

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="bg-gray-200 aspect-square rounded-lg mb-4"></div>
              <div className="h-4 bg-gray-200 rounded mb-2"></div>
              <div className="h-4 bg-gray-200 rounded w-2/3"></div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center">
          <p className="text-red-600 mb-4">Error loading products: {error.message}</p>
          <button 
            onClick={() => window.location.reload()}
            className="btn-primary"
          >
            Try Again
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Search Results Header */}
      {searchQuery && (
        <div className="mb-4">
          <Link 
            to="/products" 
            className="inline-flex items-center text-primary-600 hover:text-primary-700"
          >
            ← Back to all products
          </Link>
          <p className="text-gray-600 mt-2">
            Showing results for "{searchQuery}"
          </p>
        </div>
      )}
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Products</h1>
          <p className="text-gray-600 mt-2">
            {filteredProducts.length} of {data?.totalElements || 0} products
          </p>
        </div>
        
        <div className="flex items-center space-x-4 mt-4 sm:mt-0">
          <button 
            type="button"
            onClick={(e) => {
              e.preventDefault()
              setShowFilters(!showFilters)
            }}
            className="btn-outline flex items-center"
          >
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </button>
          
          <div className="flex border border-gray-300 rounded-md">
            <button
              type="button"
              onClick={() => setViewMode('grid')}
              className={`p-2 ${viewMode === 'grid' ? 'bg-primary-600 text-white' : 'text-gray-400'}`}
            >
              <Grid className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={() => setViewMode('list')}
              className={`p-2 ${viewMode === 'list' ? 'bg-primary-600 text-white' : 'text-gray-400'}`}
            >
              <List className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Filters Panel */}
      {showFilters && (
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Filters</h3>
            <button
              onClick={() => setShowFilters(false)}
              className="text-gray-400 hover:text-gray-600"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Category Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Category
              </label>
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="">All Categories</option>
                {categories && categories.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>

            {/* Price Range */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Min Price
              </label>
              <input
                type="number"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                placeholder="$0"
                min="0"
                step="0.01"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Max Price
              </label>
              <input
                type="number"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                placeholder="Any"
                min="0"
                step="0.01"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>

            {/* Sort */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Sort By
              </label>
              <select
                value={sortOption}
                onChange={(e) => setSortOption(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="newest">Newest First</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="name">Name: A to Z</option>
              </select>
            </div>
          </div>

          {/* Stock Filter */}
          <div className="mt-4">
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={inStockOnly}
                onChange={(e) => setInStockOnly(e.target.checked)}
                className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
              />
              <span className="ml-2 text-sm text-gray-700">Show only in-stock items</span>
            </label>
          </div>

          {/* Clear Filters */}
          <div className="mt-4 flex justify-end">
            <button
              onClick={clearFilters}
              className="text-sm text-primary-600 hover:text-primary-700 font-medium"
            >
              Clear All Filters
            </button>
          </div>
        </div>
      )}

      {/* Products Grid */}
      {filteredProducts.length > 0 ? (
        <div className={`grid gap-6 ${
          viewMode === 'grid' 
            ? 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4' 
            : 'grid-cols-1'
        }`}>
          {filteredProducts.map((product) => (
            <div key={product.id} className="group bg-white rounded-lg overflow-hidden hover:shadow-lg transition-all duration-300">
              <Link to={`/products/${product.id}`}>
                <div className="aspect-square bg-gray-100 overflow-hidden">
                  {product.imageUrl ? (
                    <img
                      src={product.imageUrl}
                      alt={product.name}
                      className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-400">
                      No Image
                    </div>
                  )}
                </div>
              </Link>
              <div className="p-4">
                <Link to={`/products/${product.id}`}>
                  <h3 className="font-bold text-gray-900 mb-2 text-lg hover:text-primary-600 transition-colors">{product.name}</h3>
                </Link>
                <p className="text-sm text-gray-500 mb-3 line-clamp-2">{product.description}</p>
                <div className="flex items-center justify-between mb-4">
                  <p className="text-2xl font-extrabold text-gray-900">${product.price}</p>
                </div>
                <button 
                  onClick={() => addToCart(product)}
                  className="w-full bg-primary-500 hover:bg-primary-600 text-white font-bold py-3 px-6 rounded-md uppercase tracking-wider text-sm transition-colors duration-200"
                >
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">No products found</p>
          <button 
            onClick={() => setFilters({ ...filters, page: 0 })}
            className="btn-primary"
          >
            Refresh
          </button>
        </div>
      )}
    </div>
  )
}