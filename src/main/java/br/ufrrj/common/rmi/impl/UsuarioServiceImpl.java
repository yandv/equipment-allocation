package br.ufrrj.common.rmi.impl;

import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.repository.impl.UsuarioRepository;
import br.ufrrj.common.rmi.UsuarioService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UsuarioServiceImpl extends UnicastRemoteObject implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) throws RemoteException {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario criarUsuario(String nome) throws RemoteException {
        return usuarioRepository.criarUsuario(nome);
    }

    @Override
    public Usuario consultarUsuarioPeloId(Integer id) throws RemoteException {
        return usuarioRepository.consultarUsuarioPeloId(id);
    }

    @Override
    public List<Usuario> consultarTodosUsuarios() throws RemoteException {
        return usuarioRepository.consultarTodosUsuarios();
    }
}   