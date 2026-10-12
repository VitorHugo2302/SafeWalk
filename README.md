# SafeWalk

Plataforma de rotas seguras para pedestres que combina mapas interativos com dados de ocorrências reportadas pela comunidade. Antes de sair de casa, o usuário consegue consultar o índice de segurança de uma rota, visualizar ocorrências próximas (assaltos, furtos, iluminação ruim, acidentes) e escolher caminhos melhores. O sistema é gratuito, não depende de APIs pagas e usa OpenStreetMap. Projeto desenvolvido para a disciplina de Gestão e Qualidade de Software seguindo metodologia Scrum.

## Problema e público-alvo

Pedestres não têm uma forma simples e padronizada de comparar o risco entre dois caminhos antes de sair de casa. Informações sobre segurança urbana ficam espalhadas em redes sociais e grupos de bairro.

**Público-alvo:** estudantes, trabalhadores, entregadores e moradores de áreas urbanas que se deslocam a pé.

Detalhes em [docs/visao.md](docs/visao.md).

## Funcionalidades

1. Cadastro e autenticação de usuário (hash SHA-256)
2. Cadastro, listagem, busca e exclusão de rotas com múltiplos trechos
3. Registro de ocorrências georreferenciadas por trecho
4. Filtro de ocorrências por tipo e período
5. Cálculo de índice de segurança (score 0..100, classes A..E) com decaimento temporal e multiplicador noturno
6. Visualização em mapa interativo (OpenStreetMap/Leaflet)
7. Geolocalização atual do usuário
8. Busca de endereço (Nominatim) e traçado automático de rota a pé (OSRM)
9. Relatórios de contagem por tipo e exportação CSV

Lista completa com critérios em [docs/requisitos/requisitos-funcionais.md](docs/requisitos/requisitos-funcionais.md).

## Tecnologias

- **Linguagem:** Java 17
- **Build:** Maven 3.9+
- **Testes unitários:** JUnit 5
- **BDD:** Cucumber
- **Interface web (Sprint 2+):** Spring Boot
- **Mapa:** Leaflet.js + OpenStreetMap
- **Geocodificação:** Nominatim
- **Roteamento a pé:** OSRM (Open Source Routing Machine)
- **Persistência:** arquivos CSV em `./dados/`

## Como executar

Pré-requisitos: JDK 17 e Maven 3.9+.

```bash
mvn package
java -jar target/safewalk.jar
```

A partir da Sprint 2, a interface web sobe em http://localhost:8080.

## Como rodar os testes

```bash
mvn test
```

Relatório de cobertura (quando configurado nas Sprints 1+):

```bash
mvn verify
# relatório em target/site/jacoco/index.html
```

Cenários BDD (a partir da Sprint 2):

```bash
mvn test -Dcucumber.filter.tags="@sprint-2"
```

## Estrutura de pastas

```
.
├── docs/
│   ├── visao.md                      Visão do produto
│   ├── backlog.md                    Backlog priorizado
│   ├── arquitetura.md                Arquitetura em camadas e ADRs
│   ├── requisitos/
│   │   ├── requisitos-funcionais.md
│   │   └── requisitos-nao-funcionais.md
│   └── testes/
│       ├── plano-de-teste.md
│       ├── roteiros-de-teste.md
│       ├── usabilidade.md
│       └── evidencias/               Prints, logs, relatórios
├── slides/                           Apresentação final
├── src/
│   ├── main/
│   │   ├── java/br/safewalk/
│   │   │   ├── App.java              Entry point CLI
│   │   │   ├── domain/               Entidades e regras puras
│   │   │   ├── service/              Casos de uso
│   │   │   ├── infra/                Persistência (CSV)
│   │   │   ├── ui/                   Interface de console
│   │   │   └── web/                  Interface web (Spring Boot, Sprint 2+)
│   │   └── resources/                Recursos web e config (Sprint 2+)
│   └── test/
│       ├── java/                     Testes JUnit 5
│       └── resources/features/       Cenários Gherkin Cucumber
├── pom.xml
└── README.md
```

## Metodologia e sprints

| Sprint | Entregável principal | Status |
|--------|---------------------|--------|
| 0 | Setup, visão, backlog, arquitetura, autenticação | ✅ |
| 1 | MVP com CRUD de rotas e ocorrências + testes JUnit iniciais + HUB (Pré-Projeto, Canvas) | 🔄 |
| 2 | Mapa + geolocalização + rota traçada + 5 cenários BDD + Relatório + Pitch | 📋 |
| 3 | Estabilização, cobertura ≥ 80%, 5 roteiros manuais, usabilidade com 3 participantes, slides | 📋 |

## Integrantes e papéis

| Integrante | Papel Scrum | Responsabilidade |
|------------|-------------|------------------|
| Vitor Hugo | *(a definir)* | *(a definir)* |
| Tiago Lucas | *(a definir)* | *(a definir)* |
| Arthur Silva | *(a definir)* | *(a definir)* |
| Helder | *(a definir)* | *(a definir)* |

Sugestão de distribuição Scrum para 4 pessoas: 1 Product Owner, 1 Scrum Master, 2 Desenvolvedores.

## Licença

Este projeto é acadêmico, desenvolvido para a disciplina de Gestão e Qualidade de Software. Uso livre para fins educacionais.
