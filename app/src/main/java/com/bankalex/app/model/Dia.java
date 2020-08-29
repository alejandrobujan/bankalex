package com.bankalex.app.model;

public class Dia{
    private String nome;
    private String horaApertura;
    private String horaPeche;

    public Dia() {

    }

    public Dia(String nome, String horaApertura, String horaPeche) {
        this.nome = nome;
        this.horaApertura = horaApertura;
        this.horaPeche = horaPeche;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(String horaApertura) {
        this.horaApertura = horaApertura;
    }

    public String getHoraPeche() {
        return horaPeche;
    }

    public void setHoraPeche(String horaPeche) {
        this.horaPeche = horaPeche;
    }

    @Override
    public String toString() {
        return nome + ": " + horaApertura + " - " + horaPeche;
    }
}
