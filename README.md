# Annona

Aplicativo Android para **gestão inteligente da despensa doméstica**. Cadastre o que você tem em casa, acompanhe a validade com um semáforo visual e evite o desperdício de alimentos e produtos com dicas de armazenamento, sugestões de receitas baseadas na sua despensa e apoio à doação.

> Projeto desenvolvido na disciplina de **Programação para Dispositivos Móveis (Mobile I e Mobile II)**.

---

## Sumário

1. [Sobre o Projeto e Problema que Resolve](#sobre-o-projeto-e-problema-que-resolve)
2. [Principais Funcionalidades](#principais-funcionalidades)
3. [Semáforo de Validade](#semáforo-de-validade)
4. [Arquitetura e Tecnologias](#arquitetura-e-tecnologias)
5. [Estrutura de Pastas](#estrutura-de-pastas)
6. [Rotas da API REST](#rotas-da-api-rest)
7. [Como Executar o Projeto](#como-executar-o-projeto)
8. [Equipe](#equipe)

---

## Sobre o Projeto e Problema que Resolve

Em muitas casas, alimentos e outros produtos são esquecidos no fundo do armário ou da geladeira e passam da validade antes de serem usados. O resultado é dinheiro jogado fora e comida desperdiçada.

O **Annona** funciona como um inventário inteligente da despensa:
- O usuário cadastra o que comprou (com sugestões automáticas de 40 itens padrão que já preenchem categoria e ícone);
- O sistema acompanha os prazos com cores fáceis de entender (**semáforo de validade**);
- Exibe **dicas personalizadas de armazenamento** conforme a categoria e o local escolhido;
- Sugere **receitas simples** filtradas pelos ingredientes que a pessoa já tem em casa;
- Oferece o **Modo Doação**, listando itens próximos do vencimento e gerando uma mensagem pronta para copiar ou compartilhar;
- Registra o **Histórico** de itens consumidos, doados ou desperdiçados.

---

## Principais Funcionalidades

- **Autenticação e Cadastro (`RF01`, `RF02`, `RF03`, `RNF08`, `RNF09`)**: Criação de conta, login validado pela API com senha em hash (`scrypt`), autenticação via token JWT e sessão persistente no aparelho (`SharedPreferences`).
- **Gestão da Despensa (`RF04` a `RF14`)**:
  - Cadastro com autocomplete de 40 itens padrão;
  - Edição completa preservando quantidade decimal, unidade de medida e preço;
  - Alternância rápida entre **"Por consumir"** (fechado) e **"Consumindo"** (aberto) pelo selo do card ou menu;
  - Exclusão lógica informando o motivo (*Item consumido*, *Item vencido/estragado*, *Item doado* ou *Outro*);
  - Lista ordenada por prioridade de vencimento e filtros por estado do semáforo ou consumo.
- **Receitas Inteligentes (`RF17`, `RF18`, `RF19`, `RF22`)**:
  - Busca receitas na API e cruza com os itens da despensa do usuário, destacando no card quais ingredientes ele já possui;
  - Detalhes completos com tempo de preparo, porções, ingredientes e passo a passo ilustrado.
- **Modo Doação (`RF15`, `RN12`, `RN13`)**:
  - Reúne automaticamente itens em estado **Crítico** ou **Atenção** que ainda estão fechados;
  - Permite retirar itens que o usuário não deseja doar ou marcar como doados;
  - Gera relatório pronto com botão de **Copiar** para a área de transferência (`ClipboardManager`) e **Compartilhar**.
- **Perfil, Notificações e Histórico (`RF16`, `RF20`, `RF21`)**:
  - Alertas locais sobre itens críticos ou vencidos, com chave para ativar/desativar sincronizada com a API;
  - Tela de **Histórico** contabilizando itens consumidos, doados e desperdiçados;
  - Tela de **Ajustes** permitindo configurar o endereço da API (emulador, rede local ou túnel `ngrok`).

---

## Semáforo de Validade

| Estado | `id_flag` | Cor no App | Regra de Cálculo | Significado |
| :--- | :---: | :---: | :--- | :--- |
| **Vencido** | `1` | Marrom | Data de validade já passou (`dias < 0`) | Não deve ser consumido. Fica fora da lista de doação. |
| **Crítico** | `2` | Vermelho | Vence hoje ou em até 3 dias (`0 a 3 dias`) | Consumir ou doar com urgência. |
| **Atenção** | `3` | Amarelo | Vence entre 4 e 7 dias (`4 a 7 dias`) | Planejar o consumo; candidato à doação. |
| **Seguro** | `4` | Verde | 8 dias ou mais, ou item sem data de validade | Sem pressa. |

---

## Arquitetura e Tecnologias

| Camada | Tecnologia | Função |
| :--- | :--- | :--- |
| **Interface (Mobile)** | Android Nativo (Kotlin), XML Layouts, View Binding, Fragments, ViewPager2, RecyclerView | Exibe as telas, gerencia interações e estado visual. |
| **Comunicação** | Retrofit 2 + OkHttp + Gson + Coroutines (`ApiClient`) | Realiza chamadas HTTP/REST assíncronas com envio automático de token JWT. |
| **Sessão Local** | `SharedPreferences` (`SecaoUsuario`) | Guarda dados da sessão do usuário, preferências de notificação e URL da API. |
| **Servidor (API)** | Node.js + Express 5 (ES Modules) + `dotenv` | Processa regras de negócio, calcula o semáforo, valida dados e acessa o banco. |
| **Banco de Dados** | Banco Local Automático (`node:sqlite` + `script.sql`) ou MySQL (`mysql2/promise`) | Cria e popula automaticamente as 9 tabelas, 40 itens padrão e 9 receitas na própria máquina ao iniciar a API. |
| **Conexão Local / Rede** | `http://10.0.2.2:8080/` (Emulador) / Rede Wi-Fi local / `ngrok` (opcional) | Funciona 100% localmente sem depender de servidor externo ou túnel de terceiros. |

---

## Estrutura de Pastas

```text
annona/
├── api/                                # Servidor Node.js + Express
│   ├── controllers/
│   │   ├── info_geral.controller.js    # Listas de apoio (medidas, locais, categorias, flags, itens padrão)
│   │   ├── itens_padrao.controller.js  # Autocomplete de 40 itens padrão
│   │   ├── receitas.controller.js      # Listagem de receitas e sugestão pela despensa
│   │   └── users.controller.js         # Login, cadastro, CRUD despensa, semáforo, doação, histórico e perfil
│   ├── routers/                        # Definição das rotas Express
│   ├── scripts_bd/
│   │   └── script.sql                  # Criação das 9 tabelas e carga inicial completa
│   ├── bd.js                           # Motor híbrido (Banco Local SQLite automático via script.sql ou MySQL)
│   ├── index.js                        # Ponto de entrada do servidor Express
│   ├── package.json                    # Dependências e script npm start
│   └── teste.env                       # Modelo de variáveis de ambiente (.env)
└── app/                                # Aplicativo Android (Kotlin)
    └── src/main/
        ├── java/com/example/annona/
        │   ├── AdaptadorItem.kt        # Adapter dos cards de itens da despensa/doação/histórico
        │   ├── AdicionarItem.kt        # Tela de cadastro e edição de item
        │   ├── ApiClient.kt            # Singleton do Retrofit + OkHttp
        │   ├── DetalheReceitaActivity.kt # Passo a passo da receita
        │   ├── Doacoes.kt              # ViewModel compartilhado entre abas
        │   ├── DoacoesFragment.kt      # Aba Doação, cópia e compartilhamento
        │   ├── HistoricoActivity.kt    # Tela de histórico de consumo/desperdício/doação
        │   ├── Item.kt                 # Modelo Item, cálculo do semáforo e tabelas de apoio
        │   ├── MainActivity.kt         # Container principal com ViewPager2 e BottomNavigationView
        │   ├── NotificacaoValidadeHelper.kt # Alertas locais de vencimento
        │   ├── Receitas.kt             # Modelos Receita e EtapaPreparo
        │   ├── ReceitasAdapter.kt      # Adapter dos cards de receitas
        │   ├── ReceitasData.kt         # Catálogo de receitas e cruzamento com a despensa
        │   ├── ReceitasFragment.kt     # Aba Receitas com filtro pela despensa e por tipo
        │   ├── SecaoUsuario.kt         # Gerenciamento de sessão (SharedPreferences)
        │   ├── ServicoApi.kt           # Interface Retrofit e DTOs de requisição/resposta
        │   ├── TelaCadastro.kt         # Tela de criação de conta de usuário
        │   ├── TelaDespensaFragment.kt # Aba Despensa (semáforo, filtros e ações)
        │   ├── TelaLogin.kt            # Tela de login
        │   └── TelaPerfilFragment.kt   # Aba Perfil, notificações, histórico e ajustes
        └── res/                        # Layouts XML, drawables, cores e strings
```

---

## Rotas da API REST

| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `POST` | `/users/login` | Valida e-mail e senha (com suporte a hash) e devolve dados do usuário + token JWT. |
| `POST` | `/users/cadastro` | Cria uma nova conta de usuário com senha criptografada em hash. |
| `GET` | `/users/listarItens/:id_usuario` | Lista os itens ativos da despensa do usuário já com semáforo calculado e ordenados por prioridade. |
| `POST` | `/users/ItensUsuario` | Cadastra um novo item na despensa do usuário. |
| `PUT` | `/users/editarItem/:id_item_usuario` | Atualiza os dados de um item conferindo o dono do registro. |
| `PUT` | `/users/itens/:id_item_usuario/consumo` | Alterna o estado do item entre `"Por consumir"` e `"Consumindo"`. |
| `DELETE` | `/users/deletarItem/:id_item_usuario` | Realiza a exclusão lógica (`deletado = TRUE`) gravando `razao_delecao`. |
| `GET` | `/users/:id_usuario/doacao` | Lista os itens candidatos à doação (Crítico ou Atenção, fechados e não removidos). |
| `PUT` | `/users/itens/:id_item_usuario/doacao` | Marca/desmarca um item como retirado da lista de doação (`fora_doacao`). |
| `GET` | `/users/:id_usuario/historico` | Lista os itens excluídos logicamente e o resumo de consumidos, desperdiçados e doados. |
| `PUT` | `/users/:id_usuario/notificacoes` | Atualiza a preferência de alertas de vencimento do usuário. |
| `GET` | `/itens_padrao` | Lista os 40 itens padrão para sugestões de preenchimento automático. |
| `GET` | `/infoGeral` | Devolve categorias, locais, medidas, flags e itens padrão em uma única chamada. |
| `GET` | `/receitas` | Lista todas as receitas cadastradas no banco. |
| `GET` | `/receitas/sugeridas/:id_usuario` | Sugere receitas que possuem ingredientes em comum com a despensa do usuário. |

---

## Como Executar o Projeto (100% Local e Automático)

O projeto foi configurado para que **qualquer integrante da equipe consiga rodar a API e o banco de dados na própria máquina com apenas um comando**, sem depender do computador de outra pessoa ligado com MySQL ou `ngrok`.

### Pré-requisitos
- **Node.js** (v22.5+ ou v24+)
- **Android Studio** (Ladybug / Meerkat ou superior) com SDK 24+

### 1. Iniciar a API e o Banco de Dados Local
Ao iniciar a API, o arquivo [`api/bd.js`](api/bd.js) lê automaticamente o script [`api/scripts_bd/script.sql`](api/scripts_bd/script.sql), cria o banco local (`annona_local.sqlite`) e popula todas as **9 tabelas, 40 itens padrão, 9 receitas e 3 usuários de teste** caso ainda não existam.

1. Abra o terminal na pasta `api`:
   ```bash
   cd api
   npm install
   npm start
   ```
2. O terminal exibirá:
   ```text
   [BD Local] Inicializando banco local a partir de script.sql...
   [BD Local] Banco de dados criado e populado automaticamente!
   Servidor Annona rodando com sucesso! Escutando em: http://localhost:8080
   ```
   *(Opcional: caso deseje utilizar um servidor **MySQL** externo em vez do banco local automático, basta definir `DB_MODE=mysql` no arquivo `.env` da pasta `api` — o sistema também criará o banco `annona` e executará o `script.sql` automaticamente no MySQL).*

### 2. Executar o Aplicativo Android
1. Abra a pasta raiz do projeto no **Android Studio**.
2. Aguarde a sincronização do Gradle e execute o app em um **Emulador Android** (o app já vem pré-configurado para acessar `http://10.0.2.2:8080/`, que aponta direto para o `localhost:8080` do seu computador).
3. **Usando em celular físico (opcional)**:
   - Caso queira testar em um aparelho físico na mesma rede Wi-Fi, abra o app em **Perfil > Ajustes** e altere a URL para `http://SEU_IP_LOCAL:8080/`.
4. Crie uma nova conta em **"Criar conta"** ou entre com um dos usuários de teste já cadastrados automaticamente:
   - E-mail: `sandy@gmail.com`, `ana@gmail.com` ou `walyson@gmail.com`
   - Senha: `123`

---

## Equipe

- **Sandy Lopes Silva**
- **Ana Francisca Mendes dos Santos**
- **Natália**
- **Walyson Gabriel**

Disciplina: **Programação para Dispositivos Móveis II** — Prof. Mayana.
