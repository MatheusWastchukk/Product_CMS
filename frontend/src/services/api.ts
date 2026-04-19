import axios from 'axios'
import type { Category, CategoryAttribute, CategoryAttributePayload, Product, ProductPayload } from '../types/catalog'

const api = axios.create({
  baseURL: 'http://localhost:8080'
})

export async function getCategories(): Promise<Category[]> {
  const { data } = await api.get<Category[]>('/categories')
  return data
}

export async function createCategory(name: string): Promise<Category> {
  const { data } = await api.post<Category>('/categories', { name })
  return data
}

export async function getCategoryAttributes(categoryId: number): Promise<CategoryAttribute[]> {
  const { data } = await api.get<CategoryAttribute[]>(`/categories/${categoryId}/attributes`)
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

export async function getProducts(categoryId?: number | null): Promise<Product[]> {
  const { data } = await api.get<Product[]>('/products', {
    params: categoryId ? { categoryId } : undefined
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
