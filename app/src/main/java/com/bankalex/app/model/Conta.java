package com.bankalex.app.model;

import java.math.BigDecimal;

public class Conta{
    
    private String iban;
    private BigDecimal saldo;

    public Conta(){

    }

    public Conta(String iban, BigDecimal saldo) {
        this.iban = iban;
        this.saldo = saldo;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getSaldo() {
        return this.saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "{" +
            " iban='" + getIban() + "'" +
            ", saldo='" + getSaldo() + "'" +
            "}";
    }



}