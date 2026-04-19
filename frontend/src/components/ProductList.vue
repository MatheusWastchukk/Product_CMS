<script setup lang="ts">
import { Edit3 } from 'lucide-vue-next'
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

    <table v-else class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>Categoria</th>
          <th>Preco</th>
          <th>Atributos</th>
          <th>Acoes</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.id }}</td>
          <td>
            <strong>{{ product.name }}</strong>
            <span class="table-description">{{ product.description || 'Sem descricao informada.' }}</span>
          </td>
          <td>{{ product.categoryName }}</td>
          <td>{{ formatPrice(product.price) }}</td>
          <td>
            <div v-if="product.attributes.length > 0" class="attribute-chip-list">
              <span v-for="attribute in product.attributes" :key="attribute.name" class="attribute-chip">
                <strong>{{ attribute.name }}</strong>
                {{ attribute.value }}
              </span>
            </div>
            <span v-else class="empty-note">Sem atributos</span>
          </td>
          <td>
            <button type="button" class="icon-button" title="Editar" @click="emit('edit', product)">
              <Edit3 :size="17" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
