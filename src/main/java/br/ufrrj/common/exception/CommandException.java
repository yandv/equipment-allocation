package br.ufrrj.common.exception;

import java.rmi.RemoteException;

public class CommandException extends RemoteException {

    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message);
    }
    
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
