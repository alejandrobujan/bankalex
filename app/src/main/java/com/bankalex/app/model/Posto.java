package com.bankalex.app.model;


public class Posto{
    
    private int _id_posto;
    private String descricion;

    public Posto(){

    }

    public Posto(int _id_posto, String descricion) {
        this._id_posto = _id_posto;
        this.descricion = descricion;
    }

    public int get_id_posto() {
        return this._id_posto;
    }

    public void set_id_posto(int _id_posto) {
        this._id_posto = _id_posto;
    }

    public String getDescricion() {
        return this.descricion;
    }

    public void setDescricion(String descricion) {
        this.descricion = descricion;
    }

    @Override
    public String toString() {
        return "{" +
            " _id_posto='" + get_id_posto() + "'" +
            ", descricion='" + getDescricion() + "'" +
            "}";
    }

}