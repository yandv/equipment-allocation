package br.ufrrj.common.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import br.ufrrj.common.model.Usuario;

public interface UsuarioService extends Remote {

    Usuario criarUsuario(String nome) throws RemoteException;

    Usuario consultarUsuarioPeloId(Integer id) throws RemoteException;
    
    List<Usuario> consultarTodosUsuarios() throws RemoteException;
}
