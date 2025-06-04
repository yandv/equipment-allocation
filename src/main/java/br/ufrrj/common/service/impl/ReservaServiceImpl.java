package br.ufrrj.common.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.ufrrj.common.exception.CommandException;
import br.ufrrj.common.model.Equipamento;
import br.ufrrj.common.model.Reserva;
import br.ufrrj.common.model.Usuario;
import br.ufrrj.common.repository.impl.EquipamentoRepository;
import br.ufrrj.common.repository.impl.ReservaRepository;
import br.ufrrj.common.repository.impl.UsuarioRepository;
import br.ufrrj.common.service.ReservaService;

public class ReservaServiceImpl extends UnicastRemoteObject implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, EquipamentoRepository equipamentoRepository) throws RemoteException {
        super();
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    @Override
    public void alocarEquipamento(Usuario usuario, Equipamento equipamento, Date dataInicio, Date dataFim) throws RemoteException {
        try {
            if (usuarioRepository.consultarUsuarioPeloId(usuario.getId()) == null) {
                throw new CommandException("Usuário não encontrado");
            }

            if (equipamentoRepository.consultarEquipamentoPeloId(equipamento.getId()) == null) {
                throw new CommandException("Equipamento não encontrado");
            }

            if (!reservaRepository.verificarDisponibilidade(equipamento.getId(), dataInicio, dataFim)) {
                throw new CommandException("O equipamento não está disponível para o período especificado");
            }

            if (dataInicio.after(dataFim)) {
                throw new CommandException("A data de início não pode ser posterior a data de fim");
            }

            var reserva = new Reserva(equipamento.getId(), usuario.getId(), dataInicio, dataFim);
            reserva.setStatus(Reserva.ReservaStatus.ATIVO);
            reservaRepository.criarReserva(reserva);
        } catch (SQLException e) {
            throw new CommandException("Error allocating equipment", e);
        }
    }

    @Override
    public void devolverEquipamento(Usuario usuario, Equipamento equipamento) throws RemoteException {
        try {
            var reservas = reservaRepository.consultarReservasPorEquipamento(equipamento.getId());
            var reservaAtiva = reservas.stream()
                .filter(r -> r.getUsuarioId().equals(usuario.getId()) && r.getStatus() == Reserva.ReservaStatus.ATIVO)
                .findFirst()
                .orElseThrow(() -> new CommandException("Não há reserva ativa para este usuário e equipamento"));

            reservaAtiva.setStatus(Reserva.ReservaStatus.FINALIZADO);
            reservaRepository.atualizarStatusReserva(reservaAtiva);
        } catch (SQLException e) {
            throw new CommandException("Error returning equipment", e);
        }
    }

    @Override
    public Reserva verificarDisponibilidade(Equipamento equipamento, Date dataInicio, Date dataFim) throws RemoteException {
        try {
            var reserva = reservaRepository.consultarReservaPorEquipamentoEData(equipamento.getId(), dataInicio, dataFim);
            if (reserva != null) {
                return reserva;
            }
            return null;
        } catch (SQLException e) {
            throw new CommandException("Error checking equipment availability", e);
        }
    }

    @Override
    public List<Reserva> consultarAlocacoesUsuario(Usuario usuario) throws RemoteException {
        try {
            return reservaRepository.consultarReservasPorUsuario(usuario.getId());
        } catch (SQLException e) {
            throw new CommandException("Error consulting user allocations", e);
        }
    }

    @Override
    public List<Reserva> consultarAlocacoesEquipamento(Equipamento equipamento) throws RemoteException {
        try {
            return reservaRepository.consultarReservasPorEquipamento(equipamento.getId());
        } catch (SQLException e) {
            throw new CommandException("Error consulting equipment allocations", e);
        }
    }
} 