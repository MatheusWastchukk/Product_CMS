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
  const input = event.target as HTMLInputElement
  emit('change', { name, value: input.value })
}
</script>

<template>
  <div class="dynamic-fields">
    <p v-if="attributes.length === 0" class="empty-note">
      Escolha uma categoria para carregar os atributos.
    </p>

    <label v-for="attribute in attributes" :key="attribute.id" class="field">
      <span>{{ attribute.name }}</span>
      <input
        :value="values[attribute.name] ?? ''"
        :placeholder="`Informe ${attribute.name}`"
        @input="updateValue(attribute.name, $event)"
      />
    </label>
  </div>
</template>
