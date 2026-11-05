# UpCar

<p align="center">
  <strong>Aplicativo Android de caronas universitárias</strong>
</p>

O **UpCar** é um aplicativo Android desenvolvido em Kotlin que simula a experiência de solicitar e acompanhar uma carona até um campus universitário. O projeto reúne cadastro de passageiro, consulta e seleção de motoristas, solicitação de carona, negociação simulada por chat, acompanhamento visual da corrida e avaliação.

> **Aviso:** o UpCar é um projeto acadêmico demonstrativo. A negociação com o motorista e o deslocamento da corrida são simulados. O aplicativo não conecta passageiros a motoristas reais nem fornece rastreamento de localização em tempo real.

---

## Telas do aplicativo

### 1. Tela inicial

Apresenta a identidade visual do UpCar e permite iniciar o fluxo de cadastro do passageiro.

![Tela inicial do UpCar](docs/images/01-boas-vindas.jpg)

### 2. Cadastro de passageiro

O usuário informa nome, RGM, e-mail e bairro de origem. Também pode selecionar uma foto para o perfil. O formulário valida os campos antes de permitir o avanço.

![Cadastro de passageiro](docs/images/02-cadastro-passageiro.jpg)

### 3. Busca e seleção de motoristas

Exibe um mapa com os motoristas disponíveis e uma lista de opções com informações como veículo, placa, horário, preço e avaliação. A seleção pode ser feita pela lista ou pelos marcadores do mapa.

![Busca de motoristas](docs/images/03-procura-motorista.jpg)

### 4. Chat e negociação

Após solicitar uma carona, o passageiro acessa uma conversa vinculada à solicitação. É possível enviar mensagens e apresentar uma contraproposta de valor. A resposta do motorista é simulada pelo aplicativo.

![Chat e negociação](docs/images/04-chat.jpg)

### 5. Corrida em andamento

Apresenta o mapa da corrida, a origem, o destino e a posição simulada do motorista. O progresso da viagem é controlado pelo próprio aplicativo.

![Corrida em andamento](docs/images/05-corrida-em-andamento.jpg)

### 6. Perfil do motorista

Mostra informações do motorista e dados apresentados pelo aplicativo, como nota e quantidade de corridas.

![Perfil do motorista](docs/images/06-perfil-motorista.jpg)

### 7. Corrida finalizada e avaliação

Ao finalizar a corrida, o passageiro visualiza um resumo e pode atribuir uma nota, selecionar elogios e escrever um comentário.

![Corrida finalizada e avaliação](docs/images/07-corrida-finalizada.jpg)

> **Observação sobre as imagens:** os caminhos acima seguem a organização de imagens documentada no projeto de referência. Caso os arquivos existentes em `docs/images/` tenham nomes diferentes, ajuste os caminhos para corresponder aos arquivos presentes no repositório.

---

## Funcionalidades

- **Cadastro de passageiro:** preenchimento e validação de nome, RGM, e-mail e bairro de origem.
- **Foto de perfil:** seleção de imagem por meio do seletor de mídia do Android.
- **Lista de motoristas:** consulta de opções com veículo, placa, horário, preço, nota e quantidade de corridas.
- **Mapa de motoristas:** visualização de marcadores no mapa e seleção de motorista.
- **Solicitação de carona:** criação de uma solicitação associada ao motorista escolhido.
- **Chat:** envio e remoção de mensagens vinculadas à carona.
- **Contraproposta:** envio de uma nova oferta de valor e resposta automática simulada.
- **Confirmação da carona:** confirmação da negociação dentro do fluxo demonstrativo.
- **Acompanhamento da corrida:** mapa com origem, destino e posição simulada do motorista.
- **Perfil do motorista:** consulta das informações disponíveis no aplicativo.
- **Avaliação:** atribuição de nota de 1 a 5, elogios e comentário após a corrida.
- **Persistência local:** armazenamento de passageiros, motoristas, caronas, mensagens e avaliações usando Room.

## Fluxo de utilização

1. O usuário abre o aplicativo e acessa a tela de boas-vindas.
2. Preenche o cadastro de passageiro e, se desejar, seleciona uma foto.
3. Consulta os motoristas disponíveis na lista e no mapa.
4. Seleciona um motorista e solicita uma carona.
5. Acessa o chat associado à solicitação e pode negociar o valor.
6. Confirma a carona após a resposta simulada.
7. Acompanha a corrida no mapa enquanto o progresso é simulado.
8. Finaliza a corrida e registra uma avaliação do motorista.

---

## Tecnologias utilizadas

| Tecnologia | Utilização |
| --- | --- |
| **Kotlin 2.1.20** | Linguagem principal do aplicativo. |
| **Android SDK** | Plataforma de execução Android. |
| **Jetpack Compose** | Construção declarativa das interfaces. |
| **Material 3** | Componentes e padrões visuais da interface. |
| **Navigation Compose 2.8.7** | Navegação entre telas e rotas com parâmetros. |
| **ViewModel** | Gerenciamento do estado e das regras da interface. |
| **StateFlow e Coroutines** | Exposição reativa de estados e execução de operações assíncronas. |
| **Room 2.8.5** | Persistência local sobre SQLite. |
| **KSP 2.3.12** | Processamento de anotações do Room. |
| **OSMDroid 6.1.20** | Exibição dos mapas e marcadores. |
| **Gradle Kotlin DSL** | Configuração e compilação do projeto. |
| **Android Gradle Plugin 9.3.3** | Plugin de compilação Android. |

As versões listadas foram consultadas no catálogo `gradle/libs.versions.toml` do projeto.

---

## Arquitetura do projeto

O aplicativo utiliza **MVVM (Model-View-ViewModel)** com separação entre interface, gerenciamento de estado e acesso aos dados.

O fluxo principal de dados é:

```text
Interface (Compose)
       |
       v
    ViewModel
       |
       v
   Repository
       |
       v
      DAO
       |
       v
   Banco Room
```

- **UI:** contém as telas, componentes reutilizáveis, navegação e tema. As telas observam os estados expostos pelos ViewModels e encaminham as ações do usuário.
- **ViewModel:** concentra o estado da interface e as operações do fluxo de caronas, motoristas e passageiros.
- **Repository:** intermedeia o acesso aos DAOs e mantém a camada de ViewModel desacoplada do Room.
- **DAO:** define as operações de leitura e escrita no banco de dados.
- **Room:** persiste os dados localmente no dispositivo. As consultas de leitura utilizam `Flow`, permitindo que as telas recebam atualizações quando os dados mudam.

### ViewModels e estados

| ViewModel | Responsabilidade |
| --- | --- |
| `MotoristaViewModel` | Observa e filtra motoristas, controla a seleção, cadastra e remove motoristas. Ao remover um motorista, também trata as caronas e mensagens relacionadas. |
| `CaronaViewModel` | Gerencia caronas, mensagens, contrapropostas, confirmação, progresso simulado da corrida e avaliações. |
| `PassageiroViewModel` | Gerencia os campos do cadastro, validação, seleção e carregamento da foto do passageiro. |

Cada ViewModel expõe um `StateFlow` público (`uiState`) e mantém o `MutableStateFlow` privado. As telas coletam os estados com `collectAsStateWithLifecycle()`.

### Repositories

O projeto possui um Repository para cada entidade persistida:

- `MotoristaRepository`
- `CaronaRepository`
- `MensagemRepository`
- `PassageiroRepository`
- `AvaliacaoRepository`

O `MotoristaRepository` também popula os motoristas iniciais quando a tabela está vazia. O `PassageiroRepository` concentra o acesso aos dados do passageiro e o carregamento da foto selecionada.

### ViewModel Factory

A classe `UpCarViewModelFactory` recebe os Repositories e fornece as dependências necessárias para criar os ViewModels utilizados pela navegação.

---

## Estrutura de diretórios

O código-fonte principal está localizado em `app/src/main/java/com/example/upcar/`.

```text
com/example/upcar/
├── MainActivity.kt
├── data/
│   ├── AppDatabase.kt
│   ├── Daos.kt
│   ├── Dados.kt
│   ├── Modelos.kt
│   └── repository/
│       ├── AvaliacaoRepository.kt
│       ├── CaronaRepository.kt
│       ├── MensagemRepository.kt
│       ├── MotoristaRepository.kt
│       └── PassageiroRepository.kt
├── ui/
│   ├── componentes/
│   │   ├── MapaCorrida.kt
│   │   ├── MapaMotoristas.kt
│   │   └── ComponentesComuns.kt
│   ├── navegacao/
│   │   └── AppNavigation.kt
│   ├── telas/
│   │   ├── BoasVindas.kt
│   │   ├── CadastroPassageiro.kt
│   │   ├── ProcuraMotorista.kt
│   │   ├── PerfilMotorista.kt
│   │   ├── Chat.kt
│   │   ├── CorridaEmAndamento.kt
│   │   └── CorridaFinalizada.kt
│   └── theme/
├── util/
│   ├── Constantes.kt
│   ├── Formatadores.kt
│   ├── Imagens.kt
│   └── Validacoes.kt
└── viewmodel/
    ├── CaronaViewModel.kt
    ├── MotoristaViewModel.kt
    ├── PassageiroViewModel.kt
    └── ViewModelFactory.kt
```

### Principais arquivos

| Arquivo | Descrição |
| --- | --- |
| `MainActivity.kt` | Inicializa a configuração do OSMDroid, o banco Room, os Repositories e a Factory. |
| `data/Modelos.kt` | Define as entidades persistidas no banco. |
| `data/Daos.kt` | Define as operações de acesso às entidades. |
| `data/AppDatabase.kt` | Configura o banco Room e o conversor de lista de textos usado nas avaliações. |
| `data/Dados.kt` | Fornece os motoristas iniciais do aplicativo. |
| `ui/navegacao/AppNavigation.kt` | Define as rotas e a navegação entre as telas. |
| `ui/componentes/MapaMotoristas.kt` | Exibe o mapa e os marcadores dos motoristas. |
| `ui/componentes/MapaCorrida.kt` | Exibe o mapa da corrida, a linha visual entre origem e destino e o marcador do motorista. |
| `viewmodel/` | Contém os ViewModels, seus estados e a Factory. |
| `data/repository/` | Contém a camada de acesso aos dados por entidade. |
| `util/` | Reúne constantes, validações, formatação e leitura de imagens. |

---

## Banco de dados

O banco local é implementado com Room e utiliza o nome `upcar_database`.

| Entidade | Tabela | Dados armazenados |
| --- | --- | --- |
| `Motorista` | `motoristas` | Informações do motorista, veículo, placa, horário, preço, nota, quantidade de corridas e coordenadas. |
| `Carona` | `caronas` | Motorista associado, origem, destino, horário, valor e estado de confirmação/negociação. |
| `Mensagem` | `mensagens` | Mensagens vinculadas a uma carona, horário e identificação de quem enviou. |
| `Passageiro` | `passageiros` | Dados do passageiro e URI da foto selecionada. |
| `AvaliacaoCorrida` | `avaliacoes` | Nota, comentário e elogios associados à corrida. |

Os dados são armazenados no próprio dispositivo. Não foi identificado no fluxo analisado um backend remoto ou sincronização entre dispositivos.

---

## Como executar o projeto

### Pré-requisitos

- Android Studio compatível com o Android Gradle Plugin utilizado pelo projeto.
- JDK compatível com a configuração do Android Gradle Plugin.
- Android SDK solicitado durante a sincronização do Gradle.
- Emulador Android ou dispositivo físico com Android 7.0 (API 24) ou superior.

### Clonar o repositório

```bash
git clone https://github.com/Lorenzovanelli/UpCar4.git
cd UpCar4
```

### Abrir no Android Studio

1. Abra o Android Studio.
2. Selecione **Open** e escolha a pasta `UpCar4`.
3. Aguarde a sincronização do Gradle.
4. Instale os componentes do Android SDK solicitados, caso necessário.
5. Selecione um emulador ou conecte um dispositivo Android.
6. Execute a configuração `app`.

### Compilar pelo terminal

No Windows:

```powershell
.\gradlew.bat assembleDebug
```

No Linux ou macOS:

```bash
./gradlew assembleDebug
```

---

## Limitações atuais

- A negociação entre passageiro e motorista é simulada; não existe comunicação com motoristas reais.
- O progresso e a posição do motorista durante a corrida são simulados.
- Os dados ficam armazenados localmente no dispositivo, sem sincronização por servidor identificada.
- Os motoristas iniciais são dados demonstrativos inseridos quando a tabela está vazia.
- O funcionamento e a exibição dos mapas dependem de conexão com a internet para carregar os mapas.
- O aplicativo é uma demonstração acadêmica e não deve ser utilizado como serviço real de transporte.

---

## Perguntas

- Como estava o projeto no Trabalho 3, e o que mudou na reorganização?
  
R = No Trabalho 3 o Room já funcionava, mas o código estava todo misturado. Todos os arquivos ficavam no mesmo pacote e existia um único CaronaViewModel que falava direto com os cinco DAOs. Ele carregava os dados do banco uma vez, no init, e guardava tudo em listas e mapas do Compose (mutableStateListOf, mutableStateMapOf). Por isso cada alteração tinha que ser feita duas vezes: na lista em memória e no banco.
Na reorganização separamos o projeto em camadas: a tela chama o ViewModel, o ViewModel chama o Repository e o Repository chama o DAO. Os DAOs passaram a devolver Flow, então o Room avisa sozinho quando a tabela muda e a tela atualiza sem precisar buscar de novo. O estado de cada tela foi agrupado em um UiState exposto por StateFlow, e os ViewModels passaram a ser criados por uma ViewModelFactory. Nenhuma tela ou funcionalidade nova foi criada.

- Como o trio decidiu dividir os pacotes — o que foi para ui, data, data.repository e
viewmodel?

R = ui: tudo que é @Composable: as telas, os componentes reutilizáveis (barra, avatar, mapas), a navegação e o tema. Nenhum desses arquivos tem lógica de banco.
data: as @Entity, os @Dao e o @Database, como já eram no Trabalho 3, mais os motoristas iniciais.
data.repository: os cinco Repositories novos, um por Entity.
viewmodel: os três ViewModels (motoristas, caronas e passageiro), cada um com seu UiState, e a ViewModelFactory. Criamos também um pacote util para funções sem estado, como validações, formatadores, constantes e a leitura da foto. A MainActivity ficou na raiz porque o AndroidManifest.xml a referencia como MainActivity.

- Por que o Repository existe — o que ele resolve que não existia antes?
  
R = Antes o ViewModel dependia diretamente do Room, então qualquer mudança na origem dos dados mexia no ViewModel. Agora só o Repository conhece o DAO. A tela nunca fala com ele e o ViewModel só enxerga o Repository. Se um dia os dados vierem de uma API, só o Repository muda, e o ViewModel e as telas continuam iguais. Também deixou o ViewModel mais limpo: o acesso à foto escolhida na galeria e a carga dos motoristas iniciais ficaram no Repository.

- O que tem dentro do UiState de cada tela, e por que escolheram esses campos?
  
R = MotoristaUiState: a lista de motoristas, o texto da busca por destino e o motorista selecionado. A lista filtrada é calculada a partir deles. É tudo que a tela de motoristas precisa para desenhar a lista e o mapa.
CaronaUiState: motoristas, caronas, mensagens, avaliações, progresso da corrida e corridas finalizadas. Chat, corrida em andamento, corrida finalizada e perfil leem essas mesmas informações. Colocamos os motoristas junto para essas telas encontrarem o motorista de cada carona.
PassageiroUiState: o passageiro salvo, os campos do formulário (nome, RGM, e-mail, bairro), a foto, se o usuário já tentou avançar e as mensagens de erro. Com isso a validação saiu da tela. Deixamos como estado local da tela só o que é puramente visual: diálogos abertos, o rascunho da mensagem do chat e os campos dos formulários de nova carona e de avaliação.

- Alguma funcionalidade quebrou durante a reorganização? Como encontraram e corrigiram?
  
R = Remoção de motorista: o código apagava as caronas da lista em memória antes de consultar quais caronas deveriam sair do banco, então as caronas do motorista removido ficavam no banco. Com a lista vindo do Room, elas reapareceriam na tela. Corrigimos lendo as caronas antes de remover.
Abertura do chat: como o Room responde de forma assíncrona, o chat podia abrir antes de a carona aparecer no estado e mostrar “não encontrado”. Corrigimos fazendo a navegação só acontecer depois que a carona chega ao uiState. Toque duplo: com o estado vindo do banco, dois toques rápidos podiam repetir uma ação, como confirmar ou avaliar duas vezes. O ViewModel agora ignora a repetição enquanto a primeira ainda está em andamento.

- Algum integrante do trio teve dificuldade em algum ponto? Como resolveram?
R = Não, todos nós conseguirmos desempenhar conforme oq foi pedido.

## Objetivo acadêmico

O UpCar tem como objetivo demonstrar o desenvolvimento de um aplicativo Android com navegação entre telas, gerenciamento de estado, persistência local, mapas e organização de código baseada em MVVM. A implementação também permite praticar o uso de Kotlin, Jetpack Compose, Coroutines, Flow e Room em um projeto integrado.

---

## Desenvolvedores

Projeto acadêmico desenvolvido por:

- **Lorenzo Vanelli**
- **Luan Henrique Neuwirth Castilho**
- **Murilo de Mattos Opis**

---

<p align="center">
  Desenvolvido como projeto acadêmico — UpCar
</p>

