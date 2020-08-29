package com.bankalex.app.model;

public class Sucursal{

    private int _id_sucursal;
    private String ubicacion;

    public Sucursal(){

    }

    public Sucursal(int _id_sucursal, String ubicacion) {
        this._id_sucursal = _id_sucursal;
        this.ubicacion = ubicacion;
    }

    public int get_id_sucursal() {
        return this._id_sucursal;
    }

    public void set_id_sucursal(int _id_sucursal) {
        this._id_sucursal = _id_sucursal;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "{" +
            " _id_sucursal='" + get_id_sucursal() + "'" +
            ", ubicacion='" + getUbicacion() + "'" +
            "}";
    }


}