const estado = {
    usuario: null,
    rotas: [],
    ocorrencias: [],
    rotaSelecionada: null,
    mapa: null,
    camadaRotas: null,
    camadaOcorrencias: null,
    camadaDesenho: null,
    camadaMinhaLoc: null,
    camadaTracada: null,
    trechosNovaRota: [],
    cliqueTemp: null,
    panelAtivo: 'rotas',
    coordenadaAtual: null,
    marcadorMinhaLoc: null,
    rotaTracada: null
};

const ICONES = {
    trecho: L.divIcon({ className: 'marker-trecho', html: '<div class="pin-trecho"></div>', iconSize: [14,14], iconAnchor: [7,7] }),
    ocorrencia: L.divIcon({ className: 'marker-oco', html: '<div class="pin-oco"></div>', iconSize: [16,16], iconAnchor: [8,8] }),
    novo: L.divIcon({ className: 'marker-novo', html: '<div class="pin-novo"></div>', iconSize: [18,18], iconAnchor: [9,9] }),
    minhaLoc: L.divIcon({ className: 'marker-eu', html: '<div class="pin-eu"></div>', iconSize: [22,22], iconAnchor: [11,11] }),
    origem: L.divIcon({ className: 'marker-origem', html: '<div class="pin-origem">A</div>', iconSize: [26,26], iconAnchor: [13,13] }),
    destino: L.divIcon({ className: 'marker-destino', html: '<div class="pin-destino">B</div>', iconSize: [26,26], iconAnchor: [13,13] })
};

async function api(url, opts = {}) {
    const resp = await fetch(url, {
        headers: { 'Content-Type': 'application/json' },
        ...opts
    });
    if (resp.status === 401) {
        mostrarLogin();
        throw new Error('nao autenticado');
    }
    const txt = await resp.text();
    const data = txt ? JSON.parse(txt) : null;
    if (!resp.ok) throw new Error((data && data.mensagem) || 'erro');
    return data;
}

function trocarAba(aba) {
    document.getElementById('form-login').classList.toggle('oculto', aba !== 'login');
    document.getElementById('form-cadastro').classList.toggle('oculto', aba !== 'cadastro');
    document.getElementById('aviso-login').textContent = '';
}

function mostrarAviso(msg) {
    document.getElementById('aviso-login').textContent = msg || '';
}

async function entrar() {
    const login = document.getElementById('login-usr').value.trim();
    const senha = document.getElementById('login-sen').value;
    if (!login || !senha) { mostrarAviso('preencha login e senha'); return; }
    try {
        const u = await api('/api/login', { method: 'POST', body: JSON.stringify({ login, senha }) });
        estado.usuario = u;
        iniciarApp();
    } catch (e) {
        mostrarAviso(e.message);
    }
}

async function cadastrar() {
    const login = document.getElementById('cad-usr').value.trim();
    const senha = document.getElementById('cad-sen').value;
    const nome = document.getElementById('cad-nome').value.trim();
    if (!login || !senha || !nome) { mostrarAviso('preencha todos os campos'); return; }
    try {
        await api('/api/cadastro', { method: 'POST', body: JSON.stringify({ login, senha, nome }) });
        mostrarAviso('conta criada, faça login');
        trocarAba('login');
    } catch (e) {
        mostrarAviso(e.message);
    }
}

async function sair() {
    try { await api('/api/sair', { method: 'POST' }); } catch (e) {}
    estado.usuario = null;
    mostrarLogin();
}

function mostrarLogin() {
    document.getElementById('tela-app').classList.add('oculto');
    document.getElementById('tela-login').classList.remove('oculto');
}

function mostrarApp() {
    document.getElementById('tela-login').classList.add('oculto');
    document.getElementById('tela-app').classList.remove('oculto');
}

function iniciarApp() {
    mostrarApp();
    document.getElementById('nome-usuario').textContent = estado.usuario.nome;
    if (!estado.mapa) iniciarMapa();
    setTimeout(() => estado.mapa.invalidateSize(), 100);
    recarregarTudo();
}

function iniciarMapa() {
    estado.mapa = L.map('mapa').setView([-23.5505, -46.6333], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap',
        maxZoom: 19
    }).addTo(estado.mapa);
    estado.camadaRotas = L.layerGroup().addTo(estado.mapa);
    estado.camadaOcorrencias = L.layerGroup().addTo(estado.mapa);
    estado.camadaDesenho = L.layerGroup().addTo(estado.mapa);
    estado.camadaMinhaLoc = L.layerGroup().addTo(estado.mapa);
    estado.camadaTracada = L.layerGroup().addTo(estado.mapa);
    estado.mapa.on('click', aoClicarMapa);
}

function aoClicarMapa(ev) {
    if (estado.panelAtivo !== 'nova-rota') return;
    estado.cliqueTemp = ev.latlng;
    abrirDialogoTrecho();
}

function abrirDialogoTrecho() {
    document.getElementById('dt-via').value = '';
    document.getElementById('dt-bairro').value = '';
    document.getElementById('dt-km').value = '';
    document.getElementById('dlg-trecho').classList.remove('oculto');
    setTimeout(() => document.getElementById('dt-via').focus(), 50);
}

function fecharDialogoTrecho() {
    document.getElementById('dlg-trecho').classList.add('oculto');
    estado.cliqueTemp = null;
}

function confirmarTrecho() {
    const via = document.getElementById('dt-via').value.trim();
    const bairro = document.getElementById('dt-bairro').value.trim();
    const km = parseFloat(document.getElementById('dt-km').value);
    if (!via || !bairro || !km || km <= 0) { alert('preencha os campos corretamente'); return; }
    estado.trechosNovaRota.push({
        nomeVia: via,
        bairro: bairro,
        distanciaKm: km,
        lat: estado.cliqueTemp.lat,
        lng: estado.cliqueTemp.lng
    });
    fecharDialogoTrecho();
    renderizarDesenhoNovaRota();
    renderizarListaNovaRota();
}

function renderizarListaNovaRota() {
    const ul = document.getElementById('nr-trechos');
    ul.innerHTML = '';
    estado.trechosNovaRota.forEach((t, i) => {
        const li = document.createElement('li');
        li.textContent = `${i+1}. ${t.nomeVia} / ${t.bairro} (${t.distanciaKm}km)`;
        ul.appendChild(li);
    });
}

function renderizarDesenhoNovaRota() {
    estado.camadaDesenho.clearLayers();
    const pts = estado.trechosNovaRota.map(t => [t.lat, t.lng]);
    pts.forEach(p => L.marker(p, { icon: ICONES.novo }).addTo(estado.camadaDesenho));
    if (pts.length > 1) {
        L.polyline(pts, { color: '#f59e0b', weight: 4, opacity: 0.7, dashArray: '6 6' }).addTo(estado.camadaDesenho);
    }
}

async function salvarNovaRota() {
    const nome = document.getElementById('nr-nome').value.trim();
    const horario = document.getElementById('nr-horario').value;
    if (!nome) { alert('informe o nome'); return; }
    if (estado.trechosNovaRota.length === 0) { alert('adicione ao menos um trecho'); return; }
    try {
        await api('/api/rotas', {
            method: 'POST',
            body: JSON.stringify({ nome, horario, trechos: estado.trechosNovaRota })
        });
        cancelarNovaRota();
        mostrarPanel('rotas');
        recarregarTudo();
    } catch (e) {
        alert('erro: ' + e.message);
    }
}

function cancelarNovaRota() {
    estado.trechosNovaRota = [];
    document.getElementById('nr-nome').value = '';
    renderizarListaNovaRota();
    renderizarDesenhoNovaRota();
}

function mostrarPanel(nome) {
    estado.panelAtivo = nome;
    document.querySelectorAll('aside .panel').forEach(p => p.classList.add('oculto'));
    document.getElementById('panel-' + nome).classList.remove('oculto');
    document.querySelectorAll('nav button').forEach(b => b.classList.toggle('ativo', b.dataset.panel === nome));
    estado.mapa.getContainer().style.cursor = nome === 'nova-rota' ? 'crosshair' : '';
    if (nome === 'nova-ocorrencia') atualizarSelectTrechos();
}

async function recarregarTudo() {
    await carregarRotas();
    await carregarOcorrencias();
    desenharMapa();
    atualizarSelectTrechos();
}

async function carregarRotas() {
    estado.rotas = await api('/api/rotas?escopo=todas');
    renderizarRotas();
}

async function carregarOcorrencias() {
    estado.ocorrencias = await api('/api/ocorrencias');
}

function renderizarRotas() {
    const filtro = document.getElementById('busca-rota').value.toLowerCase();
    const ul = document.getElementById('lista-rotas');
    ul.innerHTML = '';
    const minhas = estado.rotas.filter(r => r.dono === estado.usuario.login);
    const outras = estado.rotas.filter(r => r.dono !== estado.usuario.login);
    const render = (lista, titulo) => {
        if (lista.length === 0) return;
        const h = document.createElement('li');
        h.className = 'cabecalho';
        h.textContent = titulo;
        ul.appendChild(h);
        lista.filter(r => r.nome.toLowerCase().includes(filtro)).forEach(r => {
            const li = document.createElement('li');
            li.textContent = `${r.nome} (${r.trechos.length} trechos)`;
            if (estado.rotaSelecionada && estado.rotaSelecionada.id === r.id) li.classList.add('selecionado');
            li.onclick = () => selecionarRota(r);
            ul.appendChild(li);
        });
    };
    render(minhas, 'minhas');
    render(outras, 'outras');
}

async function selecionarRota(r) {
    estado.rotaSelecionada = r;
    renderizarRotas();
    const det = document.getElementById('detalhe-rota');
    det.classList.remove('oculto');
    document.getElementById('dr-nome').textContent = r.nome;
    const km = r.trechos.reduce((s, t) => s + t.distanciaKm, 0);
    document.getElementById('dr-info').textContent = `${km.toFixed(2)} km · horário ${r.horarioPrevisto} · dono ${r.dono}`;
    try {
        const idx = await api(`/api/rotas/${r.id}/indice`);
        const div = document.getElementById('dr-indice');
        div.className = 'indice ' + idx.classificacao;
        div.textContent = `score ${idx.score.toFixed(1)} — classe ${idx.classificacao}`;
    } catch (e) {}
    centralizarEmRota(r);
    desenharMapa();
}

function centralizarEmRota(r) {
    if (r.trechos.length === 0) return;
    const bounds = L.latLngBounds(r.trechos.map(t => [t.lat, t.lng]));
    estado.mapa.fitBounds(bounds, { padding: [50, 50], maxZoom: 16 });
}

async function excluirRotaSelecionada() {
    if (!estado.rotaSelecionada) return;
    if (!confirm('excluir rota "' + estado.rotaSelecionada.nome + '"?')) return;
    try {
        await api('/api/rotas/' + estado.rotaSelecionada.id, { method: 'DELETE' });
        estado.rotaSelecionada = null;
        document.getElementById('detalhe-rota').classList.add('oculto');
        recarregarTudo();
    } catch (e) {
        alert('erro: ' + e.message);
    }
}

function atualizarSelectTrechos() {
    const sel = document.getElementById('oc-trecho');
    if (!sel) return;
    sel.innerHTML = '';
    estado.rotas.forEach(r => {
        r.trechos.forEach(t => {
            const opt = document.createElement('option');
            opt.value = t.id;
            opt.textContent = `${r.nome} · ${t.nomeVia}`;
            sel.appendChild(opt);
        });
    });
}

async function salvarOcorrencia() {
    const trechoId = document.getElementById('oc-trecho').value;
    const tipo = document.getElementById('oc-tipo').value;
    const data = document.getElementById('oc-data').value;
    const descricao = document.getElementById('oc-desc').value.trim();
    if (!trechoId || !data) { alert('preencha trecho e data'); return; }
    try {
        await api('/api/ocorrencias', {
            method: 'POST',
            body: JSON.stringify({ trechoId, tipo, data, descricao })
        });
        document.getElementById('oc-desc').value = '';
        mostrarPanel('rotas');
        recarregarTudo();
    } catch (e) {
        alert('erro: ' + e.message);
    }
}

function trechoPorId(id) {
    for (const r of estado.rotas) {
        for (const t of r.trechos) if (t.id === id) return { rota: r, trecho: t };
    }
    return null;
}

function desenharMapa() {
    estado.camadaRotas.clearLayers();
    estado.camadaOcorrencias.clearLayers();

    estado.rotas.forEach(r => {
        const selecionada = estado.rotaSelecionada && estado.rotaSelecionada.id === r.id;
        const cor = selecionada ? '#1e3a5f' : '#94a3b8';
        const peso = selecionada ? 5 : 3;
        const pts = r.trechos.map(t => [t.lat, t.lng]);
        if (pts.length > 1) {
            L.polyline(pts, { color: cor, weight: peso, opacity: 0.8 }).addTo(estado.camadaRotas);
        }
        r.trechos.forEach(t => {
            L.marker([t.lat, t.lng], { icon: ICONES.trecho })
                .bindPopup(`<b>${r.nome}</b><br>${t.nomeVia} / ${t.bairro}<br>${t.distanciaKm} km<br><small>id: ${t.id}</small>`)
                .addTo(estado.camadaRotas);
        });
    });

    estado.ocorrencias.forEach(o => {
        const ref = trechoPorId(o.trechoId);
        if (!ref) return;
        const t = ref.trecho;
        L.marker([t.lat, t.lng], { icon: ICONES.ocorrencia })
            .bindPopup(`<b>${o.tipo}</b><br>${o.data}<br>${o.descricao || '(sem descrição)'}<br><small>por ${o.reportadoPor}</small>`)
            .addTo(estado.camadaOcorrencias);
    });
}

async function carregarContagem() {
    const inicio = document.getElementById('rel-inicio').value;
    const fim = document.getElementById('rel-fim').value;
    let url = '/api/relatorios/contagem';
    if (inicio && fim) url += `?inicio=${inicio}&fim=${fim}`;
    try {
        const mapa = await api(url);
        const saida = document.getElementById('rel-saida');
        const entradas = Object.entries(mapa);
        if (entradas.length === 0) { saida.textContent = 'nenhuma ocorrência no período'; return; }
        saida.textContent = entradas.map(([k, v]) => `${k}: ${v}`).join('\n');
    } catch (e) {
        alert('erro: ' + e.message);
    }
}

async function verificarSessao() {
    try {
        const u = await api('/api/sessao');
        estado.usuario = u;
        iniciarApp();
    } catch (e) {
        mostrarLogin();
    }
}

function localizarAgora() {
    if (!navigator.geolocation) { alert('geolocalização não suportada neste navegador'); return; }
    navigator.geolocation.getCurrentPosition(
        pos => {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;
            estado.coordenadaAtual = { lat, lng };
            estado.camadaMinhaLoc.clearLayers();
            estado.marcadorMinhaLoc = L.marker([lat, lng], { icon: ICONES.minhaLoc })
                .bindPopup('você está aqui')
                .addTo(estado.camadaMinhaLoc);
            L.circle([lat, lng], { radius: pos.coords.accuracy, color: '#2563eb', weight: 1, fillOpacity: 0.1 })
                .addTo(estado.camadaMinhaLoc);
            estado.mapa.setView([lat, lng], 16);
        },
        err => alert('erro de localização: ' + err.message),
        { enableHighAccuracy: true, timeout: 10000 }
    );
}

function usarMinhaLocalizacao() {
    if (!estado.coordenadaAtual) {
        localizarAgora();
        setTimeout(() => {
            if (estado.coordenadaAtual) {
                document.getElementById('tr-origem').value = `${estado.coordenadaAtual.lat.toFixed(6)}, ${estado.coordenadaAtual.lng.toFixed(6)}`;
            }
        }, 800);
    } else {
        document.getElementById('tr-origem').value = `${estado.coordenadaAtual.lat.toFixed(6)}, ${estado.coordenadaAtual.lng.toFixed(6)}`;
    }
}

async function geocodificar(texto) {
    const coord = parseCoord(texto);
    if (coord) return { lat: coord.lat, lng: coord.lng, nome: `${coord.lat}, ${coord.lng}` };
    const url = `https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(texto)}`;
    const resp = await fetch(url, { headers: { 'Accept-Language': 'pt-BR' } });
    const data = await resp.json();
    if (!data || data.length === 0) throw new Error('endereço não encontrado: ' + texto);
    return { lat: parseFloat(data[0].lat), lng: parseFloat(data[0].lon), nome: data[0].display_name };
}

function parseCoord(texto) {
    const m = texto.trim().match(/^(-?\d+\.?\d*)[,\s]+(-?\d+\.?\d*)$/);
    if (!m) return null;
    return { lat: parseFloat(m[1]), lng: parseFloat(m[2]) };
}

function statusTracar(msg, erro) {
    const el = document.getElementById('tr-status');
    if (!msg) { el.classList.add('oculto'); return; }
    el.classList.remove('oculto');
    el.textContent = msg;
    el.style.background = erro ? '#fecaca' : '#eef2f7';
    el.style.color = erro ? '#991b1b' : '#475569';
}

async function tracarRota() {
    const origemTxt = document.getElementById('tr-origem').value.trim();
    const destinoTxt = document.getElementById('tr-destino').value.trim();
    if (!origemTxt || !destinoTxt) { statusTracar('preencha origem e destino', true); return; }
    statusTracar('buscando endereços...', false);
    try {
        const origem = await geocodificar(origemTxt);
        await new Promise(r => setTimeout(r, 800));
        const destino = await geocodificar(destinoTxt);
        statusTracar('calculando rota...', false);
        const url = `https://router.project-osrm.org/route/v1/foot/${origem.lng},${origem.lat};${destino.lng},${destino.lat}?overview=full&geometries=geojson&steps=true`;
        const resp = await fetch(url);
        const data = await resp.json();
        if (!data.routes || data.routes.length === 0) throw new Error('nenhuma rota encontrada');
        const rota = data.routes[0];
        estado.rotaTracada = {
            origem, destino,
            geometria: rota.geometry.coordinates,
            passos: rota.legs[0].steps,
            distanciaTotal: rota.distance / 1000,
            duracao: rota.duration / 60
        };
        desenharRotaTracada();
        statusTracar(null);
        const resumo = document.getElementById('tr-resumo');
        resumo.classList.remove('oculto');
        document.getElementById('tr-info').textContent =
            `${estado.rotaTracada.distanciaTotal.toFixed(2)} km · ~${Math.round(estado.rotaTracada.duracao)} min a pé · ${estado.rotaTracada.passos.length} trechos`;
    } catch (e) {
        statusTracar('erro: ' + e.message, true);
    }
}

function desenharRotaTracada() {
    estado.camadaTracada.clearLayers();
    const r = estado.rotaTracada;
    if (!r) return;
    const latlngs = r.geometria.map(c => [c[1], c[0]]);
    L.polyline(latlngs, { color: '#059669', weight: 5, opacity: 0.85 }).addTo(estado.camadaTracada);
    L.marker([r.origem.lat, r.origem.lng], { icon: ICONES.origem }).bindPopup(r.origem.nome).addTo(estado.camadaTracada);
    L.marker([r.destino.lat, r.destino.lng], { icon: ICONES.destino }).bindPopup(r.destino.nome).addTo(estado.camadaTracada);
    estado.mapa.fitBounds(L.latLngBounds(latlngs), { padding: [60, 60] });
}

function limparRotaTracada() {
    estado.rotaTracada = null;
    estado.camadaTracada.clearLayers();
    document.getElementById('tr-resumo').classList.add('oculto');
    document.getElementById('tr-nome').value = '';
    statusTracar(null);
}

async function salvarRotaTracada() {
    if (!estado.rotaTracada) return;
    const nome = document.getElementById('tr-nome').value.trim();
    const horario = document.getElementById('tr-horario').value;
    if (!nome) { alert('informe um nome pra rota'); return; }
    const trechos = estado.rotaTracada.passos
        .filter(s => s.distance > 10)
        .map(s => ({
            nomeVia: (s.name && s.name.trim()) || 'sem nome',
            bairro: '—',
            distanciaKm: Math.max(0.01, s.distance / 1000),
            lat: s.maneuver.location[1],
            lng: s.maneuver.location[0]
        }));
    if (trechos.length === 0) { alert('rota muito curta pra salvar'); return; }
    try {
        await api('/api/rotas', {
            method: 'POST',
            body: JSON.stringify({ nome, horario, trechos })
        });
        limparRotaTracada();
        mostrarPanel('rotas');
        recarregarTudo();
    } catch (e) {
        alert('erro: ' + e.message);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const hoje = new Date().toISOString().slice(0, 10);
    document.getElementById('oc-data').value = hoje;
    verificarSessao();
});
