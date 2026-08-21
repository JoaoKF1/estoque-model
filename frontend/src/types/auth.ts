export interface RegisterRequest {
  nome: string
  email: string
  senha: string
  cnpj: string
  telefone: string
  empresa?: string
  cargo?: string
}

export interface ConfirmEmailRequest {
  email: string
  codigo: string
}

export interface ResendConfirmationRequest {
  email: string
}

export interface LoginRequest {
  email: string
  senha: string
}

export interface LoginResponse {
  token: string
  nome: string
  email: string
}

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token: string
  novaSenha: string
}

export interface MessageResponse {
  message: string
}

/** Formato de erro de validação retornado pelo Spring (campo -> mensagem). */
export type ValidationErrors = Record<string, string>
