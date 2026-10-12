# Plano de teste

> Documento vivo. Última atualização: Sprint 0 (esqueleto). Preenchimento completo previsto para Sprint 2.

## 1. Introdução

### 1.1 Objetivo
Garantir que o sistema SafeWalk atenda aos requisitos funcionais e não funcionais definidos em [requisitos-funcionais.md](../requisitos/requisitos-funcionais.md) e [requisitos-nao-funcionais.md](../requisitos/requisitos-nao-funcionais.md).

### 1.2 Escopo
- Testes unitários automatizados (JUnit 5) para `service` e `domain`.
- Testes BDD automatizados (Cucumber) para fluxos end-to-end.
- Roteiros de teste manuais para validar a interface e cenários de borda.
- Teste de usabilidade com participantes reais.

Fora do escopo: testes de carga, testes de segurança por pentest.

## 2. Estratégia

| Tipo | Ferramenta | Cobertura esperada |
|------|-----------|--------------------|
| Unitário | JUnit 5 | ≥ 80% em `service` e `domain` |
| BDD | Cucumber | Mínimo 5 cenários |
| Manual | — | Mínimo 5 roteiros com evidências |
| Usabilidade | — | Mínimo 3 participantes |

## 3. Ambiente

- SO: Windows 11 / Linux / macOS
- JDK: 17 (Temurin)
- Build: Maven 3.9+
- Navegadores suportados (fase web): Chrome, Firefox, Edge

## 4. Critérios

### 4.1 Entrada
- Código do sprint em `main` ou branch de release.
- Suíte de testes compilando.

### 4.2 Saída (aprovação)
- 100% dos testes unitários passando.
- Cobertura ≥ 80% nas camadas-alvo.
- 100% dos cenários BDD passando.
- Todos os roteiros manuais executados com evidência positiva.
- Teste de usabilidade concluído e documentado.

### 4.3 Suspensão
- Falha crítica (ex.: build quebrado) suspende o ciclo até correção.

## 5. Riscos

| Risco | Mitigação |
|-------|-----------|
| Nominatim/OSRM fora do ar durante demo | Mock local com rota pré-gravada |
| Permissão de geolocalização negada pelo usuário | Fallback com entrada manual de endereço |
| CSV corrompido | Validação ao carregar + mensagem de erro clara |

## 6. Entregas

- Testes unitários em `src/test/java/`
- Cenários BDD em `src/test/resources/features/`
- Roteiros em [roteiros-de-teste.md](roteiros-de-teste.md)
- Evidências (prints, logs) em [evidencias/](evidencias/)
- Resultados de usabilidade em [usabilidade.md](usabilidade.md)

## 7. Cronograma

| Sprint | Atividade |
|--------|-----------|
| 1 | Setup do JUnit 5, testes de `AutenticacaoService`, `RotaService` |
| 2 | Testes de `CalculadoraIndice`, setup do Cucumber, 5 cenários Gherkin |
| 3 | Fechamento da cobertura, roteiros manuais, usabilidade, relatórios |

## 8. Equipe

| Papel | Responsável |
|-------|-------------|
| Testes unitários | *(a definir)* |
| Testes BDD | *(a definir)* |
| Testes manuais | *(a definir)* |
| Testes de usabilidade | *(a definir)* |
