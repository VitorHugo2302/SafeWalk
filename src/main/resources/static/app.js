const estado = {
    usuario: null,
    mapa: null,
    coordenadaAtual: null,
    camadaAtual: null,
    camadaDestino: null,
    camadaRotas: null,
    camadaOcorrencias: null,
    camadaNovaOco: null,
    destinoAtual: null,
    ocorrencias: [],
    rotasUsuario: [],
    rotaSelecionada: null,
    rotasCandidatas: [],
    rotaAtivaIdx: 0,
    polylinesRotas: [],
    pontoNovaOco: null,
    secaoAberta: null,
    timerSugestao: null
};

const PESOS = {
    ASSALTO: 10,
    FURTO: 6,
    AGRESSAO: 8,
    ILUMINACAO_RUIM: 2.5,
    ACIDENTE_TRANSITO: 4,
    VIA_DETERIORADA: 1.5
};

const CORES_ROTA = ['#1a73e8', '#8ab4f8', '#c3d6f5'];
const RAIO_INFLUENCIA_M = 120;

const ICONES = {
    minhaLoc: L.divIcon({ className: '', html: '<div class="pin-eu"></div>', iconSize: [20,20], iconAnchor: [10,10] }),
    destino: L.divIcon({ className: '', html: '<div class="pin-destino"></div>', iconSize: [32,40], iconAnchor: [16,36] }),
    ocorrencia: L.divIcon({ className: '', html: '<div class="pin-oco">!</div>', iconSize: [20,20], iconAnchor: [10,10] }),
    novaOco: L.divIcon({
        className: '',
        html: '<div style="width:28px;height:28px;background:#ea580c;border:4px solid #fff;border-radius:50%;box-shadow:0 2px 10px rgba(234,88,12,0.6);animation:pulsoLaranja 1.5s infinite;"></div>',
        iconSize: [28,28],
        iconAnchor: [14,14]
    })
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
    } catch (e) { mostrarAviso(e.message); }
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
    } catch (e) { mostrarAviso(e.message); }
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
    document.getElementById('login-usuario').textContent = '@' + estado.usuario.login;
    document.getElementById('inicial-usuario').textContent = (estado.usuario.nome[0] || '?').toUpperCase();
    if (!estado.mapa) iniciarMapa();
    setTimeout(() => estado.mapa.invalidateSize(), 100);
    carregarDados();
    localizarAgora(true);
}

function iniciarMapa() {
    estado.mapa = L.map('mapa', { zoomControl: false }).setView([-23.5505, -46.6333], 13);
    L.control.zoom({ position: 'bottomright' }).addTo(estado.mapa);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap',
        maxZoom: 19
    }).addTo(estado.mapa);
    estado.camadaAtual = L.layerGroup().addTo(estado.mapa);
    estado.camadaDestino = L.layerGroup().addTo(estado.mapa);
    estado.camadaRotas = L.layerGroup().addTo(estado.mapa);
    estado.camadaOcorrencias = L.layerGroup().addTo(estado.mapa);
    estado.camadaNovaOco = L.layerGroup().addTo(estado.mapa);
    estado.mapa.on('click', aoClicarMapa);
}

function aoClicarMapa(ev) {
    if (estado.secaoAberta === 'ocorrencia') {
        estado.pontoNovaOco = ev.latlng;
        estado.camadaNovaOco.clearLayers();
        L.marker(ev.latlng, { icon: ICONES.novaOco }).addTo(estado.camadaNovaOco);
        L.circle(ev.latlng, {
            radius: RAIO_INFLUENCIA_M,
            color: '#ea580c',
            weight: 1,
            fillColor: '#ea580c',
            fillOpacity: 0.15,
            dashArray: '4 4'
        }).addTo(estado.camadaNovaOco);
        const info = document.getElementById('oc-ponto-info');
        info.classList.remove('oculto');
        info.innerHTML = `✓ Ponto marcado em <strong>${ev.latlng.lat.toFixed(5)}, ${ev.latlng.lng.toFixed(5)}</strong>`;
    }
}

function localizarAgora(silencioso) {
    if (!navigator.geolocation) { if (!silencioso) alert('geolocalização não suportada'); return; }
    navigator.geolocation.getCurrentPosition(
        pos => {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;
            estado.coordenadaAtual = { lat, lng };
            estado.camadaAtual.clearLayers();
            L.marker([lat, lng], { icon: ICONES.minhaLoc }).bindPopup('você está aqui').addTo(estado.camadaAtual);
            if (!silencioso || !estado.destinoAtual) {
                estado.mapa.setView([lat, lng], 15);
            }
        },
        err => { if (!silencioso) alert('não consegui sua localização: ' + err.message); },
        { enableHighAccuracy: true, timeout: 10000 }
    );
}

async function carregarDados() {
    try {
        const [rotas, ocs] = await Promise.all([
            api('/api/rotas?escopo=todas'),
            api('/api/ocorrencias')
        ]);
        estado.rotasUsuario = rotas;
        estado.ocorrencias = ocs;
        desenharOcorrencias();
    } catch (e) {}
}

function trechoPorId(id) {
    for (const r of estado.rotasUsuario) {
        for (const t of r.trechos) if (t.id === id) return t;
    }
    return null;
}

function coordDaOcorrencia(o) {
    if (o.lat && o.lng) return { lat: o.lat, lng: o.lng };
    const t = trechoPorId(o.trechoId);
    if (t) return { lat: t.lat, lng: t.lng };
    return null;
}

function desenharOcorrencias() {
    estado.camadaOcorrencias.clearLayers();
    console.log('[safewalk] desenhando', estado.ocorrencias.length, 'ocorrencias');
    estado.ocorrencias.forEach(o => {
        const c = coordDaOcorrencia(o);
        if (!c) { console.warn('[safewalk] ocorrencia sem coord', o); return; }
        L.circle([c.lat, c.lng], {
            radius: RAIO_INFLUENCIA_M,
            color: '#d93025',
            weight: 2,
            opacity: 0.7,
            fillColor: '#d93025',
            fillOpacity: 0.18,
            dashArray: '5 5',
            interactive: false
        }).addTo(estado.camadaOcorrencias);
        L.marker([c.lat, c.lng], { icon: ICONES.ocorrencia })
            .bindPopup(`<b>${rotuloTipo(o.tipo)}</b><br>${o.data}<br>${o.descricao || '(sem descrição)'}<br><small>por ${o.reportadoPor}</small><br><small>lat ${c.lat.toFixed(5)}, lng ${c.lng.toFixed(5)}</small><br><small>raio de influência: ${RAIO_INFLUENCIA_M}m</small>`)
            .addTo(estado.camadaOcorrencias);
    });
}

function rotuloTipo(t) {
    return ({
        ASSALTO: 'Assalto',
        FURTO: 'Furto',
        AGRESSAO: 'Agressão',
        ILUMINACAO_RUIM: 'Iluminação ruim',
        ACIDENTE_TRANSITO: 'Acidente',
        VIA_DETERIORADA: 'Via deteriorada'
    })[t] || t;
}

function haversine(lat1, lng1, lat2, lng2) {
    const R = 6371000;
    const toRad = x => x * Math.PI / 180;
    const dLat = toRad(lat2 - lat1);
    const dLng = toRad(lng2 - lng1);
    const a = Math.sin(dLat/2) ** 2 + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLng/2) ** 2;
    return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

function distanciaMinimaParaRota(lat, lng, coords) {
    let min = Infinity;
    const passo = Math.max(1, Math.floor(coords.length / 200));
    for (let i = 0; i < coords.length; i += passo) {
        const d = haversine(lat, lng, coords[i][1], coords[i][0]);
        if (d < min) min = d;
    }
    return min;
}

function avaliarSeguranca(coords, distKm) {
    const hoje = new Date();
    let peso = 0;
    const ocoNaRota = [];
    for (const o of estado.ocorrencias) {
        const c = coordDaOcorrencia(o);
        if (!c) continue;
        const d = distanciaMinimaParaRota(c.lat, c.lng, coords);
        console.log('[safewalk] ocorrencia', o.id, 'distância da rota:', Math.round(d), 'm', d <= RAIO_INFLUENCIA_M ? '✓ dentro' : '✗ fora');
        if (d > RAIO_INFLUENCIA_M) continue;
        const base = PESOS[o.tipo] || 1;
        const dias = Math.max(0, (hoje - new Date(o.data)) / 86400000);
        const decay = Math.max(0.15, 1 - dias / 730);
        peso += base * decay;
        ocoNaRota.push(o);
    }
    let densidade = peso / Math.max(0.1, distKm);
    const h = new Date().getHours();
    if (h >= 19 || h < 6) densidade *= 1.3;
    const score = Math.max(0, Math.min(100, 100 - Math.min(100, densidade * 4)));
    let classe = 'A';
    if (score < 85) classe = 'B';
    if (score < 70) classe = 'C';
    if (score < 50) classe = 'D';
    if (score < 30) classe = 'E';
    return { score, classe, ocoNaRota };
}

let ultimaBusca = '';

function configurarBusca() {
    const input = document.getElementById('busca-destino');
    input.addEventListener('input', aoDigitarBusca);
    input.addEventListener('keydown', e => {
        if (e.key === 'Enter') { fecharSugestoes(); tracarRotas(); }
        if (e.key === 'Escape') fecharSugestoes();
    });
    document.addEventListener('click', e => {
        if (!e.target.closest('.busca')) fecharSugestoes();
    });
}

function aoDigitarBusca(e) {
    const txt = e.target.value.trim();
    document.getElementById('btn-limpar').classList.toggle('oculto', !txt);
    if (estado.timerSugestao) clearTimeout(estado.timerSugestao);
    if (txt.length < 3) { fecharSugestoes(); return; }
    estado.timerSugestao = setTimeout(() => buscarSugestoes(txt), 400);
}

async function buscarSugestoes(txt) {
    if (txt === ultimaBusca) return;
    ultimaBusca = txt;
    try {
        const vb = estado.coordenadaAtual
            ? `&viewbox=${estado.coordenadaAtual.lng - 0.3},${estado.coordenadaAtual.lat + 0.3},${estado.coordenadaAtual.lng + 0.3},${estado.coordenadaAtual.lat - 0.3}&bounded=0`
            : '';
        const url = `https://nominatim.openstreetmap.org/search?format=json&limit=5&q=${encodeURIComponent(txt)}${vb}`;
        const resp = await fetch(url, { headers: { 'Accept-Language': 'pt-BR' } });
        const data = await resp.json();
        mostrarSugestoes(data);
    } catch (e) { fecharSugestoes(); }
}

function mostrarSugestoes(lista) {
    const cont = document.getElementById('sugestoes');
    if (!lista || lista.length === 0) { fecharSugestoes(); return; }
    cont.innerHTML = '';
    lista.forEach(item => {
        const div = document.createElement('div');
        div.className = 'item-sugestao';
        const partes = item.display_name.split(',');
        div.innerHTML = `
            <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#5f6368" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5A2.5 2.5 0 0 1 9.5 9 2.5 2.5 0 0 1 12 6.5 2.5 2.5 0 0 1 14.5 9 2.5 2.5 0 0 1 12 11.5z"/></svg>
            <div>
                <div class="endereco-principal">${partes[0]}</div>
                <div class="endereco-detalhe">${partes.slice(1, 4).join(',').trim()}</div>
            </div>`;
        div.onclick = () => {
            document.getElementById('busca-destino').value = item.display_name;
            estado.destinoAtual = {
                lat: parseFloat(item.lat),
                lng: parseFloat(item.lon),
                nome: partes[0].trim(),
                nomeCompleto: item.display_name
            };
            fecharSugestoes();
            tracarRotas();
        };
        cont.appendChild(div);
    });
    cont.classList.remove('oculto');
}

function fecharSugestoes() {
    document.getElementById('sugestoes').classList.add('oculto');
}

async function tracarRotas() {
    const txt = document.getElementById('busca-destino').value.trim();
    if (!txt) return;
    if (!estado.coordenadaAtual) {
        alert('Preciso da sua localização pra traçar rotas. Clique no botão de localização.');
        return;
    }
    try {
        if (!estado.destinoAtual || estado.destinoAtual.nomeCompleto !== txt) {
            const resp = await fetch(`https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(txt)}`, {
                headers: { 'Accept-Language': 'pt-BR' }
            });
            const data = await resp.json();
            if (!data || data.length === 0) throw new Error('endereço não encontrado');
            estado.destinoAtual = {
                lat: parseFloat(data[0].lat),
                lng: parseFloat(data[0].lon),
                nome: data[0].display_name.split(',')[0].trim(),
                nomeCompleto: data[0].display_name
            };
        }
        const o = estado.coordenadaAtual;
        const d = estado.destinoAtual;
        mostrarToast('Calculando rotas...', 'info');

        const url = `https://router.project-osrm.org/route/v1/foot/${o.lng},${o.lat};${d.lng},${d.lat}?overview=full&geometries=geojson&alternatives=3&steps=false`;
        const resp = await fetch(url);
        const data = await resp.json();
        if (!data.routes || data.routes.length === 0) throw new Error('nenhuma rota encontrada');

        let candidatas = data.routes.map(r => {
            const coords = r.geometry.coordinates;
            const distKm = r.distance / 1000;
            const av = avaliarSeguranca(coords, distKm);
            return {
                coords, distKm,
                durMin: r.duration / 60,
                score: av.score,
                classe: av.classe,
                ocoNaRota: av.ocoNaRota,
                tipoRota: 'direta'
            };
        });

        candidatas.sort((a, b) => b.score - a.score || a.distKm - b.distKm);
        const distanciaDiretaKm = candidatas[0] ? candidatas[0].distKm : 0;

        if (candidatas[0] && candidatas[0].ocoNaRota.length > 0) {
            mostrarToast('Procurando rota evitando ocorrências...', 'info');
            const piorOco = candidatas[0].ocoNaRota[0];
            const coordOco = coordDaOcorrencia(piorOco);
            if (coordOco) {
                const desvios = await gerarRotasDesvio(o, d, coordOco, distanciaDiretaKm);
                for (const r of desvios) {
                    const coords = r.geometry.coordinates;
                    const distKm = r.distance / 1000;
                    const av = avaliarSeguranca(coords, distKm);
                    candidatas.push({
                        coords, distKm,
                        durMin: r.duration / 60,
                        score: av.score,
                        classe: av.classe,
                        ocoNaRota: av.ocoNaRota,
                        tipoRota: 'desvio'
                    });
                }
            }
        }

        candidatas.sort((a, b) => b.score - a.score || a.distKm - b.distKm);
        candidatas = candidatas.slice(0, 3);
        candidatas.forEach((c, i) => c.rank = i);
        estado.rotasCandidatas = candidatas;
        estado.rotaAtivaIdx = 0;

        desenharCandidatas();
        renderizarCards();
        document.getElementById('destino-titulo').textContent = d.nome;
        document.getElementById('painel-inferior').classList.remove('oculto');

        estado.camadaDestino.clearLayers();
        L.marker([d.lat, d.lng], { icon: ICONES.destino }).bindPopup(d.nomeCompleto).addTo(estado.camadaDestino);
        const todosPontos = [[o.lat, o.lng], [d.lat, d.lng]];
        estado.mapa.fitBounds(L.latLngBounds(todosPontos), { padding: [80, 80], maxZoom: 16 });
    } catch (e) {
        mostrarToast('Erro: ' + e.message, 'erro');
    }
}

async function gerarRotasDesvio(origem, destino, pontoEvitar, distanciaDiretaKm) {
    const dx = destino.lng - origem.lng;
    const dy = destino.lat - origem.lat;
    const len = Math.sqrt(dx * dx + dy * dy);
    if (len === 0) return [];
    const perpX = -dy / len;
    const perpY = dx / len;
    const limiteKm = Math.max(distanciaDiretaKm * 1.8, distanciaDiretaKm + 2);
    const tentativas = [
        { offset: 0.0028, sinal: 1 },
        { offset: 0.0028, sinal: -1 },
        { offset: 0.0050, sinal: 1 },
        { offset: 0.0050, sinal: -1 }
    ];
    const resultados = [];
    for (const { offset, sinal } of tentativas) {
        const way = {
            lat: pontoEvitar.lat + perpY * offset * sinal,
            lng: pontoEvitar.lng + perpX * offset * sinal
        };
        try {
            const url = `https://router.project-osrm.org/route/v1/foot/${origem.lng},${origem.lat};${way.lng},${way.lat};${destino.lng},${destino.lat}?overview=full&geometries=geojson&continue_straight=false`;
            const resp = await fetch(url);
            const data = await resp.json();
            if (data.routes && data.routes.length > 0) {
                const r = data.routes[0];
                const distKm = r.distance / 1000;
                if (distKm > limiteKm) {
                    console.log('[safewalk] desvio descartado (muito longo):', distKm.toFixed(2), 'km > limite', limiteKm.toFixed(2));
                    continue;
                }
                const distDoPonto = distanciaMinimaParaRota(pontoEvitar.lat, pontoEvitar.lng, r.geometry.coordinates);
                if (distDoPonto > RAIO_INFLUENCIA_M) {
                    console.log('[safewalk] desvio aceito (evita ponto):', distKm.toFixed(2), 'km, distancia do ponto:', Math.round(distDoPonto), 'm');
                    resultados.push(r);
                    if (resultados.length >= 2) break;
                } else {
                    console.log('[safewalk] desvio descartado (ainda passa pelo ponto):', Math.round(distDoPonto), 'm <', RAIO_INFLUENCIA_M, 'm');
                }
            }
        } catch (e) {}
        await new Promise(r => setTimeout(r, 300));
    }
    return resultados;
}

function desenharCandidatas() {
    estado.camadaRotas.clearLayers();
    estado.polylinesRotas = [];
    [...estado.rotasCandidatas].reverse().forEach(c => {
        const ativa = c.rank === estado.rotaAtivaIdx;
        const cor = ativa ? CORES_ROTA[0] : CORES_ROTA[2];
        const peso = ativa ? 6 : 4;
        const latlngs = c.coords.map(x => [x[1], x[0]]);
        const poly = L.polyline(latlngs, { color: cor, weight: peso, opacity: ativa ? 0.9 : 0.55 });
        poly.on('click', () => selecionarCandidata(c.rank));
        poly.addTo(estado.camadaRotas);
        estado.polylinesRotas[c.rank] = poly;
    });
}

function renderizarCards() {
    const cont = document.getElementById('cards-rotas');
    cont.innerHTML = '';
    estado.rotasCandidatas.forEach(c => {
        const card = document.createElement('div');
        card.className = 'card-rota' + (c.rank === estado.rotaAtivaIdx ? ' selecionada' : '');
        card.onclick = () => selecionarCandidata(c.rank);
        const marcaDesvio = c.tipoRota === 'desvio' ? '<span class="tag-desvio">desvio</span>' : '';
        card.innerHTML = `
            <div class="indicador"></div>
            <div class="info">
                <div class="linha-titulo">
                    <span class="nome-rota">${nomeRota(c)} ${marcaDesvio}</span>
                    <span class="tempo">${Math.round(c.durMin)} min</span>
                </div>
                <div class="metadados">
                    <span>${c.distKm.toFixed(2)} km</span>
                    <span>·</span>
                    <span class="badge-seguranca ${c.classe}">Segurança ${c.classe} · ${Math.round(c.score)}</span>
                    <span>·</span>
                    <span>${c.ocoNaRota.length} ocorrência${c.ocoNaRota.length!==1?'s':''} no caminho</span>
                </div>
            </div>`;
        cont.appendChild(card);
    });
}

function nomeRota(c) {
    const sem = c.ocoNaRota.length === 0;
    if (c.tipoRota === 'desvio') {
        if (sem) return c.rank === 0 ? 'Rota mais segura (sem ocorrências)' : 'Rota de desvio';
        return 'Rota de desvio';
    }
    if (c.rank === 0) return sem ? 'Rota recomendada' : 'Rota mais curta';
    if (c.rank === 1) return 'Alternativa';
    return 'Alternativa ' + c.rank;
}

function selecionarCandidata(rank) {
    estado.rotaAtivaIdx = rank;
    desenharCandidatas();
    renderizarCards();
}

function limparBusca() {
    document.getElementById('busca-destino').value = '';
    document.getElementById('btn-limpar').classList.add('oculto');
    document.getElementById('painel-inferior').classList.add('oculto');
    estado.camadaRotas.clearLayers();
    estado.camadaDestino.clearLayers();
    estado.rotasCandidatas = [];
    estado.destinoAtual = null;
    fecharSugestoes();
}

function abrirDrawer() {
    document.getElementById('drawer').classList.remove('oculto');
    abrirSecao(null);
    atualizarBackdrop();
}

function fecharDrawer() {
    document.getElementById('drawer').classList.add('oculto');
    document.getElementById('drawer-backdrop').classList.add('oculto');
    estado.secaoAberta = null;
    estado.mapa.getContainer().style.cursor = '';
}

function abrirSecao(nome) {
    estado.secaoAberta = nome;
    document.querySelectorAll('.drawer-secao').forEach(s => s.classList.add('oculto'));
    if (nome) {
        document.getElementById('secao-' + nome).classList.remove('oculto');
    }
    if (nome === 'rotas') carregarListaRotas();
    if (nome === 'emergencia') preencherFormContato();
    atualizarBackdrop();
    estado.mapa.getContainer().style.cursor = nome === 'ocorrencia' ? 'crosshair' : '';
}

function atualizarBackdrop() {
    const backdrop = document.getElementById('drawer-backdrop');
    const drawerAberto = !document.getElementById('drawer').classList.contains('oculto');
    if (drawerAberto && estado.secaoAberta !== 'ocorrencia') {
        backdrop.classList.remove('oculto');
    } else {
        backdrop.classList.add('oculto');
    }
}

function carregarListaRotas() {
    const ul = document.getElementById('lista-rotas');
    ul.innerHTML = '';
    const minhas = estado.rotasUsuario.filter(r => r.dono === estado.usuario.login);
    if (minhas.length === 0) {
        ul.innerHTML = '<li style="cursor:default; color:#5f6368;">você ainda não salvou nenhuma rota</li>';
        return;
    }
    minhas.forEach(r => {
        const li = document.createElement('li');
        const km = r.trechos.reduce((s,t)=>s+t.distanciaKm, 0);
        li.textContent = `${r.nome} — ${km.toFixed(2)} km`;
        if (estado.rotaSelecionada && estado.rotaSelecionada.id === r.id) li.classList.add('selecionado');
        li.onclick = () => selecionarRotaSalva(r);
        ul.appendChild(li);
    });
}

async function selecionarRotaSalva(r) {
    estado.rotaSelecionada = r;
    carregarListaRotas();
    const det = document.getElementById('detalhe-rota');
    det.classList.remove('oculto');
    document.getElementById('dr-nome').textContent = r.nome;
    const km = r.trechos.reduce((s,t)=>s+t.distanciaKm, 0);
    document.getElementById('dr-info').textContent = `${km.toFixed(2)} km · horário ${r.horarioPrevisto}`;
    try {
        const idx = await api(`/api/rotas/${r.id}/indice`);
        const div = document.getElementById('dr-indice');
        div.className = 'badge-indice ' + idx.classificacao;
        div.textContent = `score ${idx.score.toFixed(1)} · classe ${idx.classificacao}`;
    } catch (e) {}
    if (r.trechos.length > 0) {
        const pts = r.trechos.map(t => [t.lat, t.lng]);
        estado.camadaRotas.clearLayers();
        L.polyline(pts, { color: '#137333', weight: 5, opacity: 0.85 }).addTo(estado.camadaRotas);
        estado.mapa.fitBounds(L.latLngBounds(pts), { padding: [60, 60] });
    }
}

async function excluirRotaSelecionada() {
    if (!estado.rotaSelecionada) return;
    if (!confirm('excluir rota "' + estado.rotaSelecionada.nome + '"?')) return;
    try {
        await api('/api/rotas/' + estado.rotaSelecionada.id, { method: 'DELETE' });
        estado.rotaSelecionada = null;
        document.getElementById('detalhe-rota').classList.add('oculto');
        carregarDados();
        carregarListaRotas();
    } catch (e) { alert('erro: ' + e.message); }
}

async function salvarOcorrenciaPonto() {
    if (!estado.pontoNovaOco) { alert('clique num ponto do mapa primeiro pra marcar onde foi'); return; }
    const tipo = document.getElementById('oc-tipo').value;
    const data = document.getElementById('oc-data').value;
    const descricao = document.getElementById('oc-desc').value.trim();
    if (!data) { alert('informe a data'); return; }
    const ponto = estado.pontoNovaOco;
    try {
        const resp = await api('/api/ocorrencias', {
            method: 'POST',
            body: JSON.stringify({
                trechoId: '',
                tipo, data, descricao,
                lat: ponto.lat,
                lng: ponto.lng
            })
        });
        cancelarOcorrencia();
        fecharDrawer();
        L.marker([ponto.lat, ponto.lng], { icon: ICONES.ocorrencia })
            .bindPopup(`<b>${rotuloTipo(tipo)}</b><br>${data}<br>${descricao || '(sem descrição)'}<br><small>registrada por você</small>`)
            .addTo(estado.camadaOcorrencias);
        mostrarToast('Ocorrência registrada', 'sucesso');
        await carregarDados();
        reavaliarCandidatas();
    } catch (e) { mostrarToast('Erro: ' + e.message, 'erro'); }
}

function mostrarToast(msg, tipo) {
    const t = document.createElement('div');
    t.className = 'toast ' + (tipo || 'info');
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.classList.add('visivel'), 50);
    setTimeout(() => {
        t.classList.remove('visivel');
        setTimeout(() => t.remove(), 300);
    }, 2800);
}

function reavaliarCandidatas() {
    if (estado.rotasCandidatas.length === 0) return;
    estado.rotasCandidatas.forEach(c => {
        const av = avaliarSeguranca(c.coords, c.distKm);
        c.score = av.score;
        c.classe = av.classe;
        c.ocoNaRota = av.ocoNaRota;
    });
    estado.rotasCandidatas.sort((a, b) => b.score - a.score || a.distKm - b.distKm);
    estado.rotasCandidatas.forEach((c, i) => c.rank = i);
    estado.rotaAtivaIdx = 0;
    desenharCandidatas();
    renderizarCards();
}

function cancelarOcorrencia() {
    estado.pontoNovaOco = null;
    estado.camadaNovaOco.clearLayers();
    document.getElementById('oc-desc').value = '';
    document.getElementById('oc-ponto-info').classList.add('oculto');
}

function abrirReportarAqui() {
    abrirDrawer();
    abrirSecao('ocorrencia');
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
        saida.textContent = entradas.map(([k, v]) => `${rotuloTipo(k).padEnd(20)} ${v}`).join('\n');
    } catch (e) { alert('erro: ' + e.message); }
}

const CONTATO_KEY = 'safewalk_contato_emergencia';

function carregarContatoEmergencia() {
    try {
        const raw = localStorage.getItem(CONTATO_KEY);
        if (raw) return JSON.parse(raw);
    } catch (e) {}
    return { nome: '', telefone: '' };
}

function preencherFormContato() {
    const c = carregarContatoEmergencia();
    document.getElementById('ce-nome').value = c.nome || '';
    document.getElementById('ce-telefone').value = c.telefone || '';
}

function salvarContatoEmergencia() {
    const nome = document.getElementById('ce-nome').value.trim();
    const telefone = document.getElementById('ce-telefone').value.trim();
    if (!nome || !telefone) { alert('preencha nome e telefone'); return; }
    try {
        localStorage.setItem(CONTATO_KEY, JSON.stringify({ nome, telefone }));
    } catch (e) {}
    mostrarToast('Contato de emergência salvo', 'sucesso');
    fecharDrawer();
}

function abrirSOS() {
    if (!estado.coordenadaAtual) localizarAgora(true);
    document.getElementById('sos-confirmacao').classList.remove('oculto');
    document.getElementById('sos-progresso').classList.add('oculto');
    document.getElementById('modal-sos').classList.remove('oculto');
}

function fecharSOS() {
    document.getElementById('modal-sos').classList.add('oculto');
    document.querySelectorAll('.sos-passos li').forEach(li => li.classList.remove('ativo', 'concluido'));
    document.getElementById('sos-mensagem-preview').classList.add('oculto');
    document.getElementById('sos-ligacao').classList.add('oculto');
}

async function confirmarSOS() {
    document.getElementById('sos-confirmacao').classList.add('oculto');
    document.getElementById('sos-progresso').classList.remove('oculto');

    const contato = carregarContatoEmergencia();
    const nomeContato = contato.nome || 'contato de emergência';
    const c = estado.coordenadaAtual;
    const linkLocal = c
        ? `https://www.google.com/maps?q=${c.lat.toFixed(6)},${c.lng.toFixed(6)}`
        : '(localização indisponível)';
    const nomeUsuario = estado.usuario ? estado.usuario.nome : 'Usuário';

    await passoSOS('passo-loc', 1400);

    const preview = document.getElementById('sos-mensagem-preview');
    preview.innerHTML =
        `<strong>Mensagem para ${nomeContato}:</strong>\n\n` +
        `🆘 ${nomeUsuario} precisa de ajuda!\n` +
        `Estou nesta localização agora:\n${linkLocal}\n\n` +
        `Enviado automaticamente pelo SafeWalk.`;
    preview.classList.remove('oculto');
    await passoSOS('passo-msg', 1600);

    document.getElementById('sos-ligacao').classList.remove('oculto');
    await passoSOS('passo-pol', 1800);
    document.getElementById('sos-timer').textContent = 'chamada simulada';
    mostrarToast('Emergência simulada concluída', 'sucesso');
}

function passoSOS(id, duracao) {
    return new Promise(resolve => {
        const li = document.getElementById(id);
        li.classList.add('ativo');
        setTimeout(() => {
            li.classList.remove('ativo');
            li.classList.add('concluido');
            resolve();
        }, duracao);
    });
}

async function verificarSessao() {
    try {
        const u = await api('/api/sessao');
        estado.usuario = u;
        iniciarApp();
    } catch (e) { mostrarLogin(); }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('oc-data').value = new Date().toISOString().slice(0, 10);
    configurarBusca();
    verificarSessao();
});
