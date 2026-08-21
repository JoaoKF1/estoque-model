import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * Tela principal (RF03 - Fazer login satisfeito: só chega aqui quem fez
 * login e já confirmou o e-mail). Os próximos passos do projeto entram
 * aqui: RF05 (Gerenciar Empilhadeira) e RF07 (Fazer Simulação) - ver o
 * Plano Técnico para a ordem das sprints. RF04 (Gerenciar Pallet) já
 * está implementada em /pallets.
 */
export function Home() {
  const { usuario, sair } = useAuth()

  return (
    <div className="home-page">
      <header className="home-header">
        <h1>Estoque Model</h1>
        <button onClick={sair}>Sair</button>
      </header>

      <main>
        <p>Bem-vindo(a), {usuario?.nome}!</p>
        <p className="home-hint">
          Comece cadastrando os pallets que sua empresa utiliza — eles serão usados depois na
          simulação (RF07) para calcular a estrutura de armazenagem recomendada.
        </p>
        <Link to="/pallets" className="home-cta">
          <i className="fa-solid fa-pallet" /> Gerenciar Pallet
        </Link>
      </main>
    </div>
  )
}
