export interface PalletRequest {
  tipo: string
  frenteMm: number
  profundidadeMm: number
  alturaMm: number
  pesoKg: number
  caracteristica?: string
}

export interface Pallet {
  id: number
  tipo: string
  frenteMm: number
  profundidadeMm: number
  alturaMm: number
  pesoKg: number
  caracteristica?: string
  criadoEm: string
}
