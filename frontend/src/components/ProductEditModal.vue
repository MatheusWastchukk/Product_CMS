<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { getCategoryAttributes, updateProduct } from '../services/api'
import type { Category, CategoryAttribute, Product, ProductAttribute, ProductPayload } from '../types/catalog'
import DynamicAttributesForm from './DynamicAttributesForm.vue'

const props = defineProps<{
  product: Product
  categories: Category[]
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const form = reactive<ProductPayload>({
  name: props.product.name,
  description: props.product.description,
  price: props.product.price,
  categoryId: props.product.categoryId,
  attributes: [...props.product.attributes]
})

const dynamicAttributes = ref<CategoryAttribute[]>([])
const attributeValues = reactive<Record<string, string>>({})
const saving = ref(false)
const loadingAttributes = ref(false)

const canSubmit = computed(() => {
  return form.name.trim().length > 0 && form.categoryId !== null && form.price >= 0 && !saving.value
})

props.product.attributes.forEach((attribute) => {
  attributeValues[attribute.name] = attribute.value
})

watch(
  () => form.categoryId,
  async (categoryId) => {
    dynamicAttributes.value = []
    Object.keys(attributeValues).forEach((key) => delete attributeValues[key])
    form.attributes = []

    if (!categoryId) {
      return
    }

    loadingAttributes.value = true
    try {
      dynamicAttributes.value = await getCategoryAttributes(categoryId)
      dynamicAttributes.value.forEach((attribute) => {
        const current = props.product.attributes.find((item) => item.name === attribute.name)
        attributeValues[attribute.name] = current?.value ?? ''
      })
      syncAttributes()
    } catch {
      alert('Nao foi possivel carregar os atributos da categoria.')
    } finally {
      loadingAttributes.value = false
    }
  },
  { immediate: true }
)

function handleAttributeChange(attribute: ProductAttribute) {
  attributeValues[attribute.name] = attribute.value
  syncAttributes()
}

function syncAttributes() {
  form.attributes = Object.entries(attributeValues)
    .filter(([, value]) => value.trim().length > 0)
    .map(([name, value]) => ({ name, value }))
}

async function submit() {
  if (!canSubmit.value || form.categoryId === null) {
    alert('Preencha nome, preco e categoria antes de salvar.')
    return
  }

  saving.value = true
  try {
    await updateProduct(props.product.id, {
      name: form.name,
      description: form.description,
      price: Number(form.price),
      categoryId: form.categoryId,
      attributes: form.attributes
    })
    emit('saved')
  } catch {
    alert('Nao foi possivel atualizar o produto.')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="profile-backdrop" role="presentation" @click.self="emit('close')">
    <section class="profile-panel wide-modal" aria-labelledby="edit-product-title">
      <div class="section-heading">
        <p class="eyebrow">Produto</p>
        <h2 id="edit-product-title">Editar produto</h2>
        <p>Atualize dados fixos e atributos dinamicos.</p>
      </div>

      <form class="product-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome</span>
          <input v-model="form.name" />
        </label>

        <label class="field">
          <span>Descricao</span>
          <textarea v-model="form.description"></textarea>
        </label>

        <div class="form-grid">
          <label class="field">
            <span>Preco</span>
            <input v-model.number="form.price" type="number" min="0" step="0.01" />
          </label>

          <label class="field">
            <span>Categoria</span>
            <select v-model.number="form.categoryId">
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </label>
        </div>

        <div class="attributes-block">
          <h3>Atributos dinamicos</h3>
          <p v-if="loadingAttributes">Carregando atributos...</p>
          <DynamicAttributesForm
            :attributes="dynamicAttributes"
            :values="attributeValues"
            @change="handleAttributeChange"
          />
        </div>

        <div class="profile-actions">
          <button type="button" class="ghost-button" @click="emit('close')">Cancelar</button>
          <button type="submit" :disabled="!canSubmit">{{ saving ? 'Salvando...' : 'Salvar produto' }}</button>
        </div>
      </form>
    </section>
  </div>
</template>
