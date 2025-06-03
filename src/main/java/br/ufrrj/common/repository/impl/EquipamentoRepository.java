package br.ufrrj.common.repository.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.ufrrj.common.database.connection.Database;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.repository.DatabaseRepository;

public class EquipamentoRepository extends DatabaseRepository<Equipamento> {

    public EquipamentoRepository(Database database) {
        super(database);
    }

    public Equipamento criarEquipamento(String nome) throws SQLException {
        return this.database.executeQuery("INSERT INTO equipamentos (nome) VALUES (?) RETURNING *",
                statement -> {
                    statement.setString(1, nome);
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Equipamento(resultSet.getInt("id"), resultSet.getString("nome"));
                    }
                    return null;
                });
    }

    public void atualizarEquipamento(Equipamento equipamento) throws SQLException {
        this.database.executeStatement("UPDATE equipamentos SET nome = ? WHERE id = ?",
                statement -> {
                    statement.setString(1, equipamento.getNome());
                    statement.setInt(2, equipamento.getId());
                });
    }

    public void deletarEquipamento(Equipamento equipamento) throws SQLException {
        this.database.executeStatement("DELETE FROM equipamentos WHERE id = ?",
                statement -> {
                    statement.setInt(1, equipamento.getId());
                });
    }

    public Equipamento consultarEquipamentoPeloId(Integer id) throws SQLException {
        return this.database.executeQuery("SELECT * FROM equipamentos WHERE id = ?",
                statement -> {
                    statement.setInt(1, id);
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Equipamento(resultSet.getInt("id"), resultSet.getString("nome"));
                    }
                    return null;
                });
    }

    public List<Equipamento> consultarTodosEquipamentos() throws SQLException {
        return this.database.executeQuery("SELECT * FROM equipamentos", resultSet -> {
            var equipamentos = new ArrayList<Equipamento>();
            while (resultSet.next()) {
                equipamentos.add(new Equipamento(resultSet.getInt("id"), resultSet.getString("nome")));
            }
            return equipamentos;
        });
    }

}
