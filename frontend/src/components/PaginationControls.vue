<script setup lang="ts">
defineProps<{
  page: number
  totalPages: number
  totalElements: number
  loading?: boolean
}>()

const emit = defineEmits<{
  change: [page: number]
}>()
</script>

<template>
  <nav v-if="totalPages > 1" class="pagination" aria-label="Paginação">
    <button
      type="button"
      class="ghost-button"
      :disabled="loading || page === 0"
      @click="emit('change', page - 1)"
    >
      Anterior
    </button>
    <span>Página {{ page + 1 }} de {{ totalPages }} · {{ totalElements }} registros</span>
    <button
      type="button"
      class="ghost-button"
      :disabled="loading || page >= totalPages - 1"
      @click="emit('change', page + 1)"
    >
      Próxima
    </button>
  </nav>
</template>
