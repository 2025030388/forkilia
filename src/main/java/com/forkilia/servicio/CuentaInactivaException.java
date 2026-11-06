package com.forkilia.servicio;

public class CuentaInactivaException extends Exception {
    public CuentaInactivaException(String mensaje) {
        super(mensaje);
    }
}