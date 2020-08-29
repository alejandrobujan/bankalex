package com.bankalex.app.model;

import java.util.Date;

public class Traballa{
    private Persoa empregado;
    private Sucursal sucursal;
    private Posto posto;
    private Date data_ini;
    private Date data_fin;


    public Traballa() {
    }

    public Traballa(Persoa empregado, Sucursal sucursal, Posto posto, Date data_ini, Date data_fin) {
        this.empregado = empregado;
        this.sucursal = sucursal;
        this.posto = posto;
        this.data_ini = data_ini;
        this.data_fin = data_fin;
    }

    public Persoa getEmpregado() {
        return this.empregado;
    }

    public void setEmpregado(Persoa empregado) {
        this.empregado = empregado;
    }

    public Sucursal getSucursal() {
        return this.sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Posto getPosto() {
        return this.posto;
    }

    public void setPosto(Posto posto) {
        this.posto = posto;
    }

    public Date getData_ini() {
        return this.data_ini;
    }

    public void setData_ini(Date data_ini) {
        this.data_ini = data_ini;
    }

    public Date getData_fin() {
        return this.data_fin;
    }

    public void setData_fin(Date data_fin) {
        this.data_fin = data_fin;
    }

    @Override
    public String toString() {
        return "{" +
            " empregado='" + getEmpregado() + "'" +
            ", sucursal='" + getSucursal() + "'" +
            ", posto='" + getPosto() + "'" +
            ", data_ini='" + getData_ini() + "'" +
            ", data_fin='" + getData_fin() + "'" +
            "}";
    }

}