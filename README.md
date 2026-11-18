# SafeWalk

Plataforma web de rotas seguras para pedestres com mapa interativo (OpenStreetMap via Leaflet). Permite cadastrar rotas, registrar ocorrências por trecho e calcular um índice de segurança combinando peso por tipo, decaimento temporal e horário da rota.

## Rodar

Pré-requisitos: JDK 17 e Maven 3.9+.

    mvn package
    java -jar target/safewalk.jar

A aplicação sobe em http://localhost:8080.

## Modo console (opcional)

A camada de console original foi mantida. Para rodar sem o servidor web:

    mvn -q compile exec:java -Dexec.mainClass=br.safewalk.App

## Estrutura (5 camadas)

- `br.safewalk.domain` — entidades e regras (Usuario, Rota, Trecho, Ocorrencia, Periodo, IndiceSeguranca, TipoOcorrencia, PerfilUsuario)
- `br.safewalk.service` — casos de uso (Autenticacao, Rota, Ocorrencia, Relatorio, CalculadoraIndice)
- `br.safewalk.infra` — persistência em arquivo CSV (interfaces + implementações)
- `br.safewalk.ui` — menu de console (modo alternativo)
- `br.safewalk.web` — Spring Boot (WebApp, config, controllers, dtos)

Frontend em `src/main/resources/static` (HTML + Leaflet + JS puro).

## Funcionalidades

1. Cadastro e login de usuário (SHA-256 + sessão HTTP)
2. Criação de rota clicando no mapa para marcar trechos
3. Visualização das rotas no mapa (próprias e de outros usuários)
4. Registro de ocorrência vinculada a um trecho
5. Avaliação do índice de segurança da rota (A..E)
6. Relatório de contagem por tipo + filtro de período
7. Exportação CSV
8. Exclusão com permissão por autor/moderador
9. Busca/filtro de rotas por nome

## Regra de negócio não-trivial

O índice de segurança em `service/CalculadoraIndice` combina:

- peso base por tipo de ocorrência (assalto > agressão > furto > acidente > iluminação > via)
- decaimento temporal: `max(0.15, 1 - dias/730)` — uma ocorrência de 2 anos atrás pesa 15%
- densidade por km dos trechos da rota
- multiplicador de 1.3 se a rota for noturna (19h–6h)
- normalização em score 0..100 e classificação em faixas A/B/C/D/E

## Persistência

Arquivos CSV criados no diretório `dados/` ao executar.

## Mapa

OpenStreetMap renderizado pelo Leaflet 1.9.4 (CDN). Sem chave de API, sem conta externa, sem custo.
