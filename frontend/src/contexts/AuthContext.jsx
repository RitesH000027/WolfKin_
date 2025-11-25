import { createContext, useContext, useReducer, useEffect } from 'react'
import { authApi } from '../services/api'
import toast from 'react-hot-toast'

const AuthContext = createContext(null)

const initialState = {
  user: null,
  token: localStorage.getItem('token'),
  isLoading: false,
  isAuthenticated: false,
}

function authReducer(state, action) {
  switch (action.type) {
    case 'LOGIN_START':
    case 'REGISTER_START':
      return { ...state, isLoading: true }
    
    case 'LOGIN_SUCCESS':
    case 'REGISTER_SUCCESS':
      localStorage.setItem('token', action.payload.token)
      return {
        ...state,
        isLoading: false,
        isAuthenticated: true,
        user: action.payload.user,
        token: action.payload.token,
      }
    
    case 'LOGIN_ERROR':
    case 'REGISTER_ERROR':
      localStorage.removeItem('token')
      return {
        ...state,
        isLoading: false,
        isAuthenticated: false,
        user: null,
        token: null,
      }
    
    case 'LOGOUT':
      localStorage.removeItem('token')
      return {
        ...state,
        isAuthenticated: false,
        user: null,
        token: null,
      }
    
    case 'SET_USER':
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
      }
    
    case 'SET_LOADING':
      return {
        ...state,
        isLoading: action.payload,
      }
    
    default:
      return state
  }
}

export function AuthProvider({ children }) {
  const [state, dispatch] = useReducer(authReducer, initialState)

  // Check if user is logged in on app start
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      dispatch({ type: 'SET_LOADING', payload: true })
      authApi.getCurrentUser()
        .then(user => {
          dispatch({ type: 'SET_USER', payload: user })
        })
        .catch(() => {
          localStorage.removeItem('token')
        })
        .finally(() => {
          dispatch({ type: 'SET_LOADING', payload: false })
        })
    }
  }, [])

  const login = async (credentials) => {
    try {
      dispatch({ type: 'LOGIN_START' })
      const response = await authApi.login(credentials)
      dispatch({ type: 'LOGIN_SUCCESS', payload: response })
      toast.success(`Welcome back, ${response.user.name}!`)
      return response
    } catch (error) {
      dispatch({ type: 'LOGIN_ERROR' })
      toast.error(error.message || 'Login failed')
      throw error
    }
  }

  const register = async (userData) => {
    try {
      dispatch({ type: 'REGISTER_START' })
      const response = await authApi.register(userData)
      dispatch({ type: 'REGISTER_SUCCESS', payload: response })
      toast.success(`Welcome to our store, ${response.user.name}!`)
      return response
    } catch (error) {
      dispatch({ type: 'REGISTER_ERROR' })
      toast.error(error.message || 'Registration failed')
      throw error
    }
  }

  const logout = () => {
    dispatch({ type: 'LOGOUT' })
    toast.success('Logged out successfully')
  }

  const value = {
    ...state,
    login,
    register,
    logout,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}