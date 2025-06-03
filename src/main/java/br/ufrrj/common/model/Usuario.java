package br.ufrrj.common.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Usuario implements Serializable {

    private final Integer id;
    private String nome;
    
}
