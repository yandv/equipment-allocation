package br.ufrrj.common.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Equipamento implements Serializable {

    private final UUID
     id;
    private final String nome;

}
