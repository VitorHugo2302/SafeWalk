# Backlog do produto

Lista priorizada das histórias de usuário. Status atualizado a cada sprint.

## Legenda

- **MVP**: deve estar no mínimo entregável
- **Nice-to-have**: desejável mas não bloqueia o MVP
- Esforço: P (pequeno), M (médio), G (grande)

## Épicos

- **E1 — Autenticação**: cadastro, login e identidade dos usuários
- **E2 — Rotas**: cadastro e gerenciamento de trajetos a pé
- **E3 — Ocorrências**: registro e consulta de eventos de segurança
- **E4 — Índice de segurança**: cálculo e classificação do risco por rota
- **E5 — Mapa**: visualização geográfica, geolocalização e busca de endereço
- **E6 — Relatórios**: contagens, filtros e exportação

## Histórias

| ID | Épico | História | Prioridade | Esforço | Sprint | Estado |
|----|-------|----------|------------|---------|--------|--------|
| US01 | E1 | Como visitante, quero criar uma conta pra começar a usar o sistema | MVP | P | 0 | ✅ pronto |
| US02 | E1 | Como usuário, quero fazer login pra acessar meus dados | MVP | P | 0 | ✅ pronto |
| US03 | E1 | Como usuário, quero sair da sessão pra proteger minha conta | MVP | P | 0 | ✅ pronto |
| US04 | E2 | Como usuário, quero cadastrar uma rota com vários trechos pra planejar meu trajeto | MVP | M | 1 | 📋 backlog |
| US05 | E2 | Como usuário, quero listar minhas rotas pra escolher qual usar | MVP | P | 1 | 📋 backlog |
| US06 | E2 | Como usuário, quero buscar rotas pelo nome pra encontrar rápido | MVP | P | 1 | 📋 backlog |
| US07 | E2 | Como usuário, quero excluir uma rota minha pra remover trajetos obsoletos | MVP | P | 1 | 📋 backlog |
| US08 | E3 | Como usuário, quero registrar uma ocorrência em um trecho pra avisar outros usuários | MVP | M | 2 | 📋 backlog |
| US09 | E3 | Como usuário, quero filtrar ocorrências por tipo e período pra entender a situação atual | MVP | M | 2 | 📋 backlog |
| US10 | E3 | Como usuário, quero excluir uma ocorrência que eu reportei pra corrigir erros | MVP | P | 2 | 📋 backlog |
| US11 | E4 | Como usuário, quero ver o índice de segurança de uma rota pra avaliar o risco | MVP | G | 2 | 📋 backlog |
| US12 | E5 | Como usuário, quero ver minhas rotas em um mapa pra entender o traçado | MVP | G | 3 | 📋 backlog |
| US13 | E5 | Como usuário, quero ver minha localização atual no mapa pra me orientar | MVP | M | 3 | 📋 backlog |
| US14 | E5 | Como usuário, quero buscar um endereço por texto pra traçar uma rota até lá | MVP | M | 3 | 📋 backlog |
| US15 | E5 | Como usuário, quero que o app trace automaticamente uma rota a pé entre dois pontos pra economizar tempo | MVP | G | 3 | 📋 backlog |
| US16 | E5 | Como usuário, quero salvar uma rota traçada como minha pra depois reportar ocorrências nela | MVP | M | 3 | 📋 backlog |
| US17 | E6 | Como usuário, quero ver a contagem de ocorrências por tipo pra entender quais problemas são mais comuns | Nice | P | 4 | 📋 backlog |
| US18 | E6 | Como usuário, quero exportar as ocorrências em CSV pra analisar fora do app | Nice | P | 4 | 📋 backlog |
| US19 | E3 | Como moderador, quero excluir ocorrências de outros usuários pra remover spam | Nice | P | 4 | 📋 backlog |
| US20 | E1 | Como usuário, quero recuperar minha senha pra não perder minha conta | Nice | M | — | 📋 futuro |

## Critérios de aceitação (Sprint 0)

**US01 — Cadastro**
- Dado que informei login, senha (≥ 4 caracteres) e nome, quando enviar, então minha conta é criada.
- A senha é armazenada em hash (SHA-256), nunca em texto puro.
- Se o login já existir, recebo uma mensagem clara de erro.

**US02 — Login**
- Dado que informei login e senha corretos, quando entrar, então a sessão é iniciada.
- Credenciais inválidas resultam em mensagem de erro, sem revelar se o login existe ou não.

**US03 — Logout**
- Dado que estou autenticado, quando escolher sair, então a sessão é encerrada e volto à tela inicial.

## Mapa de sprints

| Sprint | Tema | Histórias |
|--------|------|-----------|
| 0 | Setup + autenticação | US01, US02, US03 |
| 1 | CRUD de rotas | US04, US05, US06, US07 |
| 2 | Ocorrências e índice | US08, US09, US10, US11 |
| 3 | Mapa, geolocalização e roteamento | US12, US13, US14, US15, US16 |
| 4 | Relatórios e permissões | US17, US18, US19 |
