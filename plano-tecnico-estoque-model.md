# Plano técnico — Estoque Model

## 1. Contexto do TG

**Estoque Model** é o Trabalho de Graduação do curso de Tecnólogo em Análise e Desenvolvimento de Sistemas (Fatec Sorocaba), orientação da Profª Cristiane Palomar Mercado, grupo formado por Bruno Marchione Corrêa da Silva, Caroline Paccola Costa, Francine dos Reis Antunes, João Victor Kenji Funaki e Vinícius de Freitas Vieira.

Grandes empresas e galpões logísticos precisam de estruturas de armazenagem para pallets (porta-pallets, drive-in, dinâmico, entre outras) para aproveitar melhor o espaço vertical do galpão. Hoje, escolher a estrutura certa exige cotações e consultorias especializadas — processo lento e caro, agravado pelo fato de que grande parte do mercado só conhece o porta-pallets, mesmo quando outra estrutura teria custo-benefício melhor para o produto/operação em questão.

O sistema propõe resolver isso com uma ferramenta web de baixo custo: o usuário informa dados da operação (área disponível, tipo de produto/pallet, empilhadeira) e o sistema simula e recomenda a estrutura mais adequada, o modelo de empilhadeira ideal e a quantidade de pallets armazenáveis na área — reduzindo a dependência de um especialista para um primeiro estudo de viabilidade.

Existem dois perfis de uso: **usuário comum**, que cadastra seus próprios pallets/empilhadeiras e roda simulações; e **administrador**, que mantém o catálogo de estruturas disponíveis no mercado e gerencia os usuários do sistema — é o administrador quem alimenta o sistema com dados técnicos de mercado, pensando numa eventual comercialização para fabricantes do setor.

## 2. Perfis e responsabilidades

| Perfil | O que faz no sistema |
|---|---|
| `usuario` | Cadastra/edita seu perfil, gerencia (cadastra, altera, exclui, pesquisa) seus próprios pallets e empilhadeiras, roda simulações e imprime o resultado em PDF |
| `admin` | Tudo que o usuário comum faz, mais: gerencia o catálogo de estruturas de armazenagem e gerencia usuários (cadastrar, alterar, excluir, pesquisar) |

Essa separação vem do diagrama de casos de uso do TG: só o Administrador tem acesso a "Gerenciar Estrutura" e "Gerenciar Usuário"; as demais ações (login, editar perfil, gerenciar pallet/empilhadeira, fazer simulação, logout, recuperar senha) são do usuário autenticado, comum ou admin.

## 3. Stack confirmada

- **Backend**: Java 21 + Spring Boot 3 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`), Maven. Estrutura já criada hoje em `backend/`.
- **Autenticação**: JWT stateless (`jjwt`), senha com hash BCrypt (`PasswordEncoder`). CORS liberado para a URL do front-end.
- **Banco de dados**: PostgreSQL, já hospedado na LocaWeb. **Pendência aberta**: ainda não confirmamos se esse Postgres aceita conexão externa (fora da rede da LocaWeb) — enquanto isso não for validado, é o maior risco do cronograma (ver seção 9).
- **Frontend**: TypeScript + React + Vite, React Router (navegação/rotas protegidas), Axios (consumo da API). Estrutura já criada hoje em `frontend/`.
- **Documentação de API**: Swagger/OpenAPI (springdoc-openapi) — ainda não adicionado ao projeto, previsto na Sprint 8.
- **Containerização**: Docker — citado na documentação do TG como tecnologia envolvida, ainda não configurado (`Dockerfile`/`docker-compose` previstos na Sprint 8, opcional para a entrega se o tempo apertar).
- **E-mail**: hoje simulado (`ConsoleEmailService` só loga o link/código no console do backend) — precisa virar SMTP de verdade (Gmail com senha de app, Mailtrap ou Brevo têm planos gratuitos) antes da Sprint 3, já que RF02 e RF10 dependem de e-mail real chegar ao usuário.
- **Alternativa citada, não adotada por ora**: OAuth2 com Keycloak ou Spring Authorization Server — a documentação do TG cita isso como opção para cenários corporativos com mais controle de sessão/revogação; não é necessário para o escopo do TG, mas vale citar na banca como algo considerado.
- **IA**: citada na documentação como algo que pode agregar valor (recomendações, análise), mas não é requisito funcional — tratar como extensão futura, não como entrega obrigatória (ver seção 8, Fora de escopo).

## 4. Estrutura de pastas

```
backend/
└── src/main/java/com/javaliday/backend/
    ├── config/        # SecurityConfig (JWT, CORS)
    ├── security/       # JwtUtil, JwtAuthFilter
    ├── model/           # entidades JPA
    ├── repository/       # interfaces Spring Data JPA
    ├── dto/                # request/response da API
    ├── service/             # regras de negócio
    ├── controller/           # endpoints REST
    └── exception/              # tratamento de erros

frontend/
└── src/
    ├── pages/          # telas (Login, Register, ForgotPassword, ResetPassword, Home, ...)
    ├── services/        # api.ts (axios) e chamadas por domínio
    ├── context/          # AuthContext (sessão/JWT)
    ├── components/        # componentes reutilizáveis (ex.: RotaProtegida)
    └── types/              # tipos TypeScript compartilhados com os DTOs do backend
```

À medida que Gerenciar Pallet, Empilhadeira, Estrutura e Simulação forem entrando, cada um ganha seu próprio `model`/`repository`/`dto`/`service`/`controller` no backend e sua própria pasta em `pages/` no front — mesmo padrão usado hoje para `auth`.

## 5. Schema (banco de dados)

O que já existe hoje (criado automaticamente pelo Hibernate a partir das entidades):

- **`usuarios`**: `id`, `nome`, `email` (único), `senha_hash`, `cnpj`, `telefone`, `empresa`, `cargo`, `tipo_usuario`, `email_confirmado`, `criado_em`.
- **`email_confirmation_tokens`**: `id`, `codigo`, `usuario_id`, `expira_em`, `usado`.
- **`password_reset_tokens`**: `id`, `token` (único), `usuario_id`, `expira_em`, `usado`.
- **`pallets`** (Sprint 3, concluída): `id`, `tipo`, `frente_mm`, `profundidade_mm`, `altura_mm`, `peso_kg`, `caracteristica` (opcional), `usuario_id`, `criado_em`.

O que vem a seguir, direto do diagrama de classes/ER do TG:

| Tabela | Campos principais | Observação |
|---|---|---|
| `empilhadeiras` | id, altura de elevação, peso máximo, corredor de operação, nome, regra, `usuario_id` | RF05 |
| `estruturas` | id, nome, regra de compatibilidade com pallet, regra de compatibilidade com empilhadeira, texto informativo | Cadastrada só pelo Administrador (RF06) |
| `simulacoes` | id, `usuario_id`, data, área do local, altura do local, tipo de superfície, custo previsto, resultado | RF07 — o "cérebro" do sistema |
| `simulacao_pallet` / `simulacao_empilhadeira` / `simulacao_estrutura` | `simulacao_id` + `pallet_id`/`empilhadeira_id`/`estrutura_id` | Tabelas de associação — uma simulação pode envolver mais de um pallet/empilhadeira/estrutura |
| `historico` | id, data da modificação, documento alvo | Auditoria de alterações nos cadastros (aparece no diagrama de classes, ainda sem RF numerado correspondente — vale confirmar com a orientadora se entra no escopo do TG ou fica como extensão) |

## 6. Segurança (Spring Security)

Regra geral, já implementada hoje para `/api/auth/**` e a ser estendida para os próximos endpoints:

| Endpoint | Quem acessa |
|---|---|
| `/api/auth/**` (register, login, forgot/reset password) | Público |
| `/api/pallets/**`, `/api/empilhadeiras/**` | Usuário autenticado (só enxerga/edita os próprios registros) |
| `/api/estruturas/**` (escrita) | Só `admin` |
| `/api/estruturas/**` (leitura) | Qualquer usuário autenticado |
| `/api/usuarios/**` (gestão de usuários) | Só `admin` |
| `/api/simulacoes/**` | Usuário autenticado |

Isso mapeia para uma `@PreAuthorize("hasRole('ADMIN')")` (ou equivalente) nos endpoints de Estrutura/Usuário assim que o campo `tipoUser`/papel existir na entidade `Usuario` e for incluído no JWT.

## 7. Fluxo de Git/GitHub

- Repositório do grupo: `PWEB`, pasta `ProjetoFinal/` — só uma pessoa do grupo precisa entregar.
- Uma branch por funcionalidade, saindo de `main`: `feat/nome-da-feature` (ex.: `feat/gerenciar-pallet`); correções pequenas em `fix/nome-do-problema`.
- Commits no padrão Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`) — um commit por peça lógica de trabalho.
- Ao concluir a feature: merge para `main`, apaga a branch, segue para a próxima. Sem PR obrigatório dado o tamanho do grupo, mas vale revisar em dupla antes do merge quando der.

## 8. Sprints propostas

| Sprint | Entrega | Requisitos cobertos |
|---|---|---|
| 0 | Setup do backend (Spring Boot + Security + JWT) e do frontend (React + Vite), telas de Login/Registro/Esqueci-Redefinir senha | RF03, RF09, RF10 (parcial) — **entregue hoje** |
| 1 | Ajustar cadastro de usuário com CNPJ/empresa/telefone/cargo/tipoUser + confirmação de e-mail com código | RF01, RF02 |
| 2 | Fechar o fluxo de conta: e-mail real via SMTP (troca do `ConsoleEmailService`), logout explícito, editar perfil | RF08, RF09/RF10 (envio real), RF12 |
| 3 | Gerenciar Pallet (cadastrar/alterar/excluir/pesquisar) | RF04 |
| 4 | Gerenciar Empilhadeira | RF05 |
| 5 | Gerenciar Estrutura (admin) | RF06 |
| 6 | Gerenciar Usuário (admin) — painel de administração | RF11 |
| 7 | Fazer Simulação — definir e implementar a regra de cálculo (ver seção 9) e a tela de resultado | RF07 |
| 8 | Imprimir Simulação em PDF + Swagger/OpenAPI + (opcional) Docker | RF13 |
| 9 | Hospedagem (frontend + backend em plataforma gratuita), testes ponta a ponta, ajustes de responsividade | Critérios de avaliação: hospedagem, responsividade |
| 10 | Revisão final da documentação do TG (preencher Canvas do Modelo de Negócio e agradecimentos, hoje ainda com texto de exemplo), ensaio de apresentação | Entrega do TG |

Sprints 3 a 6 seguem todas o mesmo padrão de CRUD (cadastrar/alterar/excluir/pesquisar), então tendem a ser rápidas depois que a primeira (Pallet) estiver pronta e servir de modelo para as outras.

## 9. Decisões de regra de negócio em aberto

Diferente do resto do documento, a **regra de cálculo da simulação (RF07)** ainda não está definida em nenhum dos materiais do TG — nem a fórmula de quantas posições de pallet cabem numa área, nem o critério de ranqueamento entre estruturas concorrentes. Isso precisa virar uma decisão explícita do grupo antes da Sprint 7, provavelmente com a mesma lógica de "fonte única de verdade calculada pelo sistema" que já usamos para `senha_hash` e vamos usar para os demais campos derivados — por exemplo:

- Quantidade de posições = função de (área disponível, altura do local, altura/frente/profundidade do pallet, corredor de operação da empilhadeira, regra de compatibilidade da estrutura).
- Ranqueamento entre estruturas compatíveis = por critério de custo? De densidade de armazenagem? De ambos, com peso?

Outras pendências que vale registrar aqui para não se perder ao longo das sprints:

- Confirmar se o Postgres da LocaWeb aceita conexão externa (ver seção 3) — se não aceitar, plano B é um Postgres gratuito (Neon/Supabase) só para desenvolvimento/entrega.
- Definir o provedor de e-mail real antes da Sprint 2.
- Confirmar se a tabela `historico` (auditoria) do diagrama de classes entra no escopo do TG ou é extensão futura.

## 10. Fora de escopo por agora

- Inteligência artificial (citada como possível diferencial, não como requisito funcional).
- OAuth2/Keycloak (JWT simples já atende o escopo do TG).
- Publicação em app store / geração de `.apk` (o projeto é web).
- Docker em produção, se o cronograma apertar — pode ficar só documentado como próximo passo na apresentação do TG.

## 11. Padrão visual das telas

Já criado hoje e reaproveitado daqui para frente: cartão centralizado (`auth-card`), campos com `label` acima do input, alerta de erro/sucesso em faixa colorida, azul `#2563eb` como cor de destaque dos botões. As próximas telas de CRUD (Pallet, Empilhadeira, Estrutura, Usuário) devem seguir o mesmo padrão de lista + formulário que a documentação do TG descreve nos casos de uso (lista cadastrada → cadastrar/alterar/excluir a partir dela), reaproveitando componentes visuais em vez de estilizar tela por tela.

## 12. Status atual (21/08/2026)

Entregue:
- Sprint 0/1 — backend com registro (CNPJ/telefone/empresa/cargo + confirmação de e-mail por código de 6 dígitos), login (bloqueado até confirmar e-mail) e esqueci/redefinir senha (JWT, BCrypt, validação real de CNPJ, tratamento de erros); frontend com as telas correspondentes, rota protegida e visual reaproveitado do protótipo do grupo (cores, FontAwesome, SweetAlert2).
- Sprint 3 — RF04 (Gerenciar Pallet) completa: backend (`/api/pallets`, CRUD + pesquisa por tipo, sempre filtrado por usuário) e tela `/pallets` no frontend, com o mesmo padrão visual do painel esquerdo de `simulacao.html`.

Próximo passo recomendado: RF05 (Gerenciar Empilhadeira), seguindo o mesmo padrão de CRUD já validado em Pallet (é praticamente copiar model/repository/dto/service/controller e a tela, trocando os campos).
