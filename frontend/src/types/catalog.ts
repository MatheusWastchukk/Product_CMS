export interface Category {
  id: number
  name: string
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
