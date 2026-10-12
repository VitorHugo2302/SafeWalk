# Roteiros de teste manual

> Esqueleto. Roteiros completos com evidências serão executados nas Sprints 2 e 3.

Cada roteiro segue o formato: **ID**, **objetivo**, **pré-condições**, **passos**, **resultado esperado**, **resultado obtido**, **status**, **evidência**.

Evidências ficam em [evidencias/](evidencias/).

## RT01 — Fluxo principal: cadastro + login

**Objetivo:** verificar que um visitante consegue criar conta e entrar.
**Pré-condições:** Aplicação rodando, nenhum usuário cadastrado com o login a ser testado.
**Passos:**
1. Abrir a aplicação.
2. Escolher "cadastrar".
3. Informar login `teste01`, senha `1234`, nome `Teste`.
4. Confirmar.
5. Voltar à tela inicial e escolher "entrar".
6. Informar `teste01` / `1234`.

**Resultado esperado:** mensagem "cadastrado" após o passo 4; "ola Teste" após o passo 6.
**Status:** *(executar na Sprint 1)*
**Evidência:** `evidencias/RT01-*.png`

---

## RT02 — Fluxo principal: criar rota com trechos

**Objetivo:** validar que o usuário consegue registrar uma rota completa.
**Pré-condições:** usuário autenticado.
**Passos:** *(descrever ao chegar na Sprint 1)*
**Status:** *(a executar)*

---

## RT03 — Validação de dados: senha curta

**Objetivo:** verificar bloqueio no cadastro com senha inválida.
**Pré-condições:** aplicação rodando.
**Passos:**
1. Ir em "cadastrar".
2. Informar senha com 2 caracteres.
3. Tentar confirmar.

**Resultado esperado:** mensagem de erro "senha curta demais"; conta não é criada.
**Status:** *(a executar)*

---

## RT04 — Validação de dados: login duplicado

**Objetivo:** impedir duas contas com o mesmo login.
**Pré-condições:** já existe usuário `teste01`.
**Passos:**
1. Tentar cadastrar novamente com login `teste01`.

**Resultado esperado:** erro "login ja existe".
**Status:** *(a executar)*

---

## RT05 — Borda: data de ocorrência no futuro

**Objetivo:** impedir registro de ocorrência com data posterior à atual.
**Pré-condições:** usuário autenticado, rota com pelo menos um trecho.
**Passos:**
1. Ir em "registrar ocorrência".
2. Escolher trecho, tipo e descrição.
3. Informar data **1 ano no futuro**.
4. Tentar salvar.

**Resultado esperado:** erro "data no futuro"; ocorrência não é persistida.
**Status:** *(a executar na Sprint 2)*

---

## Resumo

| ID | Fluxo | Status |
|----|-------|--------|
| RT01 | Cadastro + login (principal) | a executar |
| RT02 | Criar rota (principal) | a executar |
| RT03 | Senha curta (validação) | a executar |
| RT04 | Login duplicado (validação) | a executar |
| RT05 | Data futura (borda) | a executar |
