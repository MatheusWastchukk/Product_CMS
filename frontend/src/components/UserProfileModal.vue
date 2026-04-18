<script setup lang="ts">
import { reactive, ref } from 'vue'

const props = defineProps<{
  userName: string
}>()

const emit = defineEmits<{
  close: []
  logout: []
  save: [payload: { userName: string; password: string }]
}>()

const form = reactive({
  userName: props.userName,
  password: '',
  confirmPassword: ''
})

const error = ref('')

function submit() {
  if (!form.userName.trim()) {
    error.value = 'Informe o nome do usuario.'
    return
  }

  if (form.password !== form.confirmPassword) {
    error.value = 'A confirmacao de senha precisa ser igual a senha digitada.'
    return
  }

  emit('save', {
    userName: form.userName.trim(),
    password: form.password
  })
}
</script>

<template>
  <div class="profile-backdrop" role="presentation" @click.self="emit('close')">
    <section class="profile-panel" aria-labelledby="profile-title">
      <div class="section-heading">
        <p class="eyebrow">Usuario</p>
        <h2 id="profile-title">Editar perfil</h2>
        <p>Atualize o nome exibido no sistema e defina uma nova senha local.</p>
      </div>

      <form class="profile-form" @submit.prevent="submit">
        <label class="field">
          <span>Nome</span>
          <input v-model="form.userName" />
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
          <button type="submit">Salvar alteracoes</button>
        </div>
      </form>
    </section>
  </div>
</template>
