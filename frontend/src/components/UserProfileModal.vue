<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { AppUser } from '../types/catalog'

const props = defineProps<{
  user: AppUser
}>()

const emit = defineEmits<{
  close: []
  logout: []
  save: [payload: { name: string; password: string }]
}>()

const form = reactive({
  name: props.user.name,
  password: '',
  confirmPassword: ''
})

const error = ref('')

function submit() {
  if (!form.name.trim()) {
    error.value = 'Informe o nome do usuário.'
    return
  }

  if (form.password !== form.confirmPassword) {
    error.value = 'A confirmação de senha precisa ser igual à senha digitada.'
    return
  }

  emit('save', {
    name: form.name.trim(),
    password: form.password
  })
}
</script>

<template>
  <div class="profile-backdrop" role="presentation" @click.self="emit('close')">
    <section class="profile-panel" aria-labelledby="profile-title">
      <div class="section-heading">
        <p class="eyebrow">Usuário</p>
        <h2 id="profile-title">Editar perfil</h2>
        <p>Atualize o nome exibido no sistema e defina uma nova senha de acesso.</p>
      </div>

      <form class="profile-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome</span>
          <input v-model="form.name" />
        </label>

        <label class="field">
          <span>Usuário</span>
          <input :value="props.user.username" disabled />
        </label>

        <label class="field">
          <span>Senha</span>
          <input v-model="form.password" type="password" placeholder="Digite uma nova senha" />
        </label>

        <label class="field">
          <span>Confirmar senha</span>
          <input v-model="form.confirmPassword" type="password" placeholder="Repita a senha" />
        </label>

        <p v-if="error" class="message error">{{ error }}</p>

        <div class="profile-actions">
          <button type="button" class="ghost-button" @click="emit('close')">Cancelar</button>
          <button type="submit">Salvar alterações</button>
        </div>
      </form>
    </section>
  </div>
</template>
