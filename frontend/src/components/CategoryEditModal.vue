<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { addCategoryAttributes, getCategoryAttributes } from '../services/api'
import type { Category, CategoryAttribute } from '../types/catalog'

const props = defineProps<{
  category: Category
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const attributes = ref<CategoryAttribute[]>([])
const newAttributes = ref('')
const loading = ref(false)
const saving = ref(false)

async function loadAttributes() {
  loading.value = true
  try {
    attributes.value = await getCategoryAttributes(props.category.id)
  } catch {
    alert('Nao foi possivel carregar os atributos.')
  } finally {
    loading.value = false
  }
}

async function submit() {
  const parsedAttributes = newAttributes.value
    .split(',')
    .map((attribute) => attribute.trim())
    .filter(Boolean)

  if (parsedAttributes.length === 0) {
    alert('Informe ao menos um atributo.')
    return
  }

  saving.value = true
  try {
    await addCategoryAttributes(props.category.id, parsedAttributes)
    newAttributes.value = ''
    await loadAttributes()
    emit('saved')
  } catch {
    alert('Nao foi possivel adicionar os atributos.')
  } finally {
    saving.value = false
  }
}

onMounted(loadAttributes)
</script>

<template>
  <div class="profile-backdrop" role="presentation" @click.self="emit('close')">
    <section class="profile-panel" aria-labelledby="edit-category-title">
      <div class="section-heading">
        <p class="eyebrow">Categoria</p>
        <h2 id="edit-category-title">Editar {{ category.name }}</h2>
        <p>Adicione novos atributos dinamicos para essa categoria.</p>
      </div>

      <p v-if="loading" class="empty-note">Carregando atributos...</p>
      <div v-else-if="attributes.length > 0" class="tag-list">
        <span v-for="attribute in attributes" :key="attribute.id">{{ attribute.name }}</span>
      </div>
      <p v-else class="empty-note">Nenhum atributo cadastrado.</p>

      <form class="profile-form" @submit.prevent="submit">
        <label class="field">
          <span>Novos atributos</span>
          <input v-model="newAttributes" placeholder="marca, voltagem, garantia" />
        </label>

        <div class="profile-actions">
          <button type="button" class="ghost-button" @click="emit('close')">Cancelar</button>
          <button type="submit" :disabled="saving">{{ saving ? 'Salvando...' : 'Adicionar atributos' }}</button>
        </div>
      </form>
    </section>
  </div>
</template>
