# Arquitetura

## Visão geral

O projeto segue uma arquitetura em camadas com separação clara de responsabilidades. Cada camada depende apenas de camadas mais internas, nunca o inverso. Isso garante que mudanças de interface (console → web) ou de persistência (arquivo → banco) não afetem as regras de negócio.

## Camadas

```
┌──────────────────────────────────────────────┐
│  ui  (console, futuramente web)              │
│  conhece: service                            │
└──────────────┬───────────────────────────────┘
               │
┌──────────────▼───────────────────────────────┐
│  service  (casos de uso)                     │
│  conhece: domain, infra (interfaces)         │
└──────────────┬───────────────────────────────┘
               │
┌──────────────▼───────────────────────────────┐
│  infra  (persistência, adaptadores)          │
│  conhece: domain                             │
└──────────────┬───────────────────────────────┘
               │
┌──────────────▼───────────────────────────────┐
│  domain  (entidades, enums, regras puras)    │
│  não conhece nada                            │
└──────────────────────────────────────────────┘
```

## Pacotes

| Pacote | Responsabilidade | Exemplos |
|--------|------------------|----------|
| `br.safewalk.domain` | Entidades, enums e regras invariantes | `Usuario`, `PerfilUsuario` |
| `br.safewalk.service` | Casos de uso, orquestração | `AutenticacaoService` |
| `br.safewalk.infra` | Persistência e adaptadores externos | `UsuarioRepository`, `UsuarioRepositoryArquivo` |
| `br.safewalk.ui` | Entrada e saída com o usuário | `MenuPrincipal`, `MenuUsuario`, `EntradaConsole` |

## Regras de dependência

- `domain` não importa nada de outras camadas (sem Spring, sem JDBC, sem java.io).
- `service` recebe dependências de `infra` por **interface** no construtor (inversão de dependência).
- `infra` implementa interfaces definidas em si mesmo ou em `service`.
- `ui` **nunca** acessa `infra` ou `domain` diretamente — passa por `service`.

## Padrões aplicados

- **Repository Pattern**: `UsuarioRepository` como interface, `UsuarioRepositoryArquivo` como implementação. Troca o backend sem alterar o serviço.
- **Injeção de dependência manual**: feita em `App.main`. Nenhum framework ainda — mantém o setup transparente.
- **Entidade com construtor validante**: campos imutáveis sempre que possível; validação na construção (ver `Usuario`).
- **Hashing de senha**: `AutenticacaoService` nunca armazena senha em texto puro. SHA-256 na entrada.

## Decisões técnicas (ADRs curtas)

### ADR-001 — Persistência em CSV
**Contexto:** Sprint 0 precisa de persistência leve, sem servidor.
**Decisão:** usar arquivos CSV em `./dados/`.
**Consequência:** zero setup pro usuário rodar. Em sprints futuras, trocaremos para SQLite se o volume crescer, mantendo a mesma interface `Repository`.

### ADR-002 — Interface de console no primeiro momento
**Contexto:** Precisamos entregar valor sem gastar tempo em UI.
**Decisão:** CLI em `Scanner`, menus numéricos.
**Consequência:** Testável, demonstrável, barato. Interface web entra em sprint futura (ver backlog E5).

### ADR-003 — Hash SHA-256 (sem salt) na Sprint 0
**Contexto:** Autenticação precisa não armazenar senha em texto puro.
**Decisão:** SHA-256 simples.
**Consequência:** Suficiente para o escopo acadêmico. Em produção real, trocar por BCrypt/Argon2 com salt. Fica registrado como dívida técnica.

## Evolução prevista

| Sprint | Mudança arquitetural |
|--------|----------------------|
| 1 | Novos agregados (`Rota`, `Trecho`) e seus serviços/repositórios |
| 2 | Entidade `Ocorrencia` + serviço de cálculo de índice (`CalculadoraIndice`) |
| 3 | Nova camada `web` (Spring Boot) coexistindo com `ui` de console; mapa Leaflet no front |
| 4 | Serviços de relatório e exportação; introdução de perfis `MODERADOR`/`ADMIN` |

## Diagrama de classes (Sprint 0)

```
                     ┌─────────────────┐
                     │   App (main)    │
                     └────────┬────────┘
                              │ monta
             ┌────────────────┼─────────────────┐
             ▼                ▼                 ▼
      ┌────────────┐   ┌────────────┐   ┌──────────────┐
      │ MenuPrinc. │   │ MenuUsuario│   │ EntradaConso │
      └──────┬─────┘   └──────┬─────┘   └──────────────┘
             │                │
             ▼                ▼
      ┌─────────────────────────────┐
      │   AutenticacaoService       │
      └──────────────┬──────────────┘
                     │ depende de
                     ▼
      ┌─────────────────────────────┐
      │  UsuarioRepository (iface)  │
      └──────────────┬──────────────┘
                     │ implementada por
                     ▼
      ┌─────────────────────────────┐
      │  UsuarioRepositoryArquivo   │─── lê/escreve ──▶ dados/usuarios.csv
      └──────────────┬──────────────┘
                     │ manipula
                     ▼
                ┌──────────┐
                │ Usuario  │
                │PerfilUsr │
                └──────────┘
```
