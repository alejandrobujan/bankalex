package com.bankalex.app.model;

import java.util.ArrayList;

public class Horario {
    private int id;
    private ArrayList<Dia> dias;

    public Horario() {

    }

    public Horario(int id, ArrayList<Dia> dias) {
        this.id = id;
        this.dias = dias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Dia> getDias() {
        return dias;
    }

    public void setDias(ArrayList<Dia> dias) {
        this.dias = dias;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", dias=" + dias +
                '}';
    }
}
