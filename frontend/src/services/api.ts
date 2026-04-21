import axios from 'axios'
import type {
  AppUser,
  AuthResponse,
  Category,
  CategoryAttribute,
  CategoryAttributePayload,
  PageResponse,
  Product,
  ProductPayload,
  UserPayload,
  UserUpdatePayload
} from '../types/catalog'

export interface PageRequest {
  page?: number
  size?: number
}

const TOKEN_KEY = 'productCmsToken'
const USER_KEY = 'productCmsUser'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
})

api.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getStoredUser(): AppUser | null {
  const rawUser = localStorage.getItem(USER_KEY)
  if (!rawUser) {
    return null
  }

  try {
    return JSON.parse(rawUser)
  } catch {
    clearSession()
    return null
  }
}

export function storeSession(auth: AuthResponse) {
  localStorage.setItem(TOKEN_KEY, auth.token)
  localStorage.setItem(USER_KEY, JSON.stringify(auth.user))
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export async function login(username: string, password: string): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/login', { username, password })
  storeSession(data)
  return data
}

export async function logout(): Promise<void> {
  await api.post('/auth/logout')
  clearSession()
}

export async function updateProfile(payload: { name: string; password: string }): Promise<AppUser> {
  const { data } = await api.put<AppUser>('/auth/me', payload)
  localStorage.setItem(USER_KEY, JSON.stringify(data))
  return data
}

export async function getCategories(params: PageRequest = {}): Promise<PageResponse<Category>> {
  const { data } = await api.get<PageResponse<Category>>('/categories', { params })
  return data
}

export async function createCategory(name: string): Promise<Category> {
  const { data } = await api.post<Category>('/categories', { name })
  return data
}

export async function getCategoryAttributes(
  categoryId: number,
  params: PageRequest = { size: 100 }
): Promise<PageResponse<CategoryAttribute>> {
  const { data } = await api.get<PageResponse<CategoryAttribute>>(`/categories/${categoryId}/attributes`, { params })
  return data
}

export async function addCategoryAttributes(
  categoryId: number,
  attributes: CategoryAttributePayload[]
): Promise<CategoryAttribute[]> {
  const { data } = await api.post<CategoryAttribute[]>(`/categories/${categoryId}/attributes`, {
    attributes
  })
  return data
}

export async function deleteCategory(categoryId: number): Promise<void> {
  await api.delete(`/categories/${categoryId}`)
}

export async function getProducts(params: PageRequest & { categoryId?: number | null; name?: string } = {}): Promise<PageResponse<Product>> {
  const { data } = await api.get<PageResponse<Product>>('/products', {
    params: {
      ...params,
      categoryId: params.categoryId || undefined,
      name: params.name || undefined
    }
  })
  return data
}

export async function createProduct(payload: ProductPayload): Promise<Product> {
  const { data } = await api.post<Product>('/products', payload)
  return data
}

export async function updateProduct(productId: number, payload: ProductPayload): Promise<Product> {
  const { data } = await api.put<Product>(`/products/${productId}`, payload)
  return data
}

export async function deleteProduct(productId: number): Promise<void> {
  await api.delete(`/products/${productId}`)
}

export async function getUsers(params: PageRequest = {}): Promise<PageResponse<AppUser>> {
  const { data } = await api.get<PageResponse<AppUser>>('/users', { params })
  return data
}

export async function createUser(payload: UserPayload): Promise<AppUser> {
  const { data } = await api.post<AppUser>('/users', payload)
  return data
}

export async function updateUser(userId: number, payload: UserUpdatePayload): Promise<AppUser> {
  const { data } = await api.put<AppUser>(`/users/${userId}`, payload)
  return data
}

export async function deleteUser(userId: number): Promise<void> {
  await api.delete(`/users/${userId}`)
}
