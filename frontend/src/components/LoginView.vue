<script setup lang="ts">
import { computed, reactive } from 'vue'

const emit = defineEmits<{
  login: [payload: { username: string; password: string }]
}>()

const form = reactive({
  username: '',
  password: ''
})

const canLogin = computed(() => {
  return form.username.trim().length > 0 && form.password.trim().length > 0
})

function submit() {
  if (!canLogin.value) {
    alert('Informe usuário e senha para entrar.')
    return
  }

  emit('login', {
    username: form.username.trim(),
    password: form.password
  })
}
</script>

<template>
  <main class="login-screen">
    <section class="login-panel" aria-labelledby="login-title">
      <div class="login-brand">
        <p class="eyebrow">Product Catalog CMS</p>
        <h1 id="login-title">Acesse o catálogo</h1>
        <p>Organize categorias, atributos dinâmicos e produtos em um único painel.</p>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <label class="field">
          <span>Usuário</span>
          <input v-model="form.username" autocomplete="username" placeholder="admin" />
        </label>

        <label class="field">
          <span>Senha</span>
          <input v-model="form.password" autocomplete="current-password" type="password" placeholder="senha" />
        </label>
        <button type="submit" :disabled="!canLogin">Entrar</button>
      </form>
    </section>
  </main>
</template>
