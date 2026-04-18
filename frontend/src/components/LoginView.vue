<script setup lang="ts">
import { computed, reactive } from 'vue'

const emit = defineEmits<{
  login: [userName: string]
}>()

const form = reactive({
  email: '',
  password: ''
})

const canLogin = computed(() => {
  return form.email.trim().length > 0 && form.password.trim().length > 0
})

function submit() {
  if (!canLogin.value) {
    alert('Informe e-mail e senha para entrar.')
    return
  }

  const userName = form.email.split('@')[0] || 'Usuario'
  emit('login', userName)
}
</script>

<template>
  <main class="login-screen">
    <section class="login-panel" aria-labelledby="login-title">
      <div class="login-brand">
        <p class="eyebrow">AZ Tech case</p>
        <h1 id="login-title">Acesse o catalogo</h1>
        <p>Organize categorias, atributos dinamicos e produtos em um unico painel.</p>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <label class="field">
          <span>E-mail</span>
          <input v-model="form.email" type="email" placeholder="admin@aztech.com" />
        </label>

        <label class="field">
          <span>Senha</span>
          <input v-model="form.password" type="password" placeholder="admin" />
        </label>
        <button type="submit" :disabled="!canLogin">Entrar</button>
      </form>
    </section>
  </main>
</template>
