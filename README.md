# Estoque Model — TG (Tecnólogo em ADS, Fatec Sorocaba)

API em **Java (Spring Boot 3 + Spring Security + JWT)** e front-end em
**TypeScript (React + Vite)**, com persistência em **PostgreSQL**.

Hoje foram implementadas as telas de **Registro (cadastro de usuário)**,
**Confirmar e-mail**, **Login** e **Esqueci/Redefinir minha senha** — a base
de autenticação completa (RF01, RF02, RF03, RF09, RF10 da documentação do
TG). Veja o `plano-tecnico-estoque-model.md` enviado junto para o contexto
completo do projeto e a ordem das próximas sprints (RF04 em diante:
Gerenciar Pallet, Empilhadeira, Estrutura, Usuário e Fazer Simulação).

## Estrutura do projeto

```
estoque-model/
├── backend/     # API Spring Boot (Java 21, Maven)
└── frontend/    # SPA React + TypeScript (Vite)
```

## 1. Banco de dados (PostgreSQL na LocaWeb)

Antes de rodar o backend, **teste se dá para conectar no Postgres da LocaWeb
de fora da rede deles** (do seu computador). Isso ainda não foi confirmado
e é o maior risco do projeto até aqui.

Teste com `psql` ou um cliente gráfico (DBeaver, pgAdmin, TablePlus):

```bash
psql "host=SEU_HOST_LOCAWEB port=5432 dbname=SEU_BANCO user=SEU_USUARIO sslmode=require"
```

- **Se conectar**: ótimo, use essa string de conexão nas variáveis de
  ambiente abaixo.
- **Se der timeout/recusar conexão**: hospedagem compartilhada às vezes só
  libera acesso ao Postgres *internamente* (só para apps rodando dentro da
  própria LocaWeb). Nesse caso, veja o painel da LocaWeb por uma opção de
  "acesso remoto" / liberar IP externo. Se não existir, os planos B são:
  - Rodar o back-end também na LocaWeb (se o plano de vocês permitir Java —
    a maioria dos planos de hospedagem da LocaWeb é PHP/Node, então confirme
    isso antes de contar com essa opção), ou
  - Usar um banco Postgres gratuito acessível de qualquer lugar (Neon,
    Supabase, Railway) só para o ambiente de desenvolvimento/entrega, e
    manter o da LocaWeb como o "banco oficial" se um dia ele for liberado.

## 2. Rodando o backend

Pré-requisitos: Java 21 e Maven.

```bash
cd backend

export DB_URL="jdbc:postgresql://SEU_HOST_LOCAWEB:5432/SEU_BANCO"
export DB_USERNAME="seu_usuario"
export DB_PASSWORD="sua_senha"
export JWT_SECRET="troque-por-uma-string-aleatoria-de-32-ou-mais-caracteres"

mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. As tabelas `usuarios`,
`email_confirmation_tokens`, `password_reset_tokens` e `pallets` são
criadas automaticamente (`ddl-auto=update`).

### Endpoints criados

| Método | Rota                         | Descrição                                                         |
|--------|------------------------------|--------------------------------------------------------------------|
| POST   | `/api/auth/register`         | Cadastra um novo usuário (nome, e-mail, senha, CNPJ, telefone, empresa/cargo opcionais) e envia o código de confirmação |
| POST   | `/api/auth/confirm-email`    | Confirma o cadastro com o código de 6 dígitos recebido (RF02)      |
| POST   | `/api/auth/resend-confirmation` | Reenvia um novo código de confirmação                           |
| POST   | `/api/auth/login`            | Autentica e retorna um JWT — bloqueado até o e-mail ser confirmado |
| POST   | `/api/auth/forgot-password`  | Gera token de redefinição (30 min de validade) e "envia" o link    |
| POST   | `/api/auth/reset-password`   | Troca a senha usando o token recebido                              |
| GET    | `/api/pallets?tipo=...`      | Lista os pallets do usuário logado (RF04); `tipo` filtra por texto (opcional) |
| POST   | `/api/pallets`               | Cadastra um novo pallet (tipo, frente/profundidade/altura em mm, peso em kg, característica opcional) |
| PUT    | `/api/pallets/{id}`          | Altera um pallet (só o dono pode)                                  |
| DELETE | `/api/pallets/{id}`          | Apaga um pallet (só o dono pode)                                   |

Todos os endpoints fora de `/api/auth/**` (como `/api/pallets/**`) já
exigem automaticamente o header `Authorization: Bearer <token>`, porque a
regra em `SecurityConfig` é: tudo fora de `/api/auth/**` exige autenticação.
Nos pallets, isso é reforçado de novo no `PalletService`: toda consulta é
filtrada por `usuario_id`, então um usuário nunca vê ou altera pallet de
outro, mesmo trocando o `id` na URL manualmente.

### Sobre os e-mails (confirmação de cadastro e redefinição de senha)

Como ainda não há um servidor de e-mail configurado, `ConsoleEmailService`
apenas **imprime o código/link no console/log do backend** quando alguém
chama `/api/auth/register` ou `/api/auth/forgot-password`. Isso permite
testar os dois fluxos inteiros hoje, inclusive em ambiente de
desenvolvimento sem SMTP. Para enviar e-mail de verdade depois, adicionem a
dependência `spring-boot-starter-mail` e implementem `EmailService` com
`JavaMailSender` (Gmail com "senha de app", Mailtrap ou Brevo têm planos
gratuitos e tutoriais fáceis de achar) — isso é a Sprint 2 do plano técnico.

### Validação de CNPJ

O campo `cnpj` do cadastro é validado de verdade (dígitos verificadores,
via a anotação `@CNPJ` em `validation/`), não só o formato — um CNPJ com os
14 dígitos mas checksum inválido é rejeitado com uma mensagem clara.

## 3. Rodando o frontend

Pré-requisitos: Node 18+.

```bash
cd frontend
cp .env.example .env   # ajuste VITE_API_URL se o backend não estiver em localhost:8080
npm install
npm run dev
```

Abre em `http://localhost:5173`, com as rotas:

- `/registro`
- `/confirmar-email` (chega aqui automaticamente após o cadastro; também
  acessível pelo link "Não recebeu o código?" no Login)
- `/login`
- `/esqueci-senha`
- `/redefinir-senha?token=...` (link que aparece no console do backend)
- `/` — tela inicial protegida (só acessível logado), com um botão para
  "Gerenciar Pallet"
- `/pallets` — RF04: cadastrar, listar, pesquisar por tipo, alterar e
  apagar pallets (layout reaproveitado do painel esquerdo de
  `simulacao.html`, só o visual)

## 4. GitHub

Sugestão de organização do repositório (mesmo padrão usado no restante do
curso, com a pasta do projeto final):

```
PWEB/
└── ProjetoFinal/   # ou o nome de pasta que a orientadora definir para o TG
    ├── backend/
    └── frontend/
```

Pontos que já ficam encaminhados com essa base:

- Tratamento de erros e validação de entrada (Bean Validation nos DTOs,
  incluindo validação real de CNPJ, + `GlobalExceptionHandler`).
- Persistência dos dados (JPA + PostgreSQL).
- Organização de código em camadas (controller/service/repository/dto).
- Senha sempre criptografada (BCrypt) — RNF08 da documentação do TG.

Pontos que ainda faltam (ver `plano-tecnico-estoque-model.md` para a ordem
sugerida das sprints):

- RF05/RF06 — Gerenciar Empilhadeira e Estrutura (RF04, Gerenciar Pallet,
  já está pronta — backend `/api/pallets` + tela `/pallets`).
- RF07 — Fazer Simulação (a regra de cálculo ainda não foi definida pelo
  grupo, ver seção 9 do plano técnico).
- RF08, RF11, RF12, RF13 — Logout explícito, Gerenciar Usuário (admin),
  Editar Perfil, Imprimir Simulação.
- Envio de e-mail real (SMTP) no lugar do `ConsoleEmailService`.
- Hospedar o site em plataforma gratuita e informar a URL na entrega.
- Responsividade (o CSS de hoje é simples e funcional, mas vale revisar em
  telas pequenas antes da entrega).

## 5. Dicas de hospedagem gratuita

- **Front-end (React/Vite)**: Vercel ou Netlify (free tier), deploy direto
  do repositório GitHub.
- **Back-end (Spring Boot)**: Render ou Railway (free tier) também fazem
  deploy direto do GitHub; configurem as mesmas variáveis de ambiente
  (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`) no painel deles.
- **Banco**: continua sendo o Postgres da LocaWeb, **desde que ele aceite
  conexão externa** (ver item 1) — senão, um Postgres gratuito (Neon/
  Supabase) resolve para desenvolvimento/entrega.
