<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Edit3, Plus, Trash2, X } from 'lucide-vue-next'
import { addCategoryAttributes, createCategory, deleteCategory } from '../services/api'
import type { AttributeType, Category, PageResponse } from '../types/catalog'
import CategoryEditModal from './CategoryEditModal.vue'
import PaginationControls from './PaginationControls.vue'

defineProps<{
  categories: Category[]
  pageInfo: PageResponse<Category> | null
  loading: boolean
}>()

const emit = defineEmits<{
  changed: []
  pageChange: [page: number]
}>()

const activeCategoryTab = ref<'create' | 'list'>('create')
const editingCategory = ref<Category | null>(null)

const form = reactive({
  name: ''
})

const attributeRows = ref<Array<{ id: number; name: string; type: AttributeType }>>([
  { id: Date.now(), name: '', type: 'string' }
])
const saving = ref(false)
const feedback = ref('')

async function submit() {
  const name = form.name.trim()

  if (!name) {
    showError('Informe o nome da categoria.')
    return
  }

  saving.value = true
  feedback.value = ''

  try {
    const attributes = attributeRows.value
      .map((attribute) => ({
        name: attribute.name.trim(),
        type: attribute.type
      }))
      .filter((attribute) => attribute.name.length > 0)

    const duplicatedAttribute = attributes.find((attribute, index) => {
      return attributes.findIndex((item) => item.name.toLowerCase() === attribute.name.toLowerCase()) !== index
    })

    if (duplicatedAttribute) {
      showError(`O atributo "${duplicatedAttribute.name}" está duplicado.`)
      return
    }

    const category = await createCategory(name)
    if (attributes.length > 0) {
      await addCategoryAttributes(category.id, attributes)
    }

    form.name = ''
    attributeRows.value = [{ id: Date.now(), name: '', type: 'string' }]
    feedback.value = 'Categoria cadastrada com sucesso.'
    emit('changed')
  } catch {
    showError('Não foi possível salvar a categoria. Verifique se ela já existe.')
  } finally {
    saving.value = false
  }
}

function showError(message: string) {
  alert(message)
}

function addAttributeRow() {
  attributeRows.value.push({
    id: Date.now() + attributeRows.value.length,
    name: '',
    type: 'string'
  })
}

function removeAttributeRow(rowId: number) {
  if (attributeRows.value.length === 1) {
    attributeRows.value = [{ id: Date.now(), name: '', type: 'string' }]
    return
  }

  attributeRows.value = attributeRows.value.filter((attribute) => attribute.id !== rowId)
}

async function removeCategory(category: Category) {
  if (!confirm(`Excluir a categoria "${category.name}"?`)) {
    return
  }

  try {
    await deleteCategory(category.id)
    emit('changed')
  } catch {
    showError('Não foi possível excluir a categoria. Verifique se ela possui produtos cadastrados.')
  }
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
        <p>Crie categorias e monte a lista de atributos antes de salvar.</p>
      </div>

      <form class="category-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome da categoria</span>
          <input v-model="form.name" placeholder="Acessorio" />
        </label>

        <div class="attribute-rows">
          <div v-for="attribute in attributeRows" :key="attribute.id" class="attribute-builder">
            <label class="field">
              <span>Nome do atributo</span>
              <input v-model="attribute.name" placeholder="modelo" />
            </label>

            <label class="field">
              <span>Tipo do atributo</span>
              <select v-model="attribute.type">
                <option value="string">string</option>
                <option value="int">int</option>
                <option value="decimal">decimal</option>
                <option value="boolean">boolean</option>
                <option value="date">date</option>
              </select>
            </label>

            <button
              type="button"
              class="icon-button danger-icon"
              title="Remover atributo"
              @click="removeAttributeRow(attribute.id)"
            >
              <X :size="17" />
            </button>
          </div>

          <button type="button" class="secondary-button fit-button" @click="addAttributeRow">
            <Plus :size="18" />
            Adicionar outro atributo
          </button>
        </div>

        <button type="submit" :disabled="saving">
          <Plus :size="18" />
          {{ saving ? 'Salvando...' : 'Criar categoria' }}
        </button>
      </form>
    </section>

    <section v-else class="panel category-panel">
      <div class="section-heading">
        <p class="eyebrow">Categorias</p>
        <h2>Categorias cadastradas</h2>
        <p>Gerencie categorias cadastradas.</p>
      </div>

      <div class="category-browser">
        <p v-if="loading" class="empty-note">Carregando categorias...</p>

        <table v-else-if="categories.length > 0" class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="category in categories" :key="category.id">
              <td>{{ category.id }}</td>
              <td>{{ category.name }}</td>
              <td>
                <div class="table-actions">
                  <button type="button" class="icon-button" title="Editar" @click="editingCategory = category">
                    <Edit3 :size="17" />
                  </button>
                  <button type="button" class="icon-button danger-icon" title="Excluir" @click="removeCategory(category)">
                    <Trash2 :size="17" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-else class="empty-note">Nenhuma categoria cadastrada ainda.</p>

        <PaginationControls
          v-if="pageInfo"
          :page="pageInfo.page"
          :total-pages="pageInfo.totalPages"
          :total-elements="pageInfo.totalElements"
          :loading="loading"
          @change="emit('pageChange', $event)"
        />
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
