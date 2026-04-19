<script setup lang="ts">
import { Edit3, Trash2 } from 'lucide-vue-next'
import type { Product } from '../types/catalog'

defineProps<{
  products: Product[]
  loading: boolean
}>()

const emit = defineEmits<{
  edit: [product: Product]
  delete: [product: Product]
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
      <p class="eyebrow">Catálogo</p>
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
          <th>Preço</th>
          <th>Atributos</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.id }}</td>
          <td>
            <strong>{{ product.name }}</strong>
            <span class="table-description">{{ product.description || 'Sem descrição informada.' }}</span>
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
            <div class="table-actions">
              <button type="button" class="icon-button" title="Editar" @click="emit('edit', product)">
                <Edit3 :size="17" />
              </button>
              <button type="button" class="icon-button danger-icon" title="Excluir" @click="emit('delete', product)">
                <Trash2 :size="17" />
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
