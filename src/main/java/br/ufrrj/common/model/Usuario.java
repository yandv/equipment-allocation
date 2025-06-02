package br.ufrrj.common.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Usuario implements Serializable {

    private final UUID id;
    private String nome;
    
}
