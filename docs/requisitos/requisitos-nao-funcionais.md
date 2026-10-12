# Requisitos não funcionais

Os requisitos abaixo descrevem **como** o sistema deve se comportar — qualidade, restrições e padrões técnicos. Classificados pelas categorias ISO/IEC 25010.

## Segurança

### RNF01 — Armazenamento de senhas
Senhas devem ser armazenadas apenas em hash (SHA-256 na Sprint 0, substituir por BCrypt em sprint posterior conforme ADR-003 da arquitetura). Nunca em texto puro.

### RNF02 — Isolamento de sessão
Em contexto web, cada usuário tem sua própria sessão HTTP. Dados de um usuário não vazam entre sessões.

### RNF03 — Validação de permissões
Operações sensíveis (exclusão) devem verificar se o solicitante é dono do recurso ou tem perfil `MODERADOR`/`ADMIN`.

## Usabilidade

### RNF04 — Interface simples
A interface principal deve ser utilizável sem treinamento prévio. Qualquer funcionalidade deve estar acessível em no máximo 2 cliques a partir da tela principal.

### RNF05 — Idioma
Toda a interface, mensagens de erro e documentação ficam em português (pt-BR).

### RNF06 — Feedback de ações
Toda ação do usuário deve retornar feedback visual em no máximo 2 segundos (mensagem de sucesso, erro ou indicador de carregamento).

## Desempenho

### RNF07 — Tempo de resposta
Operações básicas (login, listar rotas, registrar ocorrência) devem responder em até 2 segundos em cenário de até 100 rotas e 500 ocorrências.

### RNF08 — Build e inicialização
O projeto deve compilar com `mvn package` em menos de 60 segundos em máquina padrão. O jar executável deve subir em menos de 10 segundos.

## Portabilidade

### RNF09 — Multiplataforma
O sistema deve rodar em Windows, Linux e macOS, bastando ter JDK 17 e Maven instalados.

### RNF10 — Zero dependência externa paga
O sistema não deve depender de nenhum serviço pago. Serviços externos (Nominatim, OSRM, OpenStreetMap) usam apenas instâncias públicas gratuitas.

### RNF11 — Compatibilidade de navegador
A interface web deve funcionar nas versões atuais de Chrome, Firefox e Edge.

## Confiabilidade

### RNF12 — Persistência durável
Dados devem ser persistidos em disco (arquivos CSV em `./dados/`) e sobreviver a reinicializações da aplicação.

### RNF13 — Tratamento de falhas
Erros de entrada do usuário são capturados e exibidos como mensagens claras, sem derrubar a aplicação.

## Manutenibilidade

### RNF14 — Arquitetura em camadas
O código deve respeitar as 4+ camadas definidas (`domain`, `service`, `infra`, `ui`/`web`), sem dependências cíclicas e com inversão de dependência via interfaces em `infra`.

### RNF15 — Convenções de commit
Mensagens de commit seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`, `test:`, `refactor:`).

### RNF16 — Mínimo de classes autorais
O projeto deve ter no mínimo 10 classes autorais, não contando frameworks, mocks ou código gerado.

## Testabilidade

### RNF17 — Cobertura mínima
Cobertura de testes unitários ≥ **80%** nas classes de regra (`domain`) e serviço (`service`).

### RNF18 — Testes automatizados
Suíte de testes JUnit 5 deve rodar com `mvn test` em menos de 30 segundos.

### RNF19 — Cenários BDD
Mínimo de 5 cenários Gherkin automatizados com Cucumber, cobrindo fluxos essenciais.

## Documentação

### RNF20 — README atualizado
O README deve ser atualizado a cada mudança de comportamento visível pelo usuário.

### RNF21 — Rastreabilidade
Toda entrega (feature, correção, documento) deve estar vinculada a uma issue e a um pull request fechado no GitHub.
