<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Boxes, Tags, Users } from 'lucide-vue-next'
import AppHeader from '../components/AppHeader.vue'
import CategoryManager from '../components/CategoryManager.vue'
import LoginView from '../components/LoginView.vue'
import ProductForm from '../components/ProductForm.vue'
import ProductEditModal from '../components/ProductEditModal.vue'
import ProductList from '../components/ProductList.vue'
import UserProfileModal from '../components/UserProfileModal.vue'
import UsersManager from '../components/UsersManager.vue'
import {
  clearSession,
  deleteProduct,
  getCategories,
  getProducts,
  getStoredToken,
  getStoredUser,
  login,
  logout,
  updateProfile
} from '../services/api'
import type { AppUser, Category, Product } from '../types/catalog'

const currentUser = ref<AppUser | null>(getStoredToken() ? getStoredUser() : null)
const profileOpen = ref(false)
const activeTab = ref<'categories' | 'products' | 'users'>('products')
const activeProductTab = ref<'create' | 'list'>('create')
const editingProduct = ref<Product | null>(null)
const categories = ref<Category[]>([])
const products = ref<Product[]>([])
const productNameFilter = ref('')
const selectedCategoryFilter = ref<number | null>(null)
const loadingProducts = ref(false)
const loadingCategories = ref(false)

const filteredProducts = computed(() => {
  const name = productNameFilter.value.trim().toLowerCase()

  return products.value.filter((product) => {
    const matchesName = !name || product.name.toLowerCase().includes(name)
    const matchesCategory = !selectedCategoryFilter.value || product.categoryId === selectedCategoryFilter.value

    return matchesName && matchesCategory
  })
})

async function loadCategories() {
  loadingCategories.value = true
  try {
    categories.value = await getCategories()
  } catch {
    showError('Não foi possível carregar as categorias.')
  } finally {
    loadingCategories.value = false
  }
}

async function loadProducts() {
  loadingProducts.value = true
  try {
    products.value = await getProducts()
  } catch {
    showError('Não foi possível carregar os produtos.')
  } finally {
    loadingProducts.value = false
  }
}

function showError(message: string) {
  alert(message)
}

async function handleLogin(payload: { username: string; password: string }) {
  try {
    const auth = await login(payload.username, payload.password)
    currentUser.value = auth.user
    await Promise.all([loadCategories(), loadProducts()])
  } catch {
    showError('Usuário ou senha inválidos.')
  }
}

async function handleLogout() {
  try {
    await logout()
  } catch {
    clearSession()
  } finally {
    currentUser.value = null
    profileOpen.value = false
    categories.value = []
    products.value = []
  }
}

async function handleProfileSave(payload: { name: string; password: string }) {
  try {
    currentUser.value = await updateProfile(payload)
    profileOpen.value = false
  } catch {
    showError('Não foi possível atualizar o perfil.')
  }
}

async function handleCategoriesChanged() {
  await loadCategories()
  await loadProducts()
}

async function handleProductUpdated() {
  editingProduct.value = null
  await loadProducts()
}

async function handleProductDelete(product: Product) {
  if (!confirm(`Excluir o produto "${product.name}"?`)) {
    return
  }

  try {
    await deleteProduct(product.id)
    await loadProducts()
  } catch {
    showError('Não foi possível excluir o produto.')
  }
}

onMounted(async () => {
  if (currentUser.value) {
    await Promise.all([loadCategories(), loadProducts()])
  }
})
</script>

<template>
  <LoginView v-if="!currentUser" @login="handleLogin" />

  <main v-else class="app-layout">
    <aside class="sidebar" aria-label="Navegação principal">
      <nav class="sidebar-nav">
        <button
          type="button"
          :class="{ active: activeTab === 'products' }"
          @click="activeTab = 'products'"
        >
          <Boxes :size="18" />
          Produtos
        </button>
        <button
          type="button"
          :class="{ active: activeTab === 'categories' }"
          @click="activeTab = 'categories'"
        >
          <Tags :size="18" />
          Categorias
        </button>
        <button
          type="button"
          :class="{ active: activeTab === 'users' }"
          @click="activeTab = 'users'"
        >
          <Users :size="18" />
          Usuários
        </button>
      </nav>
    </aside>

    <AppHeader
      :user="currentUser"
      @logout="handleLogout"
      @open-profile="profileOpen = true"
    />

    <section class="content-area">
      <header class="page-heading">
        <p class="eyebrow">Página atual</p>
        <h1>{{ activeTab === 'products' ? 'Produtos' : activeTab === 'categories' ? 'Categorias' : 'Usuários' }}</h1>
      </header>

      <section v-if="activeTab === 'categories'" class="tab-page">
        <CategoryManager :categories="categories" @changed="handleCategoriesChanged" />
      </section>

      <UsersManager v-else-if="activeTab === 'users'" :current-user="currentUser" />

      <section v-else class="tab-page">
        <div class="subtabs" aria-label="Abas de produtos">
          <button
            type="button"
            :class="{ active: activeProductTab === 'create' }"
            @click="activeProductTab = 'create'"
          >
            Cadastro
          </button>
          <button
            type="button"
            :class="{ active: activeProductTab === 'list' }"
            @click="activeProductTab = 'list'"
          >
            Produtos cadastrados
          </button>
        </div>

        <div v-if="activeProductTab === 'create'" class="single-column">
          <ProductForm :categories="categories" @created="loadProducts" />
        </div>

        <div v-else class="single-column">
          <section class="toolbar" aria-label="Filtros do catálogo">
            <label class="field compact-field">
              <span>Filtrar por nome</span>
              <input v-model="productNameFilter" placeholder="Notebook" />
            </label>

            <label class="field compact-field">
              <span>Filtrar por categoria</span>
              <select v-model.number="selectedCategoryFilter" :disabled="loadingCategories">
                <option :value="null">Todas</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </label>

            <button type="button" class="secondary-button" @click="loadProducts">
              Atualizar lista
            </button>
          </section>

          <ProductList
            :products="filteredProducts"
            :loading="loadingProducts"
            @edit="editingProduct = $event"
            @delete="handleProductDelete"
          />
        </div>
      </section>
    </section>

    <UserProfileModal
      v-if="profileOpen"
      :user="currentUser"
      @close="profileOpen = false"
      @logout="handleLogout"
      @save="handleProfileSave"
    />

    <ProductEditModal
      v-if="editingProduct"
      :product="editingProduct"
      :categories="categories"
      @close="editingProduct = null"
      @saved="handleProductUpdated"
    />
  </main>
</template>
