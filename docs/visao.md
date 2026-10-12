# Visão do produto — SafeWalk

## Elevator pitch

SafeWalk é uma plataforma web que ajuda pedestres a planejar trajetos a pé mais seguros, combinando mapas interativos (OpenStreetMap) com dados de ocorrências reportadas pelos próprios usuários. Antes de sair de casa, a pessoa consegue ver o nível de segurança de uma rota e escolher caminhos melhores.

## Problema

- Pedestres não têm uma forma simples de comparar o risco entre dois caminhos antes de andar.
- Informações sobre assaltos, iluminação ruim e acidentes ficam espalhadas em redes sociais e grupos de bairro, sem padronização.
- Aplicativos de rota (Google Maps, Waze) otimizam distância e tempo, não segurança.

## Público-alvo

- Estudantes e trabalhadores que se deslocam a pé em áreas urbanas.
- Moradores de bairros com histórico de ocorrências.
- Entregadores e profissionais que caminham longas distâncias no dia.

### Personas

- **Ana (universitária, 21)** — volta da faculdade à noite e quer evitar ruas sem iluminação.
- **Carlos (bancário, 34)** — mora longe do metrô e troca o caminho conforme o horário.
- **Dona Lúcia (aposentada, 68)** — procura trajetos com menos tráfego pesado e calçadas em bom estado.

## Objetivos

1. Permitir que o usuário cadastre, visualize e avalie rotas a pé.
2. Agregar ocorrências georreferenciadas e calcular um índice de segurança por rota.
3. Oferecer busca por endereço e cálculo automático de rota a pé usando dados abertos.

## Métricas de sucesso

- Usuário consegue cadastrar uma rota e ver seu índice em menos de 2 minutos.
- 100% das rotas cadastradas possuem índice calculado e classificado (A..E).
- Zero dependência de serviços pagos (sem Google Maps API, sem conta externa).

## Diferenciais

- Dados coletados pela própria comunidade — não dependem de polícia ou mídia.
- Índice de segurança considera **recência** da ocorrência (peso cai com o tempo).
- Multiplicador por horário noturno — a mesma rota vale mais ou menos à noite.
- Totalmente gratuito, sem chave de API externa.

## Fora do escopo (por ora)

- Push notifications e alertas em tempo real.
- Aplicativo nativo mobile (apenas web responsivo).
- Moderação automática de ocorrências por IA.
- Integração com polícia ou órgãos públicos.
- Rotas em transporte público ou carro — apenas a pé.
