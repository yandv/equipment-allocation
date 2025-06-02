package br.ufrrj.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import br.ufrrj.common.model.Usuario;

public interface UsuarioService extends Remote {

    Usuario criarUsuario(String nome) throws RemoteException;

    Usuario consultarUsuarioPeloId(UUID id) throws RemoteException;
    
    List<Usuario> consultarTodosUsuarios() throws RemoteException;
}
