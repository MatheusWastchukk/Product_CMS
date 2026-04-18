<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

interface AppUser {
  id: number
  name: string
  password: string
}

const users = ref<AppUser[]>([])
const activeUserTab = ref<'create' | 'list'>('create')
const form = reactive({
  name: ''
})

function loadUsers() {
  const savedUsers = localStorage.getItem('productCmsUsers')
  users.value = savedUsers ? JSON.parse(savedUsers) : []
}

function saveUsers() {
  localStorage.setItem('productCmsUsers', JSON.stringify(users.value))
}

function createUser() {
  const name = form.name.trim()
  if (!name) {
    alert('Informe o nome do usuario.')
    return
  }

  users.value.push({
    id: Date.now(),
    name,
    password: 'senha'
  })
  form.name = ''
  saveUsers()
}

onMounted(loadUsers)
</script>

<template>
  <section class="tab-page">
    <div class="subtabs" aria-label="Abas de usuarios">
      <button
        type="button"
        :class="{ active: activeUserTab === 'create' }"
        @click="activeUserTab = 'create'"
      >
        Cadastro
      </button>
      <button
        type="button"
        :class="{ active: activeUserTab === 'list' }"
        @click="activeUserTab = 'list'"
      >
        Usuarios cadastrados
      </button>
    </div>

    <section v-if="activeUserTab === 'create'" class="panel user-panel">
      <div class="section-heading">
        <p class="eyebrow">Usuarios</p>
        <h2>Criar usuario</h2>
        <p>Novos usuarios recebem a senha padrao senha.</p>
      </div>

      <form class="profile-form" @submit.prevent="createUser">
        <label class="field">
          <span>Nome</span>
          <input v-model="form.name" placeholder="Maria Silva" />
        </label>

        <button type="submit">Criar usuario</button>
      </form>
    </section>

    <section v-else class="panel user-panel">
      <div class="section-heading">
        <p class="eyebrow">Usuarios</p>
        <h2>Usuarios cadastrados</h2>
        <p>Lista mantida localmente no navegador.</p>
      </div>

      <div v-if="users.length > 0" class="user-list">
        <article v-for="user in users" :key="user.id" class="user-card">
          <strong>{{ user.name }}</strong>
          <span>Senha padrao: {{ user.password }}</span>
        </article>
      </div>
      <p v-else class="empty-note">Nenhum usuario criado ainda.</p>
    </section>
  </section>
</template>
