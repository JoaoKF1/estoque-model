import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Redireciona para /login caso não haja usuário autenticado. */
export function RotaProtegida({ children }: { children: ReactNode }) {
  const { autenticado } = useAuth()

  if (!autenticado) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}
