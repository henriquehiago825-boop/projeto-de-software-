const telas = document.querySelectorAll('.screen');
const navItems = document.querySelectorAll('.nav-item');
const lista = document.getElementById('lista-registros');
const total = document.getElementById('total-registros');
const form = document.getElementById('form-cadastro');
const feedback = document.getElementById('form-feedback');
const descricao = document.getElementById('descricao');
const modal = document.getElementById('modal-edicao');
let toastTimer;
let buscaTimer;

function navegar(id) {
  telas.forEach(t => t.classList.toggle('active', t.id === id));
  navItems.forEach(n => n.classList.toggle('active', n.dataset.go === id));
  if (id === 'consulta') carregarRegistros();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}
document.querySelectorAll('[data-go]').forEach(el => el.addEventListener('click', () => navegar(el.dataset.go)));
function aviso(mensagem) {
  const el = document.getElementById('toast'); el.textContent = mensagem; el.classList.add('show');
  clearTimeout(toastTimer); toastTimer = setTimeout(() => el.classList.remove('show'), 2800);
}
function escapar(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
function formatarData(data) {
  if (!data) return '';
  const [ano, mes, dia] = data.split('-'); return `${dia}/${mes}/${ano}`;
}
async function requisicao(url, opcoes = {}) {
  const resposta = await fetch(url, { ...opcoes, headers: { ...(opcoes.body ? {'Content-Type':'application/json'} : {}), ...(opcoes.headers || {}) } });
  if (resposta.status === 401) throw new Error('Acesso não autorizado. Informe as credenciais solicitadas pelo navegador e tente novamente.');
  const resultado = await resposta.json().catch(() => ({}));
  if (!resposta.ok) throw new Error(resultado.mensagem || 'Não foi possível concluir a operação.');
  return resultado;
}
descricao.addEventListener('input', () => document.getElementById('contador').textContent = `${descricao.value.length}/500`);
form.addEventListener('reset', () => setTimeout(() => { feedback.textContent = ''; feedback.className = 'feedback'; document.getElementById('contador').textContent = '0/500'; }, 0));
form.addEventListener('submit', async (event) => {
  event.preventDefault(); feedback.textContent = 'Salvando informação...'; feedback.className = 'feedback';
  const dados = Object.fromEntries(new FormData(form).entries());
  try {
    await requisicao('/api/informacoes', { method: 'POST', body: JSON.stringify(dados) });
    feedback.textContent = 'Cadastro realizado com sucesso!'; feedback.className = 'feedback success';
    form.reset(); aviso('Informação cadastrada com sucesso.');
  } catch (erro) { feedback.textContent = erro.message; feedback.className = 'feedback error'; }
});
async function carregarRegistros() {
  lista.innerHTML = '<div class="empty-state">Carregando registros...</div>';
  const params = new URLSearchParams();
  const busca = document.getElementById('busca').value.trim();
  const categoria = document.getElementById('filtro-categoria').value;
  if (busca) params.set('busca', busca); if (categoria) params.set('categoria', categoria);
  try {
    const registros = await requisicao(`/api/informacoes?${params.toString()}`);
    total.textContent = `${registros.length} ${registros.length === 1 ? 'registro encontrado' : 'registros encontrados'}`;
    if (!registros.length) { lista.innerHTML = '<div class="empty-state">Nenhuma informação encontrada. Experimente alterar os filtros ou cadastre um novo registro.</div>'; return; }
    lista.innerHTML = registros.map(item => `<article class="record"><div class="record-icon" aria-hidden="true">▤</div><div class="record-content"><h3>${escapar(item.titulo)}</h3><p>${escapar(item.descricao)}</p></div><div class="record-meta"><span class="tag">${escapar(item.categoria)}</span><time datetime="${escapar(item.dataReferencia)}">${formatarData(item.dataReferencia)}</time><button class="edit-button" type="button" data-edit="${Number(item.id)}">Editar</button></div></article>`).join('');
  } catch (erro) { total.textContent = 'Consulta indisponível'; lista.innerHTML = `<div class="empty-state">${escapar(erro.message)}</div>`; }
}
lista.addEventListener('click', async (event) => {
  const botao = event.target.closest('[data-edit]');
  if (!botao) return;
  try {
    const registros = await requisicao('/api/informacoes');
    const item = registros.find(registro => Number(registro.id) === Number(botao.dataset.edit));
    if (!item) throw new Error('Registro não encontrado. Atualize a consulta.');
    document.getElementById('edit-id').value = item.id;
    document.getElementById('edit-categoria').value = item.categoria;
    document.getElementById('edit-titulo').value = item.titulo;
    document.getElementById('edit-descricao').value = item.descricao;
    document.getElementById('edit-data').value = item.dataReferencia;
    document.getElementById('edit-feedback').textContent = '';
    modal.showModal();
  } catch (erro) { aviso(erro.message); }
});
function fecharModal() { modal.close(); }
document.getElementById('fechar-modal').addEventListener('click', fecharModal);
document.getElementById('cancelar-edicao').addEventListener('click', fecharModal);
modal.addEventListener('click', event => { if (event.target === modal) fecharModal(); });
document.getElementById('form-edicao').addEventListener('submit', async event => {
  event.preventDefault();
  const id = document.getElementById('edit-id').value;
  const dados = {
    categoria: document.getElementById('edit-categoria').value,
    titulo: document.getElementById('edit-titulo').value,
    descricao: document.getElementById('edit-descricao').value,
    dataReferencia: document.getElementById('edit-data').value
  };
  const retorno = document.getElementById('edit-feedback'); retorno.textContent = 'Salvando alterações...'; retorno.className = 'feedback';
  try {
    await requisicao(`/api/informacoes/${id}`, { method: 'PUT', body: JSON.stringify(dados) });
    fecharModal(); aviso('Informação atualizada com sucesso.'); carregarRegistros();
  } catch (erro) { retorno.textContent = erro.message; retorno.className = 'feedback error'; }
});
document.getElementById('busca').addEventListener('input', () => { clearTimeout(buscaTimer); buscaTimer = setTimeout(carregarRegistros, 250); });
document.getElementById('filtro-categoria').addEventListener('change', carregarRegistros);
document.getElementById('limpar-filtros').addEventListener('click', () => { document.getElementById('busca').value = ''; document.getElementById('filtro-categoria').value = ''; carregarRegistros(); });
