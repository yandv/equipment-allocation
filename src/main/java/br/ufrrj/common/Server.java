package br.ufrrj.common;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import br.ufrrj.common.config.Configuration;
import br.ufrrj.common.database.connection.impl.PostgresDatabase;
import br.ufrrj.common.repository.impl.EquipamentoRepository;
import br.ufrrj.common.repository.impl.ReservaRepository;
import br.ufrrj.common.repository.impl.UsuarioRepository;
import br.ufrrj.common.service.EquipamentoService;
import br.ufrrj.common.service.ReservaService;
import br.ufrrj.common.service.UsuarioService;
import br.ufrrj.common.service.impl.EquipamentoServiceImpl;
import br.ufrrj.common.service.impl.ReservaServiceImpl;
import br.ufrrj.common.service.impl.UsuarioServiceImpl;

public class Server {
    public static void main(String[] args) {
        try {
            Configuration configuration = new Configuration("properties.yaml");

            PostgresDatabase database = new PostgresDatabase(
                    configuration.getString("database.host"),
                    configuration.getInt("database.port"),
                    configuration.getString("database.username"),
                    configuration.getString("database.password"),
                    configuration.getString("database.database"),
                    false);

            database.startConnection();

            if (!database.isConnected()) {
                throw new RuntimeException("Error connecting to database");
            }

            EquipamentoRepository equipamentoRepository = new EquipamentoRepository(database);
            ReservaRepository reservaRepository = new ReservaRepository(database);
            UsuarioRepository usuarioRepository = new UsuarioRepository(database);

            EquipamentoService equipamentoService = new EquipamentoServiceImpl(equipamentoRepository);
            ReservaService reservaService = new ReservaServiceImpl(reservaRepository, usuarioRepository,
                    equipamentoRepository);
            UsuarioService usuarioService = new UsuarioServiceImpl(usuarioRepository);

            Registry registry = LocateRegistry.getRegistry(configuration.getString("hostname"),
                    configuration.getInt("port"));

            registry.rebind("EquipamentoService", equipamentoService);
            registry.rebind("ReservaService", reservaService);
            registry.rebind("UsuarioService", usuarioService);

            System.out.println("Serviço RMI iniciado na porta 1099");
        } catch (Exception e) {
            System.out.println("Error initializing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}