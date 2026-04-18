<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  userName: string
}>()

const emit = defineEmits<{
  logout: []
  openProfile: []
}>()

const menuOpen = ref(false)

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}

function openProfile() {
  menuOpen.value = false
  emit('openProfile')
}

function logout() {
  menuOpen.value = false
  emit('logout')
}
</script>

<template>
  <header class="app-header">
    <div class="header-title">
      <strong>Product Catalog CMS</strong>
    </div>

    <div class="user-dropdown">
      <button type="button" class="user-menu" aria-label="Abrir opcoes do usuario" @click="toggleMenu">
        <div class="avatar" aria-hidden="true">{{ userName.charAt(0).toUpperCase() }}</div>
        <div>
          <strong>{{ userName }}</strong>
          <span>Administrador</span>
        </div>
      </button>

      <div v-if="menuOpen" class="user-options">
        <button type="button" @click="openProfile">Editar perfil</button>
        <button type="button" @click="logout">Sair</button>
      </div>
    </div>
  </header>
</template>
