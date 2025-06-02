package br.ufrrj.common.repository.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.ufrrj.common.database.connection.Database;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.model.Reserva;
import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.repository.DatabaseRepository;

public class ReservaRepository extends DatabaseRepository<Reserva> {

    public ReservaRepository(Database database) {
        super(database);
    }

    public void criarReserva(Reserva reserva) throws SQLException {
        this.database.executeStatement(
                "INSERT INTO reservas (equipamento_id, usuario_id, data_inicio, data_fim, status) VALUES (?, ?, ?, ?, ?::status_reserva)",
                statement -> {
                    statement.setObject(1, reserva.getEquipamentoId());
                    statement.setObject(2, reserva.getUsuarioId());
                    statement.setTimestamp(3, new java.sql.Timestamp(reserva.getDataInicio().getTime()));
                    statement.setTimestamp(4, new java.sql.Timestamp(reserva.getDataFim().getTime()));
                    statement.setString(5, reserva.getStatus().name());
                });
    }

    public void atualizarStatusReserva(Reserva reserva) throws SQLException {
        this.database.executeStatement(
                "UPDATE reservas SET status = ?::status_reserva WHERE equipamento_id = ? AND usuario_id = ? AND data_inicio = ?",
                statement -> {
                    statement.setString(1, reserva.getStatus().name());
                    statement.setObject(2, reserva.getEquipamentoId());
                    statement.setObject(3, reserva.getUsuarioId());
                    statement.setTimestamp(4, new java.sql.Timestamp(reserva.getDataInicio().getTime()));
                });
    }

    public List<Reserva> consultarReservasPorUsuario(UUID usuarioId) throws SQLException {
        return this.database
                .executeQuery(
                        "SELECT reservas.equipamento_id as equipamento_id, reservas.usuario_id as usuario_id, reservas.data_inicio as data_inicio, reservas.data_fim as data_fim, reservas.status as status, equipamentos.id as equipamento_id, equipamentos.nome as equipamento_nome, usuarios.id as usuario_id, usuarios.nome as usuario_nome FROM reservas LEFT JOIN equipamentos ON reservas.equipamento_id = equipamentos.id LEFT JOIN usuarios ON reservas.usuario_id = usuarios.id WHERE reservas.usuario_id = ?",
                        statement -> {
                            statement.setObject(1, usuarioId);
                        },
                        resultSet -> {
                            var list = new ArrayList<Reserva>();
                            while (resultSet.next()) {
                                var reserva = new Reserva(
                                        UUID.fromString(resultSet.getString("equipamento_id")),
                                        UUID.fromString(resultSet.getString("usuario_id")),
                                        new Date(resultSet.getTimestamp("data_inicio").getTime()),
                                        new Date(resultSet.getTimestamp("data_fim").getTime()),
                                        Reserva.ReservaStatus.valueOf(resultSet.getString("status")),
                                        new Equipamento(
                                                UUID.fromString(resultSet.getString("equipamento_id")),
                                                resultSet.getString("equipamento_nome")),
                                        new Usuario(
                                                UUID.fromString(resultSet.getString("usuario_id")),
                                                resultSet.getString("usuario_nome")));
                                list.add(reserva);
                            }
                            return list;
                        });
    }

    public List<Reserva> consultarReservasPorEquipamento(UUID equipamentoId) throws SQLException {
        return this.database.executeQuery(
                "SELECT reservas.equipamento_id as equipamento_id, reservas.usuario_id as usuario_id, reservas.data_inicio as data_inicio, reservas.data_fim as data_fim, reservas.status as status, equipamentos.id as equipamento_id, equipamentos.nome as equipamento_nome, usuarios.id as usuario_id, usuarios.nome as usuario_nome FROM reservas LEFT JOIN usuarios ON reservas.usuario_id = usuarios.id LEFT JOIN equipamentos ON reservas.equipamento_id = equipamentos.id WHERE reservas.equipamento_id = ?",
                statement -> {
                    statement.setObject(1, equipamentoId);
                },
                resultSet -> {
                    var list = new ArrayList<Reserva>();
                    while (resultSet.next()) {
                        var reserva = new Reserva(
                                UUID.fromString(resultSet.getString("equipamento_id")),
                                UUID.fromString(resultSet.getString("usuario_id")),
                                new Date(resultSet.getTimestamp("data_inicio").getTime()),
                                new Date(resultSet.getTimestamp("data_fim").getTime()),
                                Reserva.ReservaStatus.valueOf(resultSet.getString("status")),
                                new Equipamento(
                                        UUID.fromString(resultSet.getString("equipamento_id")),
                                        resultSet.getString("equipamento_nome")),
                                new Usuario(
                                        UUID.fromString(resultSet.getString("usuario_id")),
                                        resultSet.getString("usuario_nome")));
                        list.add(reserva);
                    }
                    return list;
                });
    }

    public Reserva consultarReservaPorEquipamentoEData(UUID equipamentoId, Date dataInicio, Date dataFim)
            throws SQLException {
        return this.database.executeQuery(
                "SELECT reservas.equipamento_id as equipamento_id, reservas.usuario_id as usuario_id, reservas.data_inicio as data_inicio, reservas.data_fim as data_fim, reservas.status as status, equipamentos.id as equipamento_id, equipamentos.nome as equipamento_nome, usuarios.id as usuario_id, usuarios.nome as usuario_nome FROM reservas LEFT JOIN usuarios ON reservas.usuario_id = usuarios.id LEFT JOIN equipamentos ON reservas.equipamento_id = equipamentos.id WHERE reservas.equipamento_id = ? AND reservas.data_inicio = ? AND reservas.data_fim = ?",
                statement -> {
                    statement.setObject(1, equipamentoId);
                    statement.setTimestamp(2, new java.sql.Timestamp(dataInicio.getTime()));
                    statement.setTimestamp(3, new java.sql.Timestamp(dataFim.getTime()));
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Reserva(
                                UUID.fromString(resultSet.getString("equipamento_id")),
                                UUID.fromString(resultSet.getString("usuario_id")),
                                new Date(resultSet.getTimestamp("data_inicio").getTime()),
                                new Date(resultSet.getTimestamp("data_fim").getTime()),
                                Reserva.ReservaStatus.valueOf(resultSet.getString("status")),
                                new Equipamento(
                                        UUID.fromString(resultSet.getString("equipamento_id")),
                                        resultSet.getString("equipamento_nome")),
                                new Usuario(
                                        UUID.fromString(resultSet.getString("usuario_id")),
                                        resultSet.getString("usuario_nome")));
                    }
                    return null;
                });
    }

    public boolean verificarDisponibilidade(UUID equipamentoId, Date dataInicio, Date dataFim) throws SQLException {
        return this.database.executeQuery(
                "SELECT COUNT(*) as count FROM reservas WHERE equipamento_id = ? AND status = ?::status_reserva AND (data_inicio <= ? AND data_fim >= ?)",
                statement -> {
                    statement.setObject(1, equipamentoId);
                    statement.setString(2, Reserva.ReservaStatus.ATIVO.name());
                    statement.setTimestamp(3, new java.sql.Timestamp(dataInicio.getTime()));
                    statement.setTimestamp(4, new java.sql.Timestamp(dataFim.getTime()));
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") == 0;
                    }
                    return false;
                });
    }
}