import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { esqueciSenha } from '../services/authService'
import { extrairMensagemErro } from '../services/api'
import { alertaErro, alertaSucesso } from '../utils/alertas'

export function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setCarregando(true)

    try {
      const resposta = await esqueciSenha({ email })
      await alertaSucesso('Verifique seu e-mail', resposta.message)
    } catch (e) {
      alertaErro('Não foi possível continuar', extrairMensagemErro(e))
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Esqueci minha senha</h1>
        <p className="auth-subtitle">
          Informe seu e-mail. Se ele estiver cadastrado, enviaremos um link para redefinir sua senha.
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

        <button type="submit" disabled={carregando}>
          {carregando ? 'Enviando...' : 'Enviar link de redefinição'}
        </button>

        <p className="auth-footer">
          <Link to="/login">Voltar para o login</Link>
        </p>
      </form>
    </div>
  )
}
