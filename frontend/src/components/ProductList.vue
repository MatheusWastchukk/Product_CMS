<script setup lang="ts">
import type { Product } from '../types/catalog'

defineProps<{
  products: Product[]
  loading: boolean
}>()

const emit = defineEmits<{
  edit: [product: Product]
}>()

function formatPrice(price: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(price)
}
</script>

<template>
  <section class="panel list-panel" aria-labelledby="product-list-title">
    <div class="section-heading">
      <p class="eyebrow">Catalogo</p>
      <h2 id="product-list-title">Produtos cadastrados</h2>
      <p>Lista atualizada a partir da API Spring Boot.</p>
    </div>

    <p v-if="loading" class="empty-note">Carregando produtos...</p>
    <p v-else-if="products.length === 0" class="empty-note">
      Nenhum produto cadastrado ainda.
    </p>

    <div v-else class="product-list">
      <article v-for="product in products" :key="product.id" class="product-card">
        <div>
          <p class="product-category">{{ product.categoryName }}</p>
          <h3>{{ product.name }}</h3>
          <p>{{ product.description || 'Sem descricao informada.' }}</p>
        </div>

        <strong>{{ formatPrice(product.price) }}</strong>

        <dl v-if="product.attributes.length > 0" class="attribute-list">
          <template v-for="attribute in product.attributes" :key="attribute.name">
            <dt>{{ attribute.name }}</dt>
            <dd>{{ attribute.value }}</dd>
          </template>
        </dl>

        <div class="card-actions">
          <button type="button" class="secondary-button" @click="emit('edit', product)">Editar</button>
        </div>
      </article>
    </div>
  </section>
</template>
