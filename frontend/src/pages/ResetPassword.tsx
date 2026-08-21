import { useState, type FormEvent } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { redefinirSenha } from '../services/authService'
import { extrairMensagemErro } from '../services/api'
import { alertaErro, alertaSucesso } from '../utils/alertas'

/**
 * Página acessada pelo link enviado no fluxo de "esqueci minha senha"
 * (ex.: /redefinir-senha?token=abc123). Veja ConsoleEmailService no
 * backend, que hoje apenas imprime esse link no console/log.
 */
export function ResetPassword() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const tokenDaUrl = searchParams.get('token') ?? ''

  const [token, setToken] = useState(tokenDaUrl)
  const [novaSenha, setNovaSenha] = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()

    if (novaSenha !== confirmarSenha) {
      alertaErro('As senhas não conferem', 'Digite a mesma senha nos dois campos.')
      return
    }

    setCarregando(true)
    try {
      const resposta = await redefinirSenha({ token, novaSenha })
      await alertaSucesso('Senha redefinida', resposta.message)
      navigate('/login')
    } catch (e) {
      alertaErro('Não foi possível redefinir', extrairMensagemErro(e))
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Redefinir senha</h1>
        <p className="auth-subtitle">Cole o token recebido e escolha uma nova senha.</p>

        <label htmlFor="token">Token</label>
        <div className="input-box">
          <i className="fa-solid fa-key" />
          <input
            id="token"
            type="text"
            value={token}
            onChange={(e) => setToken(e.target.value)}
            required
            placeholder="Token recebido"
          />
        </div>

        <label htmlFor="novaSenha">Nova senha</label>
        <div className="input-box">
          <i className="fa-solid fa-lock" />
          <input
            id="novaSenha"
            type="password"
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
            required
            minLength={6}
            autoComplete="new-password"
          />
        </div>

        <label htmlFor="confirmarSenha">Confirmar nova senha</label>
        <div className="input-box">
          <i className="fa-solid fa-lock" />
          <input
            id="confirmarSenha"
            type="password"
            value={confirmarSenha}
            onChange={(e) => setConfirmarSenha(e.target.value)}
            required
            autoComplete="new-password"
          />
        </div>

        <button type="submit" disabled={carregando}>
          {carregando ? 'Salvando...' : 'Redefinir senha'}
        </button>

        <p className="auth-footer">
          <Link to="/login">Voltar para o login</Link>
        </p>
      </form>
    </div>
  )
}
