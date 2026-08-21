import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../services/authService'
import { extrairMensagemErro } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { alertaErro } from '../utils/alertas'

export function Login() {
  const navigate = useNavigate()
  const { salvarSessao } = useAuth()

  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setCarregando(true)

    try {
      const resposta = await login({ email, senha })
      salvarSessao(resposta.token, { nome: resposta.nome, email: resposta.email })
      navigate('/')
    } catch (e) {
      alertaErro('Não foi possível entrar', extrairMensagemErro(e))
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="login-container">
      <form className="login-form-section" onSubmit={handleSubmit}>
        <h1>Bem-vindo</h1>
        <p className="subtitle">Faça login para continuar</p>

        <div className="input-box">
          <i className="fa-solid fa-user" />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            placeholder="E-mail"
          />
        </div>

        <div className="input-box">
          <i className="fa-solid fa-lock" />
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
            autoComplete="current-password"
            placeholder="Senha"
          />
        </div>

        <div className="buttons">
          <button type="submit" disabled={carregando}>
            {carregando ? 'Entrando...' : 'Entrar'}
          </button>
          <button type="button" className="secundario" onClick={() => navigate('/registro')}>
            Cadastrar
          </button>
        </div>

        <Link to="/esqueci-senha" className="forgot">
          Esqueceu a senha?
        </Link>

        <p className="login-secondary-link">
          <Link to="/confirmar-email">Não recebeu o código de confirmação?</Link>
        </p>
      </form>
      <div className="login-image-section" />
    </div>
  )
}
