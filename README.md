# Equipment Allocation

A distributed system for booking shared equipment, built with Java RMI and PostgreSQL for the Distributed Systems course at UFRRJ. A server exposes three remote services and an interactive command-line client calls them over the network.

## Architecture

- **Server** (`br.ufrrj.common.Server`): starts an RMI registry and binds `UsuarioService`, `EquipamentoService` and `ReservaService`.
- **Client** (`br.ufrrj.common.Client`): a numbered menu that looks the services up in the registry. When a call fails it reconnects, up to 3 attempts.
- **Layers**: models (`Usuario`, `Equipamento`, `Reserva`), repositories over JDBC, and services with the business rules.

## Remote services

| Service | Methods |
| --- | --- |
| `UsuarioService` | `criarUsuario(nome)`, `consultarUsuarioPeloId(id)`, `consultarTodosUsuarios()` |
| `EquipamentoService` | `criarEquipamento(nome)`, `atualizarEquipamento(equipamento)`, `deletarEquipamento(equipamento)`, `consultarEquipamentoPeloId(id)`, `consultarTodosEquipamentos()` |
| `ReservaService` | `alocarEquipamento(usuario, equipamento, inicio, fim)`, `devolverEquipamento(usuario, equipamento)`, `verificarDisponibilidade(equipamento, inicio, fim)`, `consultarAlocacoesUsuario(usuario)`, `consultarAlocacoesEquipamento(equipamento)` |

A booking is only created when the equipment has no overlapping booking in the requested period.

## Database

`schema.sql` creates three tables:

- `usuarios` and `equipamentos`, each with an id and a name;
- `reservas`, linking a user to a piece of equipment over a period, with status `ATIVO` or `FINALIZADO`, a check that the start comes before the end, and indexes by user, equipment and period.

## Requirements

- Java 17 or newer
- Maven
- PostgreSQL (a `docker-compose.yml` with PostgreSQL 16 is included)

## Running

1. Start PostgreSQL and apply the schema:

   ```bash
   docker compose up -d
   psql -h localhost -U postgres -d postgres -f schema.sql
   ```

2. Edit `src/main/resources/properties.yaml`. It is read from the classpath, so it goes into the jar at build time. `hostname` and `port` point to the RMI server; `database` holds the PostgreSQL connection:

   ```yaml
   hostname: localhost
   port: 1099

   database:
     host: localhost
     port: 5432
     username: postgres
     password: your_password
     database: postgres
   ```

3. Build, then start the server and the client in separate terminals:

   ```bash
   mvn clean package
   java -cp target/equipment-allocation.jar br.ufrrj.common.Server
   java -cp target/equipment-allocation.jar br.ufrrj.common.Client
   ```

## Client menu

- **Users**: create, find by id, list all.
- **Equipment**: create, update, delete, find by id, list all.
- **Bookings**: allocate, return, check availability, list bookings of a user or of a piece of equipment.
