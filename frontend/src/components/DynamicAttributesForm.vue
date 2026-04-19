<script setup lang="ts">
import type { CategoryAttribute, ProductAttribute } from '../types/catalog'

defineProps<{
  attributes: CategoryAttribute[]
  values: Record<string, string>
}>()

const emit = defineEmits<{
  change: [value: ProductAttribute]
}>()

function updateValue(name: string, event: Event) {
  const field = event.target as HTMLInputElement | HTMLSelectElement
  emit('change', { name, value: field.value })
}

function inputType(type: string) {
  if (type === 'int' || type === 'decimal') {
    return 'number'
  }

  if (type === 'date') {
    return 'date'
  }

  return 'text'
}
</script>

<template>
  <div class="dynamic-fields">
    <p v-if="attributes.length === 0" class="empty-note">
      Escolha uma categoria para carregar os atributos.
    </p>

    <label v-for="attribute in attributes" :key="attribute.id" class="field">
      <span>{{ attribute.name }}</span>
      <select
        v-if="attribute.type === 'boolean'"
        :value="values[attribute.name] ?? ''"
        @change="updateValue(attribute.name, $event)"
      >
        <option value="">Selecione</option>
        <option value="true">Sim</option>
        <option value="false">Não</option>
      </select>
      <input
        v-else
        :type="inputType(attribute.type)"
        :step="attribute.type === 'decimal' ? '0.01' : attribute.type === 'int' ? '1' : undefined"
        :value="values[attribute.name] ?? ''"
        :placeholder="`Informe ${attribute.name} (${attribute.type})`"
        @input="updateValue(attribute.name, $event)"
      />
    </label>
  </div>
</template>
