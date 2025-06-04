package br.ufrrj.common.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

import br.ufrrj.common.exception.CommandException;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.repository.impl.EquipamentoRepository;
import br.ufrrj.common.service.EquipamentoService;

public class EquipamentoServiceImpl extends UnicastRemoteObject implements EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoServiceImpl(EquipamentoRepository equipamentoRepository) throws RemoteException {
        super();
        this.equipamentoRepository = equipamentoRepository;
    }

    @Override
    public Equipamento criarEquipamento(String nome) throws RemoteException {
        try {
            return this.equipamentoRepository.criarEquipamento(nome);
        } catch (SQLException e) {
            throw new CommandException("Error creating equipment", e);
        }
    }

    @Override
    public void atualizarEquipamento(Equipamento equipamento) throws RemoteException {
        try {
            this.equipamentoRepository.atualizarEquipamento(equipamento);
        } catch (SQLException e) {
            throw new CommandException("Error updating equipment", e);
        }
    }

    @Override
    public void deletarEquipamento(Equipamento equipamento) throws RemoteException {
        try {
            this.equipamentoRepository.deletarEquipamento(equipamento);
        } catch (SQLException e) {
            throw new CommandException("Error deleting equipment", e);
        }
    }

    @Override
    public Equipamento consultarEquipamentoPeloId(Integer id) throws RemoteException {
        try {
            return this.equipamentoRepository.consultarEquipamentoPeloId(id);
        } catch (SQLException e) {
            throw new CommandException("Error consulting equipment by id", e);
        }
    }

    @Override
    public List<Equipamento> consultarTodosEquipamentos() throws RemoteException {
        try {
            return this.equipamentoRepository.consultarTodosEquipamentos();
        } catch (SQLException e) {
            throw new CommandException("Error consulting all equipments", e);
        }
    }

}
