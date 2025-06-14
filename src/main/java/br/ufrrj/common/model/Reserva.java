package br.ufrrj.common.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Reserva implements Serializable {

    private final Integer equipamentoId;
    private final Integer usuarioId;
    private final Date dataInicio;
    private final Date dataFim;

    @Setter
    private ReservaStatus status;

    private Equipamento equipamento;
    private Usuario usuario;

    public enum ReservaStatus {
        ATIVO, FINALIZADO;
    }
    
}
