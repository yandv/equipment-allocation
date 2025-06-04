package br.ufrrj.common.repository.impl;

import br.ufrrj.common.database.connection.Database;
import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.repository.DatabaseRepository;
import br.ufrrj.common.service.UsuarioService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository extends DatabaseRepository<Usuario> implements UsuarioService {

    public UsuarioRepository(Database database) {
        super(database);
    }

    @Override
    public Usuario criarUsuario(String nome) throws RemoteException {
        return database.executeQuery("INSERT INTO usuarios (nome) VALUES (?) RETURNING *", statement -> {
            statement.setString(1, nome);
        }, resultSet -> {
            if (resultSet.next()) {
                return new Usuario(resultSet.getInt("id"), resultSet.getString("nome"));
            }
            return null;
        });
    }

    @Override
    public Usuario consultarUsuarioPeloId(Integer id) throws RemoteException {
        return database.executeQuery("SELECT * FROM usuarios WHERE id = ?", statement -> {
            statement.setObject(1, id);
        }, resultSet -> {
            if (resultSet.next()) {
                return new Usuario(
                        resultSet.getInt("id"),
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
                        resultSet.getInt("id"),
                        resultSet.getString("nome")));
            }
            return usuariosList;
        });
    }
}