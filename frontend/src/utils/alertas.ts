import Swal from 'sweetalert2'

/**
 * Wrappers finos sobre o SweetAlert2, no mesmo padrão visual usado no
 * protótipo estático do grupo (index.html/app.js) para a tela de
 * Gerenciar Pallet - reaproveitado aqui nas telas de autenticação.
 */

export function alertaSucesso(titulo: string, texto?: string) {
  return Swal.fire({
    title: titulo,
    text: texto,
    icon: 'success',
    confirmButtonText: 'OK',
    confirmButtonColor: '#ff5a2e',
  })
}

export function alertaErro(titulo: string, texto?: string) {
  return Swal.fire({
    title: titulo,
    text: texto,
    icon: 'error',
    confirmButtonText: 'OK',
    confirmButtonColor: '#ff5a2e',
  })
}
