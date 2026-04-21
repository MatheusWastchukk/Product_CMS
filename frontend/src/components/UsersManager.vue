<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Edit3, Plus, RotateCcw, Save, Trash2, UserRound, X } from 'lucide-vue-next'
import { createUser, deleteUser, getUsers, updateUser } from '../services/api'
import type { AppUser, PageResponse } from '../types/catalog'
import PaginationControls from './PaginationControls.vue'

const props = defineProps<{
  currentUser: AppUser
}>()

const users = ref<AppUser[]>([])
const usersPage = ref<PageResponse<AppUser> | null>(null)
const activeUserTab = ref<'create' | 'list'>('create')
const editingUser = ref<AppUser | null>(null)
const loading = ref(false)
const currentPage = ref(0)
const pageSize = 10

const form = reactive({
  name: '',
  username: '',
  email: ''
})

const editForm = reactive({
  name: '',
  username: '',
  email: '',
  resetPassword: false
})

async function loadUsers() {
  loading.value = true
  try {
    const page = await getUsers({ page: currentPage.value, size: pageSize })
    usersPage.value = page
    users.value = page.content
  } catch {
    alert('Não foi possível carregar os usuários.')
  } finally {
    loading.value = false
  }
}

async function changePage(page: number) {
  currentPage.value = page
  await loadUsers()
}

async function createNewUser() {
  const name = form.name.trim()
  const username = form.username.trim()
  const email = form.email.trim()

  if (!name || !username) {
    alert('Informe nome e usuário.')
    return
  }

  try {
    await createUser({ name, username, email })
    form.name = ''
    form.username = ''
    form.email = ''
    activeUserTab.value = 'list'
    await loadUsers()
  } catch {
    alert('Não foi possível criar o usuário. Verifique se o usuário ou e-mail já existe.')
  }
}

function openEditModal(user: AppUser) {
  editingUser.value = user
  editForm.name = user.name
  editForm.username = user.username
  editForm.email = user.email ?? ''
  editForm.resetPassword = false
}

async function saveEditedUser() {
  if (!editingUser.value) {
    return
  }

  const name = editForm.name.trim()
  const username = editForm.username.trim()
  const email = editForm.email.trim()

  if (!name || !username) {
    alert('Informe nome e usuário.')
    return
  }

  try {
    await updateUser(editingUser.value.id, {
      name,
      username,
      email,
      resetPassword: editForm.resetPassword
    })
    editingUser.value = null
    await loadUsers()
  } catch {
    alert('Não foi possível atualizar o usuário.')
  }
}

async function removeUser(user: AppUser) {
  if (user.id === props.currentUser.id) {
    alert('Você não pode excluir o usuário logado.')
    return
  }

  if (!confirm(`Excluir o usuário "${user.name}"?`)) {
    return
  }

  try {
    await deleteUser(user.id)
    await loadUsers()
  } catch {
    alert('Não foi possível excluir o usuário.')
  }
}

onMounted(loadUsers)
</script>

<template>
  <section class="tab-page">
    <div class="subtabs" aria-label="Abas de usuários">
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
        Usuários cadastrados
      </button>
    </div>

    <section v-if="activeUserTab === 'create'" class="panel user-panel">
      <div class="section-heading">
        <p class="eyebrow">Usuários</p>
        <h2>Criar usuário</h2>
        <p>Novos usuários recebem a senha padrão.</p>
      </div>

      <form class="profile-form" @submit.prevent="createNewUser">
        <label class="field">
          <span>Nome</span>
          <input v-model="form.name" placeholder="Maria Silva" />
        </label>

        <label class="field">
          <span>Usuário</span>
          <input v-model="form.username" autocomplete="username" placeholder="maria.silva" />
        </label>

        <label class="field">
          <span>E-mail</span>
          <input v-model="form.email" type="email" placeholder="maria@email.com" />
        </label>

        <button type="submit">
          <Plus :size="18" />
          Criar usuário
        </button>
      </form>
    </section>

    <section v-else class="panel user-panel">
      <div class="section-heading">
        <p class="eyebrow">Usuários</p>
        <h2>Usuários cadastrados</h2>
        <p>Lista carregada pela API do backend.</p>
      </div>

      <p v-if="loading" class="empty-note">Carregando usuários...</p>

      <table v-else-if="users.length > 0" class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Usuário</th>
            <th>E-mail</th>
            <th>Ações</th>
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
            <td>{{ user.username }}</td>
            <td>{{ user.email || 'Não informado' }}</td>
            <td>
              <div class="table-actions">
                <button type="button" class="icon-button" title="Editar" @click="openEditModal(user)">
                  <Edit3 :size="17" />
                </button>
                <button type="button" class="icon-button danger-icon" title="Excluir" @click="removeUser(user)">
                  <Trash2 :size="17" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty-note">Nenhum usuário criado ainda.</p>

      <PaginationControls
        v-if="usersPage"
        :page="usersPage.page"
        :total-pages="usersPage.totalPages"
        :total-elements="usersPage.totalElements"
        :loading="loading"
        @change="changePage"
      />
    </section>

    <div v-if="editingUser" class="profile-backdrop" role="presentation" @click.self="editingUser = null">
      <section class="profile-panel" aria-labelledby="edit-user-title">
        <div class="section-heading">
          <p class="eyebrow">Usuário</p>
          <h2 id="edit-user-title">Editar usuário</h2>
          <p>Atualize nome, usuário, e-mail ou redefina a senha padrão.</p>
        </div>

        <form class="profile-form" @submit.prevent="saveEditedUser">
          <label class="field">
            <span>Nome</span>
            <input v-model="editForm.name" />
          </label>

          <label class="field">
            <span>Usuário</span>
            <input v-model="editForm.username" />
          </label>

          <label class="field">
            <span>E-mail</span>
            <input v-model="editForm.email" type="email" />
          </label>

          <label class="check-field">
            <input v-model="editForm.resetPassword" type="checkbox" />
            <span>
              <RotateCcw :size="17" />
              Resetar senha
            </span>
          </label>

          <div class="profile-actions">
            <button type="button" class="ghost-button" @click="editingUser = null">
              <X :size="18" />
              Cancelar
            </button>
            <button type="submit">
              <Save :size="18" />
              Salvar usuário
            </button>
          </div>
        </form>
      </section>
    </div>
  </section>
</template>
