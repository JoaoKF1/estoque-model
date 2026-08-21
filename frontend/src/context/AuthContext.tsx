import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'

interface UsuarioLogado {
  nome: string
  email: string
}

interface AuthContextValue {
  usuario: UsuarioLogado | null
  autenticado: boolean
  salvarSessao: (token: string, usuario: UsuarioLogado) => void
  sair: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

const TOKEN_KEY = 'estoquemodel_token'
const USUARIO_KEY = 'estoquemodel_usuario'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioLogado | null>(() => {
    const salvo = localStorage.getItem(USUARIO_KEY)
    return salvo ? (JSON.parse(salvo) as UsuarioLogado) : null
  })

  const salvarSessao = (token: string, usuarioLogado: UsuarioLogado) => {
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USUARIO_KEY, JSON.stringify(usuarioLogado))
    setUsuario(usuarioLogado)
  }

  const sair = () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USUARIO_KEY)
    setUsuario(null)
  }

  const value = useMemo<AuthContextValue>(
    () => ({ usuario, autenticado: usuario !== null, salvarSessao, sair }),
    [usuario],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth precisa ser usado dentro de um <AuthProvider>')
  }
  return context
}
