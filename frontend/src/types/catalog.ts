export interface Category {
  id: number
  name: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface CategoryAttribute {
  id: number
  name: string
  type: AttributeType
}

export type AttributeType = 'string' | 'int' | 'decimal' | 'boolean' | 'date'

export interface CategoryAttributePayload {
  name: string
  type: AttributeType
}

export interface ProductAttribute {
  name: string
  value: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  categoryId: number
  categoryName: string
  attributes: ProductAttribute[]
}

export interface ProductPayload {
  name: string
  description: string
  price: number
  categoryId: number | null
  attributes: ProductAttribute[]
}

export interface AppUser {
  id: number
  name: string
  username: string
  email: string | null
  role: string
}

export interface AuthResponse {
  token: string
  user: AppUser
}

export interface UserPayload {
  name: string
  username: string
  email: string
}

export interface UserUpdatePayload extends UserPayload {
  resetPassword: boolean
}
