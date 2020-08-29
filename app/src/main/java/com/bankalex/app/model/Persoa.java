package com.bankalex.app.model;

import java.util.Date;

public class Persoa{
    
    private int _id_persoa;
    private String dni;
    private String nome;
    private String telefono;
    private String enderezo;
    private Date data_contrato;
    private String clave;
    private boolean empregado;
    private boolean cliente;

    public Persoa(){

    }
    public Persoa(int _id_persoa, String dni, String nome, String telefono, String enderezo, Date data_contrato, String clave, boolean empregado, boolean cliente) {
        this._id_persoa = _id_persoa;
        this.dni = dni;
        this.nome = nome;
        this.telefono = telefono;
        this.enderezo = enderezo;
        this.data_contrato = data_contrato;
        this.clave = clave;
        this.empregado = empregado;
        this.cliente = cliente;
    }

    public int get_id_persoa() {
        return this._id_persoa;
    }

    public void set_id_persoa(int _id_persoa) {
        this._id_persoa = _id_persoa;
    }

    public String getDni() {
        return this.dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEnderezo() {
        return this.enderezo;
    }

    public void setEnderezo(String enderezo) {
        this.enderezo = enderezo;
    }

    public Date getData_contrato() {
        return this.data_contrato;
    }

    public void setData_contrato(Date data_contrato) {
        this.data_contrato = data_contrato;
    }

    public String getClave() {
        return this.clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isEmpregado() {
        return empregado;
    }

    public void setEmpregado(boolean empregado) {
        this.empregado = empregado;
    }

    public boolean isCliente() {
        return cliente;
    }

    public void setCliente(boolean cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "{" +
            " _id_persoa='" + get_id_persoa() + "'" +
            ", dni='" + getDni() + "'" +
            ", nome='" + getNome() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", enderezo='" + getEnderezo() + "'" +
            ", data_contrato='" + getData_contrato() + "'" +
            ", clave='" + getClave() + "'" +
            "}";
    }

    

}