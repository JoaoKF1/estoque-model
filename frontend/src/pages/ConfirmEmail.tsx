import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { confirmarEmail, reenviarConfirmacao } from '../services/authService'
import { extrairMensagemErro } from '../services/api'
import { alertaErro, alertaSucesso } from '../utils/alertas'

interface LocationState {
  email?: string
}

/**
 * RF02 - Confirmar e-mail. Chega aqui logo após o cadastro (RF01), com o
 * e-mail já preenchido via state da navegação; também pode ser acessada
 * direto (ex.: link "não recebeu o código?" no Login), com o campo em
 * branco.
 */
export function ConfirmEmail() {
  const location = useLocation()
  const navigate = useNavigate()
  const emailInicial = (location.state as LocationState | null)?.email ?? ''

  const [email, setEmail] = useState(emailInicial)
  const [codigo, setCodigo] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [reenviando, setReenviando] = useState(false)

  async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setCarregando(true)

    try {
      const resposta = await confirmarEmail({ email, codigo })
      await alertaSucesso('E-mail confirmado', resposta.message)
      navigate('/login')
    } catch (e) {
      alertaErro('Não foi possível confirmar', extrairMensagemErro(e))
    } finally {
      setCarregando(false)
    }
  }

  async function handleReenviar() {
    setReenviando(true)
    try {
      const resposta = await reenviarConfirmacao({ email })
      await alertaSucesso('Código reenviado', resposta.message)
    } catch (e) {
      alertaErro('Não foi possível reenviar', extrairMensagemErro(e))
    } finally {
      setReenviando(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Confirmar e-mail</h1>
        <p className="auth-subtitle">
          Digite o código de 6 dígitos que enviamos para o seu e-mail para ativar sua conta.
        </p>

        <label htmlFor="email">E-mail</label>
        <div className="input-box">
          <i className="fa-solid fa-envelope" />
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            placeholder="seu@email.com"
          />
        </div>

        <label htmlFor="codigo">Código de confirmação</label>
        <div className="input-box">
          <i className="fa-solid fa-key" />
          <input
            id="codigo"
            type="text"
            inputMode="numeric"
            maxLength={6}
            value={codigo}
            onChange={(e) => setCodigo(e.target.value)}
            required
            placeholder="000000"
          />
        </div>

        <button type="submit" disabled={carregando}>
          {carregando ? 'Confirmando...' : 'Confirmar'}
        </button>

        <button type="button" className="secundario" onClick={handleReenviar} disabled={reenviando || !email}>
          {reenviando ? 'Enviando...' : 'Reenviar código'}
        </button>

        <p className="auth-footer">
          <Link to="/login">Voltar para o login</Link>
        </p>
      </form>
    </div>
  )
}
