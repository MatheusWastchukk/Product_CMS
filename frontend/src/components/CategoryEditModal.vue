<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import { addCategoryAttributes, getCategoryAttributes } from '../services/api'
import type { AttributeType, Category, CategoryAttribute } from '../types/catalog'

const props = defineProps<{
  category: Category
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const attributes = ref<CategoryAttribute[]>([])
const newAttributeName = ref('')
const newAttributeType = ref<AttributeType>('string')
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
  const attributeName = newAttributeName.value.trim()

  if (!attributeName) {
    alert('Informe o nome do atributo.')
    return
  }

  saving.value = true
  try {
    await addCategoryAttributes(props.category.id, [{ name: attributeName, type: newAttributeType.value }])
    newAttributeName.value = ''
    newAttributeType.value = 'string'
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
      <table v-else-if="attributes.length > 0" class="data-table compact-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Atributo</th>
            <th>Tipo</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="attribute in attributes" :key="attribute.id">
            <td>{{ attribute.id }}</td>
            <td>{{ attribute.name }}</td>
            <td><span class="type-pill">{{ attribute.type }}</span></td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty-note">Nenhum atributo cadastrado.</p>

      <form class="profile-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome do atributo</span>
          <input v-model="newAttributeName" placeholder="marca" />
        </label>

        <label class="field">
          <span>Tipo do atributo</span>
          <select v-model="newAttributeType">
            <option value="string">string</option>
            <option value="int">int</option>
            <option value="decimal">decimal</option>
            <option value="boolean">boolean</option>
            <option value="date">date</option>
          </select>
        </label>

        <div class="profile-actions">
          <button type="button" class="ghost-button" @click="emit('close')">Cancelar</button>
          <button type="submit" :disabled="saving">
            <Plus :size="18" />
            {{ saving ? 'Salvando...' : 'Adicionar atributo' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>
