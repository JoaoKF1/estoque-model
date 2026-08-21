import axios from 'axios'

const baseURL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

export const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Anexa o token JWT (se existir) em toda requisição.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('estoquemodel_token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/** Extrai uma mensagem de erro amigável de uma resposta de erro do axios. */
export function extrairMensagemErro(erro: unknown): string {
  if (axios.isAxiosError(erro)) {
    const data = erro.response?.data

    if (data && typeof data === 'object') {
      if ('message' in data && typeof (data as { message?: unknown }).message === 'string') {
        return (data as { message: string }).message
      }
      // Erros de validação (@Valid): { campo: mensagem, ... } -> pega o primeiro.
      const valores = Object.values(data as Record<string, unknown>)
      if (valores.length > 0 && typeof valores[0] === 'string') {
        return valores[0] as string
      }
    }

    if (erro.response?.status === 401) {
      return 'E-mail ou senha inválidos'
    }
  }

  return 'Não foi possível completar a operação. Tente novamente.'
}
