package com.forkilia.modelo;

public class Clienteempresa extends Cliente
{
    private String empresa;
    private int numpersonas;
    private double porcentajeDescuento = 0.10;
    
    public Clienteempresa(String idCliente, String idUsuario, 
            String nombre, String telefono, String direccion,
            String empresa, int numpersonas, double porcentajeDescuento)
    {
        super(idCliente, idUsuario, nombre, telefono, direccion);
        this.empresa = empresa;
        this.numpersonas = numpersonas;
        this.porcentajeDescuento=porcentajeDescuento;
    }

    public String getempresa()
    {
        return empresa;
    }

    public void setempresa(String empresa)
    {
        this.empresa = empresa;
    }

    public int getnumpersonas()
    {
        return numpersonas;
    }

    public void setnumpersonas(int numpersonas)
    {
        this.numpersonas = numpersonas;
    }
    
     public double getporcentajeDescuento()
    {
        return porcentajeDescuento;
    }

    public double setporcentajeDescuento(double porcentajeDescuento)
    {
        this.porcentajeDescuento = porcentajeDescuento;
        return 0;
    }
    
    public double calculaPrecioConDescuento (double preciOriginal)
    {
        double descuento = preciOriginal * porcentajeDescuento;
        return preciOriginal - descuento;
    }
    
    
}