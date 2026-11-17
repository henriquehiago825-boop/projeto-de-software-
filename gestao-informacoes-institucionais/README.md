# Gestão de Informações Institucionais

MVP acadêmico para centralizar o cadastro, a consulta e a atualização de informações de uma instituição. A solução foi pensada para reduzir a dependência de anotações, planilhas e mensagens dispersas, facilitando a localização dos registros e diminuindo retrabalho.

## Funcionalidades

- Tela inicial com acesso às funções principais.
- Cadastro de informações com categoria, título, descrição e data.
- Consulta por título e filtro por categoria.
- Edição de registros já cadastrados.
- Persistência local com banco H2.
- Validação dos dados no navegador e no servidor.
- Acesso à API protegido por autenticação.
- Consultas ao banco realizadas pelo Spring Data JPA, sem montar SQL concatenando entradas do usuário.

## Tecnologias

- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, Bean Validation e Spring Security
- H2 Database
- HTML5, CSS3 e JavaScript
- Maven

## Requisitos para executar

- JDK 17 ou superior
- Maven 3.8 ou superior

## Como executar localmente

1. Extraia o ZIP e abra a pasta `gestao-informacoes-institucionais` no terminal ou na IDE.
2. Configure as credenciais de demonstração. No Windows PowerShell:

   ```powershell
   $env:APP_USER="admin"
   $env:APP_PASSWORD="CrieUmaSenhaForteAqui!"
   ```

   No macOS/Linux:

   ```bash
   export APP_USER=admin
   export APP_PASSWORD='CrieUmaSenhaForteAqui!'
   ```

3. Execute:

   ```bash
   mvn spring-boot:run
   ```

4. Acesse `http://localhost:8080`.
5. Ao cadastrar ou consultar, o navegador solicitará usuário e senha. Use os valores definidos no passo 2.

A API também pode ser testada em `/api/informacoes` após autenticação.

## API

| Método | Rota | Função |
|---|---|---|
| GET | `/api/informacoes` | Lista os registros |
| GET | `/api/informacoes?busca=aviso` | Pesquisa pelo título |
| GET | `/api/informacoes?categoria=Evento` | Filtra por categoria |
| POST | `/api/informacoes` | Cadastra um registro |
| PUT | `/api/informacoes/{id}` | Atualiza um registro existente |

Exemplo de corpo JSON para POST e PUT:

```json
{
  "categoria": "Comunicado",
  "titulo": "Novo horário de atendimento",
  "descricao": "O atendimento terá início às 8h a partir da próxima semana.",
  "dataReferencia": "2026-11-16"
}
```

## Estrutura do projeto

```text
gestao-informacoes-institucionais/
├── src/
│   ├── main/
│   │   ├── java/br/com/instituicao/gestao/
│   │   │   ├── config/       # Configuração de segurança
│   │   │   ├── controller/   # Endpoints REST e tratamento de erros
│   │   │   ├── dto/          # Validação dos dados recebidos
│   │   │   ├── model/        # Entidade Informacao
│   │   │   ├── repository/   # Persistência com Spring Data JPA
│   │   │   └── service/      # Regras de negócio
│   │   └── resources/
│   │       ├── static/       # Interface web
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Segurança e limitações do MVP

- Os campos são validados no cliente e no servidor, com limites de tamanho e obrigatoriedade.
- O acesso aos endpoints da API exige autenticação HTTP Basic; a senha é armazenada pelo Spring Security em formato Argon2id, nunca em texto puro.
- A camada de persistência usa Spring Data JPA e parâmetros vinculados, reduzindo o risco de SQL Injection.
- O JavaScript escapa conteúdo antes de inseri-lo na interface, reduzindo o risco de XSS refletido/armazenado.
- Não são coletados dados pessoais sensíveis no fluxo principal.
- HTTP Basic deve ser usado somente em ambiente local de demonstração sem TLS. Em publicação real, configure HTTPS e credenciais fortes por variáveis de ambiente/gestor de segredos.
- O usuário de demonstração é mantido em memória; para uma versão institucional real, deve ser substituído por cadastro persistente de usuários, perfis e política de recuperação de senha.
- O banco H2 em arquivo é adequado para demonstração local, não para alta concorrência ou produção.

## Escopo deliberadamente limitado

O MVP não inclui gestão de usuários, permissões por departamento, anexos, notificações, relatórios avançados ou integração com sistemas externos. O foco é validar o fluxo essencial de cadastrar, localizar e atualizar informações em um repositório centralizado.

## Testes

Execute `mvn test` para rodar a suíte automatizada. Antes de publicar, também valide manualmente: cadastro válido, campos vazios, descrição acima de 500 caracteres, pesquisa por título, filtro por categoria, edição de registro e tentativa de acessar a API sem autenticação.
