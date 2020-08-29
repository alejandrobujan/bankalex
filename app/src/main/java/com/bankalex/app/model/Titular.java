package com.bankalex.app.model;

import java.util.Date;

public class Titular{
    private Persoa cliente;
    private Conta conta;
    private Date data_ini;
    private Date data_fin;

    public Titular(){

    }

    public Titular(Persoa cliente, Conta conta, Date data_ini, Date data_fin) {
        this.cliente = cliente;
        this.conta = conta;
        this.data_ini = data_ini;
        this.data_fin = data_fin;
    }

    public Persoa getCliente() {
        return this.cliente;
    }

    public void setCliente(Persoa cliente) {
        this.cliente = cliente;
    }

    public Conta getConta() {
        return this.conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
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
            " cliente='" + getCliente() + "'" +
            ", conta='" + getConta() + "'" +
            ", data_ini='" + getData_ini() + "'" +
            ", data_fin='" + getData_fin() + "'" +
            "}";
    }

}