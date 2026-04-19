<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { Save } from 'lucide-vue-next'
import DynamicAttributesForm from './DynamicAttributesForm.vue'
import { createProduct, getCategoryAttributes } from '../services/api'
import type { Category, CategoryAttribute, ProductAttribute, ProductPayload } from '../types/catalog'

const props = defineProps<{
  categories: Category[]
}>()

const emit = defineEmits<{
  created: []
}>()

const form = reactive<ProductPayload>({
  name: '',
  description: '',
  price: 0,
  categoryId: null,
  attributes: []
})

const dynamicAttributes = ref<CategoryAttribute[]>([])
const attributeValues = reactive<Record<string, string>>({})
const loadingAttributes = ref(false)
const saving = ref(false)
const feedback = ref('')

const selectedCategoryName = computed(() => {
  return props.categories.find((category) => category.id === form.categoryId)?.name ?? ''
})

const canSubmit = computed(() => {
  return form.name.trim().length > 0 && form.categoryId !== null && form.price >= 0 && !saving.value
})

watch(
  () => form.categoryId,
  async (categoryId) => {
    dynamicAttributes.value = []
    form.attributes = []
    Object.keys(attributeValues).forEach((key) => delete attributeValues[key])
    feedback.value = ''

    if (!categoryId) {
      return
    }

    loadingAttributes.value = true
    try {
      dynamicAttributes.value = await getCategoryAttributes(categoryId)
    } catch {
      showError('Nao foi possivel carregar os atributos da categoria.')
    } finally {
      loadingAttributes.value = false
    }
  }
)

function handleAttributeChange(attribute: ProductAttribute) {
  attributeValues[attribute.name] = attribute.value
  form.attributes = Object.entries(attributeValues)
    .filter(([, value]) => value.trim().length > 0)
    .map(([name, value]) => ({ name, value }))
}

async function submit() {
  if (!canSubmit.value || form.categoryId === null) {
    showError('Preencha nome, preco e categoria antes de salvar.')
    return
  }

  saving.value = true
  feedback.value = ''

  try {
    await createProduct({
      name: form.name,
      description: form.description,
      price: Number(form.price),
      categoryId: form.categoryId,
      attributes: form.attributes
    })

    form.name = ''
    form.description = ''
    form.price = 0
    form.categoryId = null
    form.attributes = []
    Object.keys(attributeValues).forEach((key) => delete attributeValues[key])
    feedback.value = 'Produto cadastrado com sucesso.'
    emit('created')
  } catch {
    showError('Nao foi possivel salvar o produto. Confira os dados e tente novamente.')
  } finally {
    saving.value = false
  }
}

function showError(message: string) {
  alert(message)
}
</script>

<template>
  <section class="panel form-panel" aria-labelledby="product-form-title">
    <div class="section-heading">
      <p class="eyebrow">Cadastro</p>
      <h2 id="product-form-title">Novo produto</h2>
      <p v-if="categories.length > 0">Categoria selecionada: {{ selectedCategoryName || 'nenhuma' }}</p>
      <p v-else>Cadastre uma categoria antes de criar produtos.</p>
    </div>

    <form class="product-form" @submit.prevent="submit">
      <label class="field">
        <span>Nome</span>
        <input v-model="form.name" placeholder="Notebook X" />
      </label>

      <label class="field">
        <span>Descricao</span>
        <textarea v-model="form.description" placeholder="Detalhes principais do produto"></textarea>
      </label>

      <div class="form-grid">
        <label class="field">
          <span>Preco</span>
          <input v-model.number="form.price" type="number" min="0" step="0.01" />
        </label>

        <label class="field">
          <span>Categoria</span>
          <select v-model.number="form.categoryId" :disabled="categories.length === 0">
            <option :value="null">Selecione</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </label>
      </div>

      <div class="attributes-block">
        <div>
          <h3>Atributos dinamicos</h3>
          <p v-if="loadingAttributes">Carregando atributos...</p>
        </div>
        <DynamicAttributesForm
          :attributes="dynamicAttributes"
          :values="attributeValues"
          @change="handleAttributeChange"
        />
      </div>

      <p v-if="feedback" class="message success">{{ feedback }}</p>

      <button type="submit" :disabled="!canSubmit">
        <Save :size="18" />
        {{ saving ? 'Salvando...' : 'Salvar produto' }}
      </button>
    </form>
  </section>
</template>
