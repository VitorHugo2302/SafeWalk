# SafeWalk

Plataforma de rotas seguras para pedestres. Permite que usuários cadastrem trajetos a pé, registrem ocorrências por trecho e consultem um índice de segurança calculado a partir dos dados da comunidade.

## Status

Em desenvolvimento. Projeto estruturado em sprints.

| Sprint | Entrega |
|--------|---------|
| 0 | Setup do projeto, visão, backlog, arquitetura e autenticação |
| 1 | *(próximas sprints definidas no [backlog](docs/backlog.md))* |

## Documentação

- [Visão do produto](docs/visao.md)
- [Backlog](docs/backlog.md)
- [Arquitetura](docs/arquitetura.md)

## Rodar

Pré-requisitos: JDK 17 e Maven 3.9+.

    mvn package
    java -jar target/safewalk.jar

## Estrutura

- `br.safewalk.domain` — entidades e regras de negócio
- `br.safewalk.service` — casos de uso
- `br.safewalk.infra` — persistência em arquivo
- `br.safewalk.ui` — interface de console
