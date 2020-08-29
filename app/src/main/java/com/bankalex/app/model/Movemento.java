package com.bankalex.app.model;

import java.math.BigDecimal;
import java.util.Date;

public class Movemento{
    
    private Conta conta;
    private int _id_movemento;
    private BigDecimal importe;
    private String concepto;
    private Date data;

    public Movemento(Conta conta, int _id_movemento, BigDecimal importe, String concepto, Date data) {
        this.conta = conta;
        this._id_movemento = _id_movemento;
        this.importe = importe;
        this.concepto = concepto;
        this.data = data;
    }

    public Conta getConta() {
        return this.conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public int get_id_movemento() {
        return this._id_movemento;
    }

    public void set_id_movemento(int _id_movemento) {
        this._id_movemento = _id_movemento;
    }

    public BigDecimal getImporte() {
        return this.importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getConcepto() {
        return this.concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Date getData() {
        return this.data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
            " conta='" + getConta() + "'" +
            ", _id_movemento='" + get_id_movemento() + "'" +
            ", importe='" + getImporte() + "'" +
            ", concepto='" + getConcepto() + "'" +
            ", data='" + getData() + "'" +
            "}";
    }


}