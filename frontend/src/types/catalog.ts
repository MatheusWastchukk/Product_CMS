export interface Category {
  id: number
  name: string
}

export interface CategoryAttribute {
  id: number
  name: string
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
