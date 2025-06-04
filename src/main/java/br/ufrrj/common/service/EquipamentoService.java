package br.ufrrj.common.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import br.ufrrj.common.model.Equipamento;

public interface EquipamentoService extends Remote {
     
    Equipamento criarEquipamento(String nome) throws RemoteException;

    void atualizarEquipamento(Equipamento equipamento) throws RemoteException;

    void deletarEquipamento(Equipamento equipamento) throws RemoteException;

    Equipamento consultarEquipamentoPeloId(Integer id) throws RemoteException;

    List<Equipamento> consultarTodosEquipamentos() throws RemoteException;
    
}
