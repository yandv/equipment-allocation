package br.ufrrj.common;

import br.ufrrj.common.config.Configuration;
import br.ufrrj.common.exception.CommandException;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.model.Reserva;
import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.service.EquipamentoService;
import br.ufrrj.common.service.ReservaService;
import br.ufrrj.common.service.UsuarioService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {

    public enum Menu {
        MAIN,
        USERS,
        EQUIPMENTS,
        RESERVATIONS;
    }

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static Scanner scanner;

    private static UsuarioService usuarioService;
    private static EquipamentoService equipamentoService;
    private static ReservaService reservaService;

    private static Menu currentMenu = Menu.MAIN;
    private static int attempts = 0;
    private static boolean isRunning = true;

    public static void main(String[] args) {
        try {
            Configuration configuration = new Configuration("properties.yaml");

            System.out.println("Conectando ao servidor " + configuration.getString("hostname") + ":"
                    + configuration.getInt("port"));

            Registry registry = LocateRegistry.getRegistry(configuration.getString("hostname"),
                    configuration.getInt("port"));

            usuarioService = (UsuarioService) registry.lookup("UsuarioService");
            equipamentoService = (EquipamentoService) registry.lookup("EquipamentoService");
            reservaService = (ReservaService) registry.lookup("ReservaService");

            scanner = new Scanner(System.in);

            clearConsole();

            handleMainLoop(args);
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleMainLoop(String[] args) {
        try {
            while (isRunning) {
                switch (currentMenu) {
                    case MAIN:
                        showMainMenu();
                        break;
                    case USERS:
                        showUserMenu();
                        break;
                    case EQUIPMENTS:
                        showEquipmentMenu();
                        break;
                    case RESERVATIONS:
                        showReservationMenu();
                        break;
                }
            }
        } catch (RemoteException e) {
            if (e.getCause() instanceof CommandException) {
                System.err.println(e.getCause().getMessage());
                handleMainLoop(args);
                return;
            }

            if (e.getMessage().contains("Connection refused")) {
                System.out
                        .println("Falha ao se comunicar com o servidor, tentaremos mais " + (3 - attempts)
                                + " vezes.");
                attempts++;
                if (attempts < 3) {
                    main(args);
                } else {
                    System.out.println("Falha ao se comunicar com o servidor, tente novamente mais tarde.");
                }
            } else {
                System.err.println("Erro de comunicação: " + e.getMessage());
            }
        }
    }

    private static void showMainMenu() throws RemoteException {
        System.out.println("\n=== MENU PRINCIPAL ===\n");
        System.out.println("1. Gerenciar Usuários");
        System.out.println("2. Gerenciar Equipamentos");
        System.out.println("3. Gerenciar Reservas");
        System.out.println("\n0. Sair");
        System.out.print("\nEscolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice >= 1 && choice <= 3) {
            clearConsole();
        }

        switch (choice) {
            case 1:
                currentMenu = Menu.USERS;
                showUserMenu();
                break;
            case 2:
                currentMenu = Menu.EQUIPMENTS;
                showEquipmentMenu();
                break;
            case 3:
                currentMenu = Menu.RESERVATIONS;
                showReservationMenu();
                break;
            case 0:
                System.out.println("\nBye bye...");
                isRunning = false;
                return;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void showUserMenu() throws RemoteException {
        System.out.println("\n=== MENU DE USUÁRIOS ===\n");
        System.out.println("1. Criar usuário");
        System.out.println("2. Consultar usuário por ID");
        System.out.println("3. Listar todos os usuários");
        System.out.println("\n0. Voltar ao menu principal");
        System.out.print("\nEscolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();
                System.out.println("Opção escolhida: Criar usuário");
                System.out.println("--------------------------------");
                String nome = parseString("Nome do usuário: ");
                var usuario = usuarioService.criarUsuario(nome);
                System.out
                        .println("Usuário " + usuario.getNome() + " (" + usuario.getId() + ") criado com sucesso!");
                break;
            case 2:
                clearConsole();
                System.out.println("Opção escolhida: Consultar usuário por ID");
                System.out.println("--------------------------------");
                Integer id = parseInteger("ID do usuário: ");
                Usuario usuarioEncontrado = usuarioService.consultarUsuarioPeloId(id);
                if (usuarioEncontrado != null) {
                    System.out.println("Usuário encontrado:");
                    System.out.println("ID: " + usuarioEncontrado.getId());
                    System.out.println("Nome: " + usuarioEncontrado.getNome());
                } else {
                    System.out.println("Usuário não encontrado.");
                }
                break;
            case 3:
                clearConsole();
                System.out.println("Opção escolhida: Listar todos os usuários");
                System.out.println("--------------------------------");
                List<Usuario> usuarios = usuarioService.consultarTodosUsuarios();
                System.out.println("\nLista de usuários:");
                for (Usuario u : usuarios) {
                    System.out.println("Usuário " + u.getNome() + " (" + u.getId() + ")");
                }
                break;
            case 0:
                currentMenu = Menu.MAIN;
                return;
            default:
                System.out.println("Opção inválida!");
        }

        waitToContinue();
    }

    private static void showEquipmentMenu() throws RemoteException {
        System.out.println("\n=== MENU DE EQUIPAMENTOS ===\n");
        System.out.println("1. Criar equipamento");
        System.out.println("2. Atualizar equipamento");
        System.out.println("3. Excluir equipamento");
        System.out.println("4. Consultar equipamento por ID");
        System.out.println("5. Listar todos os equipamentos");
        System.out.println("\n0. Voltar ao menu principal");
        System.out.print("\nEscolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        clearConsole();

        switch (choice) {
            case 1:
                clearConsole();
                System.out.println("Opção escolhida: Criar equipamento");
                System.out.println("--------------------------------");
                String nome = parseString("Nome do equipamento: ");
                var equipamento = equipamentoService.criarEquipamento(nome);
                System.out.println("Equipamento " + equipamento.getNome() + " (" + equipamento.getId()
                        + ") criado com sucesso!");

                break;
            case 2:
                clearConsole();
                System.out.println("Opção escolhida: Atualizar equipamento");
                System.out.println("--------------------------------");

                Integer idEquipamento = parseInteger("ID do equipamento: ");
                Equipamento equipamentoAtualizar = equipamentoService
                        .consultarEquipamentoPeloId(idEquipamento);

                if (equipamentoAtualizar != null) {
                    System.out.print("Novo nome: ");
                    String novoNome = scanner.nextLine();
                    equipamentoAtualizar = new Equipamento(idEquipamento, novoNome);
                    equipamentoService.atualizarEquipamento(equipamentoAtualizar);
                    System.out.println("Equipamento atualizado com sucesso!");
                } else {
                    System.out.println("Equipamento não encontrado.");
                }

                break;
            case 3:
                clearConsole();
                System.out.println("Opção escolhida: Excluir equipamento");
                System.out.println("--------------------------------");
                idEquipamento = parseInteger("ID do equipamento: ");
                Equipamento equipamentoExcluir = equipamentoService
                        .consultarEquipamentoPeloId(idEquipamento);

                if (equipamentoExcluir != null) {
                    equipamentoService.deletarEquipamento(equipamentoExcluir);
                    System.out.println("Equipamento excluído com sucesso!");
                } else {
                    System.out.println("Equipamento não encontrado.");
                }

                break;
            case 4:
                clearConsole();
                System.out.println("Opção escolhida: Consultar equipamento por ID");
                System.out.println("--------------------------------");
                idEquipamento = parseInteger("ID do equipamento: ");
                Equipamento equipamentoEncontrado = equipamentoService
                        .consultarEquipamentoPeloId(idEquipamento);

                if (equipamentoEncontrado != null) {
                    System.out.println("Equipamento " + equipamentoEncontrado.getNome() + " ("
                            + equipamentoEncontrado.getId() + ")");
                } else {
                    System.out.println("Equipamento não encontrado.");
                }

                break;
            case 5:
                clearConsole();
                System.out.println("Opção escolhida: Listar todos os equipamentos");
                System.out.println("--------------------------------");
                List<Equipamento> equipamentos = equipamentoService.consultarTodosEquipamentos();
                System.out.println("\nLista de equipamentos:");
                for (Equipamento e : equipamentos) {
                    System.out.println("Equipamento " + e.getNome() + " (" + e.getId() + ")");
                }

                break;
            case 0:
                currentMenu = Menu.MAIN;
                return;
            default:
                System.out.println("Opção inválida!");
        }

        waitToContinue();
    }

    private static void showReservationMenu() throws RemoteException {
        System.out.println("\n=== MENU DE RESERVAS ===\n");
        System.out.println("1. Alocar equipamento");
        System.out.println("2. Devolver equipamento");
        System.out.println("3. Verificar disponibilidade");
        System.out.println("4. Consultar alocações de um usuário");
        System.out.println("5. Consultar alocações de um equipamento");
        System.out.println("\n0. Voltar ao menu principal");
        System.out.print("\nEscolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        clearConsole();

        switch (choice) {
            case 1:
                clearConsole();
                System.out.println("Opção escolhida: Alocar equipamento");
                System.out.println("--------------------------------");
                Integer usuarioId = parseInteger("ID do usuário: ");
                Integer equipamentoId = parseInteger("ID do equipamento: ");

                Usuario usuario = usuarioService.consultarUsuarioPeloId(usuarioId);
                Equipamento equipamento = equipamentoService
                        .consultarEquipamentoPeloId(equipamentoId);

                if (usuario == null || equipamento == null) {
                    System.out.println("Usuário ou equipamento não encontrado.");
                    break;
                }

                Date dataInicio = parseDate("Data de início (dd/MM/yyyy HH:mm): ");
                Date dataFim = parseDate("Data de fim (dd/MM/yyyy HH:mm): ");

                reservaService.alocarEquipamento(usuario, equipamento, dataInicio, dataFim);
                System.out.println("Equipamento alocado com sucesso!");
                break;
            case 2:
                clearConsole();
                System.out.println("Opção escolhida: Devolver equipamento");
                System.out.println("--------------------------------");
                usuarioId = parseInteger("ID do usuário: ");
                equipamentoId = parseInteger("ID do equipamento: ");

                usuario = usuarioService.consultarUsuarioPeloId(usuarioId);
                equipamento = equipamentoService.consultarEquipamentoPeloId(equipamentoId);

                if (usuario == null || equipamento == null) {
                    System.out.println("Usuário ou equipamento não encontrado.");
                    break;
                }

                reservaService.devolverEquipamento(usuario, equipamento);
                System.out.println("Equipamento devolvido com sucesso!");
                break;
            case 3:
                clearConsole();
                System.out.println("Opção escolhida: Verificar disponibilidade");
                System.out.println("--------------------------------");
                equipamentoId = parseInteger("ID do equipamento: ");
                equipamento = equipamentoService.consultarEquipamentoPeloId(equipamentoId);

                if (equipamento == null) {
                    System.out.println("Equipamento não encontrado.");
                    break;
                }

                dataInicio = parseDate("Data de início (dd/MM/yyyy HH:mm): ");
                dataFim = parseDate("Data de fim (dd/MM/yyyy HH:mm): ");

                var reserva = reservaService.verificarDisponibilidade(equipamento, dataInicio, dataFim);

                if (reserva != null) {
                    System.out.println(
                            "Equipamento não disponível para o período especificado, pois foi alocado para o usuário "
                                    + reserva.getUsuario().getNome() + " de "
                                    + DATE_TIME_FORMAT.format(reserva.getDataInicio()) + " a "
                                    + DATE_TIME_FORMAT.format(reserva.getDataFim()));
                } else {
                    System.out.println("Equipamento disponível para o período especificado!");
                }
                break;
            case 4:
                clearConsole();
                System.out.println("Opção escolhida: Consultar alocações de um usuário");
                System.out.println("--------------------------------");
                usuarioId = parseInteger("ID do usuário: ");
                usuario = usuarioService.consultarUsuarioPeloId(usuarioId);

                if (usuario == null) {
                    System.out.println("Usuário não encontrado.");
                    break;
                }

                List<Reserva> reservasUsuario = reservaService.consultarAlocacoesUsuario(usuario);
                System.out.println("\nAlocações do usuário:");

                if (reservasUsuario.isEmpty()) {
                    System.out.println("Não há alocações para este usuário.");
                } else {
                    for (Reserva r : reservasUsuario) {
                        System.out.println("Equipamento " + r.getEquipamento().getNome()
                                + " alocado para o usuário " + r.getUsuario().getNome() + " de "
                                + DATE_TIME_FORMAT.format(r.getDataInicio()) + " a "
                                + DATE_TIME_FORMAT.format(r.getDataFim()) + " com status " + r.getStatus());
                    }
                }
                break;
            case 5:
                clearConsole();
                System.out.println("Opção escolhida: Consultar alocações de um equipamento");
                System.out.println("--------------------------------");
                equipamentoId = parseInteger("ID do equipamento: ");
                equipamento = equipamentoService.consultarEquipamentoPeloId(equipamentoId);

                if (equipamento == null) {
                    System.out.println("Equipamento não encontrado.");
                    break;
                }

                List<Reserva> reservasEquipamento = reservaService.consultarAlocacoesEquipamento(equipamento);
                System.out.println("\nAlocações do equipamento:");

                if (reservasEquipamento.isEmpty()) {
                    System.out.println("Nenhuma alocação encontrada para este equipamento.");
                } else {
                    for (Reserva r : reservasEquipamento) {
                        System.out.println("Equipamento " + r.getEquipamento().getNome()
                                + " alocado para o usuário " + r.getUsuario().getNome() + " de "
                                + DATE_TIME_FORMAT.format(r.getDataInicio()) + " a "
                                + DATE_TIME_FORMAT.format(r.getDataFim()) + " com status " + r.getStatus());
                    }
                }
                break;
            case 0:
                currentMenu = Menu.MAIN;
                return;
            default:
                System.out.println("Opção inválida!");
        }

        waitToContinue();
    }

    private static void waitToContinue() {
        System.out.println("\nPressione Enter para continuar...");
        scanner.nextLine();
        clearConsole();
    }

    private static void clearConsole() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    private static Date parseDate(String message) {
        System.out.println(message);
        try {
            return DATE_TIME_FORMAT.parse(scanner.nextLine());
        } catch (ParseException e) {
            return parseDate("Data inválida! Formato esperado: dd/MM/yyyy HH:mm");
        }
    }

    private static String parseString(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static Integer parseInteger(String message) {
        System.out.println(message);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return parseInteger("ID inválido! Formato númerico esperado.");
        }
    }
}
