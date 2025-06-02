package br.ufrrj.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import java.util.Date;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.model.Reserva;
import br.ufrrj.common.model.Usuario;

public interface ReservaService extends Remote {

    void alocarEquipamento(Usuario usuario, Equipamento equipamento, Date dataInicio, Date dataFim)
            throws RemoteException;

    void devolverEquipamento(Usuario usuario, Equipamento equipamento) throws RemoteException;

    Reserva verificarDisponibilidade(Equipamento equipamento, Date dataInicio, Date dataFim) throws RemoteException;

    List<Reserva> consultarAlocacoesUsuario(Usuario usuario) throws RemoteException;

    List<Reserva> consultarAlocacoesEquipamento(Equipamento equipamento) throws RemoteException;

}
