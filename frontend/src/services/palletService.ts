import { api } from './api'
import type { Pallet, PalletRequest } from '../types/pallet'

export async function listarPallets(tipo?: string): Promise<Pallet[]> {
  const { data } = await api.get<Pallet[]>('/pallets', {
    params: tipo ? { tipo } : undefined,
  })
  return data
}

export async function criarPallet(dados: PalletRequest): Promise<Pallet> {
  const { data } = await api.post<Pallet>('/pallets', dados)
  return data
}

export async function atualizarPallet(id: number, dados: PalletRequest): Promise<Pallet> {
  const { data } = await api.put<Pallet>(`/pallets/${id}`, dados)
  return data
}

export async function deletarPallet(id: number): Promise<void> {
  await api.delete(`/pallets/${id}`)
}
