import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { registrar } from '../services/authService'
import { extrairMensagemErro } from '../services/api'
import { alertaErro, alertaSucesso } from '../utils/alertas'

export function Register() {
  const navigate = useNavigate()

  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')
  const [cnpj, setCnpj] = useState('')
  const [telefone, setTelefone] = useState('')
  const [empresa, setEmpresa] = useState('')
  const [cargo, setCargo] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()

    if (senha !== confirmarSenha) {
      alertaErro('As senhas não conferem', 'Digite a mesma senha nos dois campos.')
      return
    }

    setCarregando(true)
    try {
      const resposta = await registrar({ nome, email, senha, cnpj, telefone, empresa, cargo })
      await alertaSucesso('Cadastro realizado', resposta.message)
      navigate('/confirmar-email', { state: { email } })
    } catch (e) {
      alertaErro('Não foi possível cadastrar', extrairMensagemErro(e))
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Criar conta</h1>
        <p className="auth-subtitle">Cadastre-se para acessar o Estoque Model</p>

        <label htmlFor="nome">Nome</label>
        <div className="input-box">
          <i className="fa-solid fa-user" />
          <input
            id="nome"
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            required
            minLength={2}
            placeholder="Seu nome completo"
          />
        </div>

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

        <label htmlFor="cnpj">CNPJ</label>
        <div className="input-box">
          <i className="fa-solid fa-id-card" />
          <input
            id="cnpj"
            type="text"
            value={cnpj}
            onChange={(e) => setCnpj(e.target.value)}
            required
            inputMode="numeric"
            placeholder="00.000.000/0000-00"
          />
        </div>

        <label htmlFor="telefone">Telefone</label>
        <div className="input-box">
          <i className="fa-solid fa-phone" />
          <input
            id="telefone"
            type="tel"
            value={telefone}
            onChange={(e) => setTelefone(e.target.value)}
            required
            placeholder="(00) 00000-0000"
          />
        </div>

        <label htmlFor="empresa">Empresa (opcional)</label>
        <div className="input-box">
          <i className="fa-solid fa-building" />
          <input
            id="empresa"
            type="text"
            value={empresa}
            onChange={(e) => setEmpresa(e.target.value)}
            placeholder="Nome da empresa"
          />
        </div>

        <label htmlFor="cargo">Cargo (opcional)</label>
        <div className="input-box">
          <i className="fa-solid fa-briefcase" />
          <input
            id="cargo"
            type="text"
            value={cargo}
            onChange={(e) => setCargo(e.target.value)}
            placeholder="Seu cargo na empresa"
          />
        </div>

        <label htmlFor="senha">Senha</label>
        <div className="input-box">
          <i className="fa-solid fa-lock" />
          <input
            id="senha"
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
            minLength={6}
            autoComplete="new-password"
            placeholder="Mínimo 6 caracteres"
          />
        </div>

        <label htmlFor="confirmarSenha">Confirmar senha</label>
        <div className="input-box">
          <i className="fa-solid fa-lock" />
          <input
            id="confirmarSenha"
            type="password"
            value={confirmarSenha}
            onChange={(e) => setConfirmarSenha(e.target.value)}
            required
            autoComplete="new-password"
            placeholder="Repita a senha"
          />
        </div>

        <button type="submit" disabled={carregando}>
          {carregando ? 'Cadastrando...' : 'Cadastrar'}
        </button>

        <p className="auth-footer">
          Já tem conta? <Link to="/login">Fazer login</Link>
        </p>
      </form>
    </div>
  )
}
