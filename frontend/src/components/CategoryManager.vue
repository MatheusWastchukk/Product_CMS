<script setup lang="ts">
import { reactive, ref } from 'vue'
import { addCategoryAttributes, createCategory } from '../services/api'
import type { Category } from '../types/catalog'
import CategoryEditModal from './CategoryEditModal.vue'

defineProps<{
  categories: Category[]
}>()

const emit = defineEmits<{
  changed: []
}>()

const activeCategoryTab = ref<'create' | 'list'>('create')
const editingCategory = ref<Category | null>(null)

const form = reactive({
  name: '',
  attributes: ''
})

const saving = ref(false)
const feedback = ref('')

async function submit() {
  const name = form.name.trim()
  const attributes = form.attributes
    .split(',')
    .map((attribute) => attribute.trim())
    .filter(Boolean)

  if (!name) {
    showError('Informe o nome da categoria.')
    return
  }

  saving.value = true
  feedback.value = ''

  try {
    const category = await createCategory(name)
    if (attributes.length > 0) {
      await addCategoryAttributes(category.id, attributes)
    }

    form.name = ''
    form.attributes = ''
    feedback.value = 'Categoria cadastrada com sucesso.'
    emit('changed')
  } catch {
    showError('Nao foi possivel salvar a categoria. Verifique se ela ja existe.')
  } finally {
    saving.value = false
  }
}

function showError(message: string) {
  alert(message)
}
</script>

<template>
  <section class="category-page" aria-labelledby="category-title">
    <div class="subtabs" aria-label="Abas de categorias">
      <button
        type="button"
        :class="{ active: activeCategoryTab === 'create' }"
        @click="activeCategoryTab = 'create'"
      >
        Cadastro
      </button>
      <button
        type="button"
        :class="{ active: activeCategoryTab === 'list' }"
        @click="activeCategoryTab = 'list'"
      >
        Categorias cadastradas
      </button>
    </div>

    <section v-if="activeCategoryTab === 'create'" class="panel category-panel">
      <div class="section-heading">
        <p class="eyebrow">Categorias</p>
        <h2 id="category-title">Cadastrar categoria</h2>
        <p>Crie categorias e defina os campos dinamicos separados por virgula.</p>
      </div>

      <form class="category-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome da categoria</span>
          <input v-model="form.name" placeholder="Acessorio" />
        </label>

        <label class="field">
          <span>Atributos</span>
          <input v-model="form.attributes" placeholder="modelo, cor, tamanho" />
        </label>

        <button type="submit" :disabled="saving">
          {{ saving ? 'Salvando...' : 'Criar categoria' }}
        </button>
      </form>
    </section>

    <section v-else class="panel category-panel">
      <div class="section-heading">
        <p class="eyebrow">Categorias</p>
        <h2>Categorias cadastradas</h2>
        <p>Consulte atributos e adicione novos campos para uma categoria.</p>
      </div>

      <div class="category-browser">
        <div v-if="categories.length > 0" class="category-list">
          <article v-for="category in categories" :key="category.id" class="category-card">
            <strong>{{ category.name }}</strong>
            <span>ID {{ category.id }}</span>
            <button type="button" class="secondary-button" @click="editingCategory = category">Editar</button>
          </article>
        </div>
        <p v-else class="empty-note">Nenhuma categoria cadastrada ainda.</p>
      </div>
    </section>

    <p v-if="feedback" class="message success">{{ feedback }}</p>

    <CategoryEditModal
      v-if="editingCategory"
      :category="editingCategory"
      @close="editingCategory = null"
      @saved="emit('changed')"
    />
  </section>
</template>
