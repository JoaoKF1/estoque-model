import { api } from './api'
import type {
  ConfirmEmailRequest,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  MessageResponse,
  RegisterRequest,
  ResendConfirmationRequest,
  ResetPasswordRequest,
} from '../types/auth'

export async function registrar(dados: RegisterRequest): Promise<MessageResponse> {
  const { data } = await api.post<MessageResponse>('/auth/register', dados)
  return data
}

export async function login(dados: LoginRequest): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>('/auth/login', dados)
  return data
}

export async function confirmarEmail(dados: ConfirmEmailRequest): Promise<MessageResponse> {
  const { data } = await api.post<MessageResponse>('/auth/confirm-email', dados)
  return data
}

export async function reenviarConfirmacao(dados: ResendConfirmationRequest): Promise<MessageResponse> {
  const { data } = await api.post<MessageResponse>('/auth/resend-confirmation', dados)
  return data
}

export async function esqueciSenha(dados: ForgotPasswordRequest): Promise<MessageResponse> {
  const { data } = await api.post<MessageResponse>('/auth/forgot-password', dados)
  return data
}

export async function redefinirSenha(dados: ResetPasswordRequest): Promise<MessageResponse> {
  const { data } = await api.post<MessageResponse>('/auth/reset-password', dados)
  return data
}
