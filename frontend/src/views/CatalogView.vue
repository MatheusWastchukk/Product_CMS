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
import { getCategories, getProducts } from '../services/api'
import type { Category, Product } from '../types/catalog'

const loggedUser = ref(localStorage.getItem('productCmsUser') ?? '')
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
    showError('Nao foi possivel carregar as categorias.')
  } finally {
    loadingCategories.value = false
  }
}

async function loadProducts() {
  loadingProducts.value = true
  try {
    products.value = await getProducts()
  } catch {
    showError('Nao foi possivel carregar os produtos.')
  } finally {
    loadingProducts.value = false
  }
}

function showError(message: string) {
  alert(message)
}

function handleLogin(userName: string) {
  loggedUser.value = userName
  localStorage.setItem('productCmsUser', userName)
}

function handleLogout() {
  loggedUser.value = ''
  profileOpen.value = false
  localStorage.removeItem('productCmsUser')
}

function handleProfileSave(payload: { userName: string; password: string }) {
  loggedUser.value = payload.userName
  localStorage.setItem('productCmsUser', payload.userName)
  if (payload.password) {
    localStorage.setItem('productCmsPassword', payload.password)
  }
  profileOpen.value = false
}

async function handleCategoriesChanged() {
  await loadCategories()
  await loadProducts()
}

async function handleProductUpdated() {
  editingProduct.value = null
  await loadProducts()
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadProducts()])
})
</script>

<template>
  <LoginView v-if="!loggedUser" @login="handleLogin" />

  <main v-else class="app-layout">
    <aside class="sidebar" aria-label="Navegacao principal">
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
          Usuarios
        </button>
      </nav>
    </aside>

    <AppHeader
      :user-name="loggedUser"
      @logout="handleLogout"
      @open-profile="profileOpen = true"
    />

    <section class="content-area">
      <header class="page-heading">
        <p class="eyebrow">Pagina atual</p>
        <h1>{{ activeTab === 'products' ? 'Produtos' : activeTab === 'categories' ? 'Categorias' : 'Usuarios' }}</h1>
      </header>

      <section v-if="activeTab === 'categories'" class="tab-page">
        <CategoryManager :categories="categories" @changed="handleCategoriesChanged" />
      </section>

      <UsersManager v-else-if="activeTab === 'users'" :current-user="loggedUser" />

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
          <section class="toolbar" aria-label="Filtros do catalogo">
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

          <ProductList :products="filteredProducts" :loading="loadingProducts" @edit="editingProduct = $event" />
        </div>
      </section>
    </section>

    <UserProfileModal
      v-if="profileOpen"
      :user-name="loggedUser"
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
