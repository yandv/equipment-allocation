package br.ufrrj.common.repository.impl;

import br.ufrrj.common.database.connection.Database;
import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.repository.DatabaseRepository;
import br.ufrrj.common.rmi.UsuarioService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsuarioRepository extends DatabaseRepository<Usuario> implements UsuarioService {

    public UsuarioRepository(Database database) {
        super(database);
    }

    @Override
    public Usuario criarUsuario(String nome) throws RemoteException {
        UUID id = UUID.randomUUID();
        database.executeStatement("INSERT INTO usuarios (id, nome) VALUES (?, ?)", statement -> {
            statement.setObject(1, id);
            statement.setString(2, nome);
        });
        return new Usuario(id, nome);
    }

    @Override
    public Usuario consultarUsuarioPeloId(UUID id) throws RemoteException {
        return database.executeQuery("SELECT * FROM usuarios WHERE id = ?", statement -> {
            statement.setObject(1, id);
        }, resultSet -> {
            if (resultSet.next()) {
                return new Usuario(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("nome"));
            }
            return null;
        });
    }

    @Override
    public List<Usuario> consultarTodosUsuarios() throws RemoteException {
        return database.executeQuery("SELECT * FROM usuarios", resultSet -> {
            var usuariosList = new ArrayList<Usuario>();
            while (resultSet.next()) {
                usuariosList.add(new Usuario(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("nome")));
            }
            return usuariosList;
        });
    }
}