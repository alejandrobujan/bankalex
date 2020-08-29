package com.bankalex.app.model;

import java.util.Date;

public class Encarga{
    
    private Persoa emp_sup; 
    private Persoa emp_inf;
    private Date data_ini;
    private Date data_fin;

    public Encarga(){

    }

    public Encarga(Persoa emp_sup, Persoa emp_inf, Date data_ini, Date data_fin) {
        this.emp_sup = emp_sup;
        this.emp_inf = emp_inf;
        this.data_ini = data_ini;
        this.setData_fin(data_fin);
    }

    public Persoa getEmp_sup() {
        return this.emp_sup;
    }

    public void setEmp_sup(Persoa emp_sup) {
        this.emp_sup = emp_sup;
    }

    public Persoa getEmp_inf() {
        return this.emp_inf;
    }

    public void setEmp_inf(Persoa emp_inf) {
        this.emp_inf = emp_inf;
    }

    public Date getData_ini() {
        return this.data_ini;
    }

    public void setData_ini(Date data_ini) {
        this.data_ini = data_ini;
    }

    public Date getData_fin() {
        return data_fin;
    }

    public void setData_fin(Date data_fin) {
        this.data_fin = data_fin;
    }

    @Override
    public String toString() {
        return "{" +
            " emp_sup='" + getEmp_sup() + "'" +
            ", emp_inf='" + getEmp_inf() + "'" +
            ", data_ini='" + getData_ini() + "'" +
            "}";
    }
}