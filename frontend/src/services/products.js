import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { productsApi } from './api'
import toast from 'react-hot-toast'

// Query keys
export const PRODUCT_QUERY_KEYS = {
  all: ['products'],
  list: (params) => ['products', 'list', params],
  detail: (id) => ['products', 'detail', id],
  slug: (slug) => ['products', 'slug', slug],
}

// Get all products with filters
export function useProducts(params = {}) {
  return useQuery({
    queryKey: PRODUCT_QUERY_KEYS.list(params),
    queryFn: () => productsApi.getAll(params),
    keepPreviousData: true,
    retry: 3,
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

// Get single product by ID
export function useProduct(id) {
  return useQuery({
    queryKey: PRODUCT_QUERY_KEYS.detail(id),
    queryFn: () => productsApi.getById(id),
    enabled: !!id,
  })
}

// Get single product by slug
export function useProductBySlug(slug) {
  return useQuery({
    queryKey: PRODUCT_QUERY_KEYS.slug(slug),
    queryFn: () => productsApi.getBySlug(slug),
    enabled: !!slug,
  })
}

// Create product mutation (Admin)
export function useCreateProduct() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: productsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: PRODUCT_QUERY_KEYS.all })
      toast.success('Product created successfully')
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to create product')
    },
  })
}

// Update product mutation (Admin)
export function useUpdateProduct() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, ...data }) => productsApi.update(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: PRODUCT_QUERY_KEYS.all })
      queryClient.invalidateQueries({ queryKey: PRODUCT_QUERY_KEYS.detail(variables.id) })
      toast.success('Product updated successfully')
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to update product')
    },
  })
}

// Delete product mutation (Admin)
export function useDeleteProduct() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: productsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: PRODUCT_QUERY_KEYS.all })
      toast.success('Product deleted successfully')
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to delete product')
    },
  })
}