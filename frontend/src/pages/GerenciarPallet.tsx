import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { atualizarPallet, criarPallet, deletarPallet, listarPallets } from '../services/palletService'
import { extrairMensagemErro } from '../services/api'
import { alertaErro, alertaSucesso } from '../utils/alertas'
import type { Pallet } from '../types/pallet'

const FORM_INICIAL = {
  tipo: '',
  frenteMm: '',
  profundidadeMm: '',
  alturaMm: '',
  pesoKg: '',
  caracteristica: '',
}

/**
 * RF04 - Gerenciar Pallet. Layout inspirado no painel esquerdo de
 * simulacao.html (protótipo estático do grupo), com uma lista à direita
 * no lugar dos campos de simulação (isso entra depois, na RF07).
 */
export function GerenciarPallet() {
  const navigate = useNavigate()
  const { sair } = useAuth()

  const [pallets, setPallets] = useState<Pallet[]>([])
  const [carregandoLista, setCarregandoLista] = useState(true)
  const [filtroTipo, setFiltroTipo] = useState('')

  const [form, setForm] = useState(FORM_INICIAL)
  const [selecionado, setSelecionado] = useState<Pallet | null>(null)
  const [salvando, setSalvando] = useState(false)

  async function carregar(tipo?: string) {
    setCarregandoLista(true)
    try {
      setPallets(await listarPallets(tipo))
    } catch (e) {
      alertaErro('Não foi possível carregar', extrairMensagemErro(e))
    } finally {
      setCarregandoLista(false)
    }
  }

  useEffect(() => {
    carregar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function limparForm() {
    setForm(FORM_INICIAL)
    setSelecionado(null)
  }

  function selecionar(pallet: Pallet) {
    setSelecionado(pallet)
    setForm({
      tipo: pallet.tipo,
      frenteMm: String(pallet.frenteMm),
      profundidadeMm: String(pallet.profundidadeMm),
      alturaMm: String(pallet.alturaMm),
      pesoKg: String(pallet.pesoKg),
      caracteristica: pallet.caracteristica ?? '',
    })
  }

  function handlePesquisar() {
    carregar(filtroTipo)
  }

  async function handleSalvar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setSalvando(true)

    const dados = {
      tipo: form.tipo,
      frenteMm: Number(form.frenteMm),
      profundidadeMm: Number(form.profundidadeMm),
      alturaMm: Number(form.alturaMm),
      pesoKg: Number(form.pesoKg),
      caracteristica: form.caracteristica.trim() || undefined,
    }

    try {
      if (selecionado) {
        await atualizarPallet(selecionado.id, dados)
        await alertaSucesso('Pallet atualizado', 'As alterações foram salvas.')
      } else {
        await criarPallet(dados)
        await alertaSucesso('Pallet cadastrado', 'O pallet foi salvo com sucesso.')
      }
      limparForm()
      await carregar(filtroTipo)
    } catch (e) {
      alertaErro('Não foi possível salvar', extrairMensagemErro(e))
    } finally {
      setSalvando(false)
    }
  }

  async function handleDeletar(pallet: Pallet) {
    try {
      await deletarPallet(pallet.id)
      if (selecionado?.id === pallet.id) {
        limparForm()
      }
      await carregar(filtroTipo)
    } catch (e) {
      alertaErro('Não foi possível apagar', extrairMensagemErro(e))
    }
  }

  return (
    <div className="pallet-page">
      <header className="pallet-topbar">
        <h1>Gerenciar Pallet</h1>
        <div className="top-actions">
          <button type="button" className="top-btn" onClick={() => navigate('/')}>
            <i className="fa-solid fa-house" /> Início
          </button>
          <button type="button" className="top-btn" onClick={sair}>
            <i className="fa-solid fa-right-from-bracket" /> Sair
          </button>
        </div>
      </header>

      <div className="pallet-container">
        <form className="pallet-form-panel" onSubmit={handleSalvar}>
          <h3>
            <i className="fa-solid fa-pallet" /> Pallets
          </h3>

          <label htmlFor="filtroTipo">Pesquisar por tipo</label>
          <input
            id="filtroTipo"
            type="text"
            value={filtroTipo}
            onChange={(e) => setFiltroTipo(e.target.value)}
            placeholder="Ex.: PBR"
          />
          <div className="pallet-actions">
            <button type="button" className="btn" onClick={handlePesquisar}>
              Pesquisar
            </button>
          </div>

          <label htmlFor="tipo">Tipo</label>
          <input
            id="tipo"
            type="text"
            value={form.tipo}
            onChange={(e) => setForm({ ...form, tipo: e.target.value })}
            required
            placeholder="Ex.: PBR, Europeu..."
          />

          <label htmlFor="frenteMm">Frente (mm)</label>
          <input
            id="frenteMm"
            type="number"
            min="0.01"
            step="0.01"
            value={form.frenteMm}
            onChange={(e) => setForm({ ...form, frenteMm: e.target.value })}
            required
          />

          <label htmlFor="profundidadeMm">Profundidade (mm)</label>
          <input
            id="profundidadeMm"
            type="number"
            min="0.01"
            step="0.01"
            value={form.profundidadeMm}
            onChange={(e) => setForm({ ...form, profundidadeMm: e.target.value })}
            required
          />

          <label htmlFor="alturaMm">Altura (mm)</label>
          <input
            id="alturaMm"
            type="number"
            min="0.01"
            step="0.01"
            value={form.alturaMm}
            onChange={(e) => setForm({ ...form, alturaMm: e.target.value })}
            required
          />

          <label htmlFor="pesoKg">Peso (kg)</label>
          <input
            id="pesoKg"
            type="number"
            min="0.01"
            step="0.01"
            value={form.pesoKg}
            onChange={(e) => setForm({ ...form, pesoKg: e.target.value })}
            required
          />

          <label htmlFor="caracteristica">Característica (opcional)</label>
          <input
            id="caracteristica"
            type="text"
            maxLength={120}
            value={form.caracteristica}
            onChange={(e) => setForm({ ...form, caracteristica: e.target.value })}
            placeholder="Ex.: madeira, bom estado..."
          />

          <div className="pallet-actions">
            <button
              type="button"
              className="btn"
              onClick={() => selecionado && handleDeletar(selecionado)}
              disabled={!selecionado}
            >
              Apagar
            </button>
            <button type="submit" className="btn salvar" disabled={salvando}>
              {salvando ? 'Salvando...' : selecionado ? 'Alterar' : 'Salvar'}
            </button>
          </div>

          {selecionado && (
            <button type="button" className="pallet-limpar" onClick={limparForm}>
              Cancelar edição / novo pallet
            </button>
          )}
        </form>

        <section className="pallet-list-panel">
          <h2>Pallets cadastrados</h2>

          {carregandoLista ? (
            <p className="pallet-empty">Carregando...</p>
          ) : pallets.length === 0 ? (
            <p className="pallet-empty">
              Nenhum pallet cadastrado ainda. Use o formulário ao lado para criar o primeiro.
            </p>
          ) : (
            <table className="pallet-table">
              <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Frente (mm)</th>
                  <th>Profundidade (mm)</th>
                  <th>Altura (mm)</th>
                  <th>Peso (kg)</th>
                  <th>Característica</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {pallets.map((pallet) => (
                  <tr key={pallet.id} className={selecionado?.id === pallet.id ? 'selecionado' : ''}>
                    <td>{pallet.tipo}</td>
                    <td>{pallet.frenteMm}</td>
                    <td>{pallet.profundidadeMm}</td>
                    <td>{pallet.alturaMm}</td>
                    <td>{pallet.pesoKg}</td>
                    <td>{pallet.caracteristica ?? '—'}</td>
                    <td className="pallet-row-actions">
                      <button type="button" title="Editar" onClick={() => selecionar(pallet)}>
                        <i className="fa-solid fa-pen" />
                      </button>
                      <button type="button" title="Apagar" onClick={() => handleDeletar(pallet)}>
                        <i className="fa-solid fa-trash" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </div>
    </div>
  )
}
