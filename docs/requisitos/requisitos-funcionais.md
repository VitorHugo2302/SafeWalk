# Requisitos funcionais

Os requisitos abaixo descrevem **o que** o sistema deve fazer. Cada requisito tem um identificador (RFxx), descrição, prioridade e critérios de aceitação.

Prioridade: **Alta** (bloqueia o MVP), **Média** (necessário mas pode esperar), **Baixa** (nice to have).

## Autenticação

### RF01 — Cadastro de usuário
**Prioridade:** Alta
**Descrição:** O sistema deve permitir que um visitante crie uma conta informando login, senha e nome.
**Critérios de aceitação:**
- Login não pode estar em branco e deve ser único.
- Senha deve ter no mínimo 4 caracteres.
- A senha é armazenada apenas em hash (SHA-256), nunca em texto puro.
- O usuário recebe mensagem de erro clara em caso de falha.

### RF02 — Login
**Prioridade:** Alta
**Descrição:** O sistema deve autenticar o usuário com login e senha.
**Critérios de aceitação:**
- Credenciais inválidas resultam em mensagem genérica (sem indicar qual campo falhou).
- Sessão autenticada permanece até logout explícito.

### RF03 — Logout
**Prioridade:** Alta
**Descrição:** O sistema deve permitir encerrar a sessão autenticada.
**Critérios de aceitação:**
- Após o logout, as áreas restritas voltam a exigir autenticação.

## Rotas

### RF04 — Cadastro de rota
**Prioridade:** Alta
**Descrição:** O usuário autenticado deve poder cadastrar uma rota com nome, horário previsto e um ou mais trechos.
**Critérios de aceitação:**
- Nome não pode ser vazio.
- Cada trecho tem via, bairro, distância em km (> 0) e coordenadas (lat/lng a partir da Sprint 2).
- A rota é persistida e sobrevive a reinicializações.

### RF05 — Listagem e busca de rotas
**Prioridade:** Alta
**Descrição:** O usuário deve poder listar suas rotas e filtrá-las por nome.
**Critérios de aceitação:**
- A lista mostra nome, quantidade de trechos e distância total.
- Filtro é case-insensitive e por substring do nome.

### RF06 — Exclusão de rota
**Prioridade:** Alta
**Descrição:** O usuário deve poder excluir rotas das quais é dono. Moderadores podem excluir rotas de qualquer usuário.
**Critérios de aceitação:**
- Tentativa de excluir rota de terceiro por usuário comum retorna erro de permissão.

## Ocorrências

### RF07 — Registro de ocorrência
**Prioridade:** Alta
**Descrição:** O usuário deve poder registrar uma ocorrência (assalto, furto, agressão, iluminação ruim, acidente, via deteriorada) em um trecho específico, com data e descrição.
**Critérios de aceitação:**
- Data não pode ser no futuro.
- Trecho deve existir.
- A ocorrência fica associada ao usuário que a reportou.

### RF08 — Listagem e filtro de ocorrências
**Prioridade:** Alta
**Descrição:** O usuário deve poder listar todas as ocorrências, filtrando por tipo e período (data inicial e final).
**Critérios de aceitação:**
- Filtros podem ser combinados.
- Período inválido (fim antes do início) retorna erro.

### RF09 — Exclusão de ocorrência
**Prioridade:** Média
**Descrição:** O usuário pode excluir ocorrências que ele mesmo reportou. Moderadores podem excluir qualquer uma.

## Índice de segurança

### RF10 — Cálculo do índice de segurança
**Prioridade:** Alta
**Descrição:** O sistema deve calcular um índice de segurança (0..100) e classificação (A, B, C, D, E) para cada rota, com base nas ocorrências dos seus trechos.
**Critérios de aceitação:**
- Combina peso base por tipo, decaimento temporal (ocorrência de 2 anos pesa 15%), densidade por km e multiplicador noturno (1.3x entre 19h e 6h).
- Score é normalizado para 0..100.
- Classificação segue faixas: A ≥ 85, B ≥ 70, C ≥ 50, D ≥ 30, E < 30.

## Mapa e geolocalização (Sprint 2)

### RF11 — Visualização em mapa
**Prioridade:** Alta
**Descrição:** O sistema deve exibir as rotas e ocorrências em um mapa interativo (OpenStreetMap via Leaflet).

### RF12 — Localização atual do usuário
**Prioridade:** Alta
**Descrição:** O sistema deve conseguir obter e exibir a localização atual do usuário no mapa.
**Critérios de aceitação:**
- Usa `navigator.geolocation` do navegador.
- Pede permissão ao usuário.
- Mostra marcador e raio de precisão.

### RF13 — Busca de endereço
**Prioridade:** Alta
**Descrição:** O usuário deve poder buscar um endereço por texto livre e o sistema deve retornar as coordenadas correspondentes (Nominatim).

### RF14 — Traçado automático de rota a pé
**Prioridade:** Alta
**Descrição:** Dado origem e destino, o sistema deve calcular e desenhar uma rota a pé real pelas ruas (OSRM).
**Critérios de aceitação:**
- Mostra distância total e tempo estimado.
- Permite salvar a rota traçada como rota do usuário.

## Relatórios (Sprint 2/3)

### RF15 — Contagem de ocorrências por tipo
**Prioridade:** Média
**Descrição:** O sistema deve gerar um relatório com a contagem de ocorrências agrupadas por tipo, com filtro opcional de período.

### RF16 — Exportação CSV
**Prioridade:** Baixa
**Descrição:** O usuário deve poder exportar as ocorrências em formato CSV.
