<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Plus, UserRound } from 'lucide-vue-next'

const props = defineProps<{
  currentUser: string
}>()

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
  ensureCurrentUser()
}

function saveUsers() {
  localStorage.setItem('productCmsUsers', JSON.stringify(users.value))
}

function ensureCurrentUser() {
  if (!props.currentUser) {
    return
  }

  const exists = users.value.some((user) => user.name.toLowerCase() === props.currentUser.toLowerCase())
  if (!exists) {
    users.value.unshift({
      id: Date.now(),
      name: props.currentUser,
      password: 'senha'
    })
    saveUsers()
  }
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

        <button type="submit">
          <Plus :size="18" />
          Criar usuario
        </button>
      </form>
    </section>

    <section v-else class="panel user-panel">
      <div class="section-heading">
        <p class="eyebrow">Usuarios</p>
        <h2>Usuarios cadastrados</h2>
        <p>Lista mantida localmente no navegador.</p>
      </div>

      <table v-if="users.length > 0" class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Usuario</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>
              <span class="user-cell">
                <UserRound :size="17" />
                {{ user.name }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty-note">Nenhum usuario criado ainda.</p>
    </section>
  </section>
</template>
