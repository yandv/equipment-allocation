# Sistema de Alocação de Equipamentos

Este é um sistema distribuído para gerenciamento e alocação de equipamentos, desenvolvido utilizando Java RMI (Remote Method Invocation) e PostgreSQL.

### Schema do Banco de Dados

O banco de dados possui as seguintes tabelas:

- `usuarios`: Tabela de usuários
- `equipamentos`: Tabela de equipamentos
- `reservas`: Tabela de reservas

Um usuário e um equipamento podem ter várias reservas, sendo que uma reserva será identificada pelo id do equipamento e o id do usuário.
O que é necessário para inicializar o banco está no arquivo `schema.sql`. 

## Descrição do Projeto

O sistema permite o gerenciamento de equipamentos, usuários e reservas através de uma arquitetura cliente-servidor utilizando Java RMI. A aplicação é composta por três serviços principais:

1. **EquipamentoService**: Gerencia o cadastro e consulta de equipamentos
2. **ReservaService**: Controla as reservas de equipamentos
3. **UsuarioService**: Gerencia o cadastro e autenticação de usuários

## Arquitetura

O projeto segue uma arquitetura em camadas:

- **Model**: Contém as entidades do sistema
- **Repository**: Implementa o acesso aos dados
- **Service**: Implementa a lógica de negócio
- **Database**: Gerencia a conexão com o banco de dados PostgreSQL

## Requisitos

- Java 17 ou superior
- PostgreSQL
- Maven

## Configuração

1. Configure o arquivo `properties.yaml` com as credenciais do banco de dados e o hostname e porta do servidor:
```yaml
hostname: localhost
port: 1099

database:
  host: localhost
  port: 5432
  username: seu_usuario
  password: sua_senha
  database: nome_do_banco
```

2. Faça o build do projeto:
```bash
mvn clean install
```

3. Execute o servidor:
```bash
java -cp target/equipment-allocation.jar br.ufrrj.common.Server
```

4. Execute o cliente:
```bash
java -cp target/equipment-allocation.jar br.ufrrj.common.Client
```

## API

### EquipamentoService

- `cadastrarEquipamento(Equipamento equipamento)`: Cadastra um novo equipamento
- `listarEquipamentos()`: Lista todos os equipamentos cadastrados
- `buscarEquipamentoPorId(int id)`: Busca um equipamento específico
- `atualizarEquipamento(Equipamento equipamento)`: Atualiza dados de um equipamento
- `removerEquipamento(int id)`: Remove um equipamento do sistema

### ReservaService

- `criarReserva(Reserva reserva)`: Cria uma nova reserva
- `listarReservas()`: Lista todas as reservas
- `buscarReservaPorId(int id)`: Busca uma reserva específica
- `cancelarReserva(int id)`: Cancela uma reserva existente

### UsuarioService

- `cadastrarUsuario(Usuario usuario)`: Cadastra um novo usuário
- `autenticarUsuario(String login, String senha)`: Autentica um usuário
- `buscarUsuarioPorId(int id)`: Busca um usuário específico
- `atualizarUsuario(Usuario usuario)`: Atualiza dados de um usuário

## Cliente (Client.java)

O `Client.java` é a interface de linha de comando (CLI) que permite a interação com o sistema. Ele implementa um menu interativo que se conecta aos serviços RMI do servidor.

### Funcionalidades Principais

1. **Menu Principal**
   - Gerenciar Usuários
   - Gerenciar Equipamentos
   - Gerenciar Reservas

2. **Menu de Usuários**
   - Criar usuário
   - Consultar usuário por ID
   - Listar todos os usuários

3. **Menu de Equipamentos**
   - Criar equipamento
   - Atualizar equipamento
   - Excluir equipamento
   - Consultar equipamento por ID
   - Listar todos os equipamentos

4. **Menu de Reservas**
   - Alocar equipamento
   - Devolver equipamento
   - Verificar disponibilidade
   - Consultar alocações de um usuário
   - Consultar alocações de um equipamento

### Características de Implementação

- **Conexão com Servidor**: Utiliza Java RMI para se conectar aos serviços remotos
- **Tratamento de Erros**: Implementa reconexão automática em caso de falha de comunicação
- **Interface Amigável**: Menu interativo com opções numeradas e feedback claro
- **Validação de Dados**: Verifica a existência de recursos antes de operações
- **Formatação de Datas**: Utiliza `SimpleDateFormat` para manipulação de datas

### Exemplo de Uso do Cliente

```bash
# Iniciar o cliente
java -cp target/equipment-allocation.jar br.ufrrj.common.Client

# Exemplo de fluxo de uso:
1. Escolha "1" para Gerenciar Usuários
2. Escolha "1" para Criar usuário
3. Digite o nome do usuário
4. O sistema retornará o ID do usuário criado
```

### Tratamento de Erros

O cliente implementa um mecanismo de reconexão automática que:
- Tenta reconectar até 3 vezes em caso de falha
- Exibe mensagens claras de erro para o usuário
- Mantém o estado da aplicação consistente

## Justificativas de Implementação

1. **Java RMI**: Escolhido por ser uma tecnologia nativa do Java para comunicação distribuída, oferecendo:
   - Simplicidade na implementação
   - Integração natural com Java
   - Suporte a invocação remota de métodos

2. **PostgreSQL**: Selecionado como banco de dados por:
   - Robustez e confiabilidade
   - Suporte a transações ACID
   - Excelente performance com grandes volumes de dados

3. **Arquitetura em Camadas**: Implementada para:
   - Melhor organização do código
   - Facilidade de manutenção
   - Separação clara de responsabilidades

4. **Repository Pattern**: Utilizado para:
   - Abstrair o acesso aos dados
   - Facilitar a troca de implementação do banco de dados
   - Centralizar a lógica de acesso aos dados

## Exemplos de Uso

### Cadastro de Equipamento
```java
Equipamento equipamento = new Equipamento();
equipamento.setNome("Microscópio");
equipamento.setDescricao("Microscópio profissional");
equipamentoService.cadastrarEquipamento(equipamento);
```

### Criação de Reserva
```java
Reserva reserva = new Reserva();
reserva.setEquipamentoId(1);
reserva.setUsuarioId(1);
reserva.setDataInicio(LocalDateTime.now());
reserva.setDataFim(LocalDateTime.now().plusDays(1));
reservaService.criarReserva(reserva);
```

### Autenticação de Usuário
```java
Usuario usuario = usuarioService.autenticarUsuario("usuario@email.com", "senha123");
if (usuario != null) {
    System.out.println("Usuário autenticado com sucesso!");
}
```

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes. 