package com.bankalex.app.connectivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bankalex.app.model.Conta;
import com.bankalex.app.model.Encarga;
import com.bankalex.app.model.Movemento;
import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Posto;
import com.bankalex.app.model.Sucursal;
import com.bankalex.app.model.Titular;
import com.bankalex.app.model.Traballa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

public class BaseDatos extends SQLiteOpenHelper {

    public final static String NOME_BD = "bankalex";
    public final static int VERSION_BD = 1;
    public SQLiteDatabase sqlLiteDB;


    public BaseDatos(Context context) {
        super(context, NOME_BD, null, VERSION_BD);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }


    public Persoa consultaPersoa(int id_persoa) {
        Persoa persoa = new Persoa();
        Cursor cursor = sqlLiteDB.rawQuery("select * from PERSOA where _id_persoa=?", new String[]{String.valueOf(id_persoa)});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                persoa.set_id_persoa(cursor.getInt(0));
                persoa.setDni(cursor.getString(1));
                persoa.setNome(cursor.getString(2));
                persoa.setTelefono(cursor.getString(3));
                persoa.setEnderezo(cursor.getString(4));
                if(cursor.getString(5)!=null){
                    persoa.setData_contrato(Operacions.getDate(cursor.getString(5)));
                }
                persoa.setClave(cursor.getString(6));
                persoa.setEmpregado(cursor.getInt(7) == 1);
                persoa.setCliente(cursor.getInt(8) == 1);
                cursor.moveToNext();
            }
        }
        return persoa;
    }

    public Persoa consultaPersoa(String dni) {
        Persoa persoa = new Persoa();
        Cursor cursor = sqlLiteDB.rawQuery("select * from PERSOA where dni=?", new String[]{dni});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                persoa.set_id_persoa(cursor.getInt(0));
                persoa.setDni(cursor.getString(1));
                persoa.setNome(cursor.getString(2));
                persoa.setTelefono(cursor.getString(3));
                persoa.setEnderezo(cursor.getString(4));
                if(cursor.getString(5)!=null){
                    persoa.setData_contrato(Operacions.getDate(cursor.getString(5)));
                }
                persoa.setClave(cursor.getString(6));
                persoa.setEmpregado(cursor.getInt(7) == 1);
                persoa.setCliente(cursor.getInt(8) == 1);
                cursor.moveToNext();
            }
        }
        return persoa;
    }

    public Conta consultaConta(String iban) {
        Conta conta = new Conta();
        Cursor cursor = sqlLiteDB.rawQuery("select * from CONTA where iban=?", new String[]{iban});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                conta.setIban(cursor.getString(0));
                conta.setSaldo(getSaldo(listadoMovemento(iban)));
                cursor.moveToNext();
            }
        }
        return conta;
    }

    public Titular consultaTitularidadActual(String dni, String iban){
        Titular titular = new Titular();
        Persoa p = consultaPersoa(dni);
        Cursor cursor = sqlLiteDB.rawQuery("select * from TITULAR where _id_cliente=? and iban=? and data_fin IS NULL", new String[]{String.valueOf(p.get_id_persoa()),iban});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                titular.setCliente(p);
                titular.setConta(consultaConta(cursor.getString(1)));
                titular.setData_ini(Operacions.getDate(cursor.getString(2)));
                cursor.moveToNext();
            }
        }

        return titular;

    }

    public ArrayList<Titular> listadoTitular(String dni, boolean historico){
        ArrayList<Titular> titulares = new ArrayList<>();
        Cursor cursor = null;
        Persoa p = consultaPersoa(dni);
        String id = String.valueOf(p.get_id_persoa());
        if(historico){
            cursor = sqlLiteDB.rawQuery("select * from TITULAR where _id_cliente=? order by data_ini desc", new String[]{id});
        }else{
            cursor = sqlLiteDB.rawQuery("select * from TITULAR where _id_cliente=? and data_fin IS NULL", new String[]{id});
        }
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                titulares.add(new Titular(consultaPersoa(dni),consultaConta(cursor.getString(1)),Operacions.getDate(cursor.getString(2)),(cursor.getString(3)!=null ? Operacions.getDate(cursor.getString(3)) : null)));
                cursor.moveToNext();
            }
        }
        return titulares;
    }

    public ArrayList<Movemento> listadoMovemento(String iban){
        ArrayList<Movemento> movementos = new ArrayList<>();
        Conta conta = new Conta();
        Cursor cursorConta = sqlLiteDB.rawQuery("select * from CONTA where iban=?", new String[]{iban});
        if (cursorConta.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursorConta.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                conta.setIban(cursorConta.getString(0));
                conta.setSaldo(new BigDecimal(0));
                cursorConta.moveToNext();
            }
        }
        Cursor cursor = sqlLiteDB.rawQuery("select * from MOVEMENTO where iban=? ORDER BY _id_movemento desc", new String[]{iban});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                movementos.add(new Movemento(conta,cursor.getInt(1),new BigDecimal(String.valueOf(cursor.getDouble(2))).setScale(2, RoundingMode.HALF_EVEN),cursor.getString(3),Operacions.getDate(cursor.getString(4))));
                cursor.moveToNext();
            }
        }
    return movementos;
    }

    public BigDecimal getSaldo(ArrayList<Movemento> movementos){
        BigDecimal cnt = new BigDecimal("0");
        for(Movemento m : movementos){
            cnt = cnt.add(m.getImporte());
        }
        return cnt;
    }

    public void engadirMovemento(Movemento movemento){
        ContentValues valores = new ContentValues();
        valores.put("iban", movemento.getConta().getIban());
        valores.put("_id_movemento", movemento.get_id_movemento());
        valores.put("importe", Double.parseDouble(movemento.getImporte().toString()));
        valores.put("concepto",movemento.getConcepto());
        valores.put("data",Operacions.formatDateToDB(movemento.getData()));
        long idFila1 = sqlLiteDB.insert("MOVEMENTO", null, valores);
    }

    public void engadirConta(Conta conta){
        ContentValues valores = new ContentValues();
        valores.put("iban", conta.getIban());
        valores.put("saldo",conta.getSaldo().doubleValue());
        long idFila1 = sqlLiteDB.insert("CONTA", null, valores);
    }

    public Encarga consultaEmpSupActual(String dni){
        Encarga encarga = new Encarga();
        Persoa p = consultaPersoa(dni);
        Cursor cursor = sqlLiteDB.rawQuery("select * from Encarga where _id_emp_inf=? order by data_ini desc limit 1", new String[]{String.valueOf(p.get_id_persoa())});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                encarga.setEmp_sup(consultaPersoa(cursor.getInt(0)));
                encarga.setEmp_inf(p);
                encarga.setData_ini(Operacions.getDate(cursor.getString(2)));
                cursor.moveToNext();
            }
        }
        return encarga;
    }

    public ArrayList<Encarga> listadoEmpInf(String dni, boolean historico){
        ArrayList<Encarga> listaEmpInf = new ArrayList<>();
        Persoa p = consultaPersoa(dni);
        Cursor cursor;
        if(historico){
            cursor = sqlLiteDB.rawQuery("select * from Encarga where _id_emp_sup=? order by data_ini desc", new String[]{String.valueOf(p.get_id_persoa())});
        }else{
            cursor = sqlLiteDB.rawQuery("select * from Encarga where _id_emp_sup=? and data_fin IS NULL", new String[]{String.valueOf(p.get_id_persoa())});
        }
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                listaEmpInf.add(new Encarga(p,consultaPersoa(cursor.getInt(1)),Operacions.getDate(cursor.getString(2)),(cursor.getString(3)!=null ? Operacions.getDate(cursor.getString(3)) : null)));
                cursor.moveToNext();
            }
        }
        return listaEmpInf;
    }

    public ArrayList<Encarga> listadoHistEmpSup(String dni) {
        ArrayList<Encarga> listaEmpInf = new ArrayList<>();
        Persoa p = consultaPersoa(dni);
        Cursor cursor;
        cursor = sqlLiteDB.rawQuery("select * from Encarga where _id_emp_inf=? order by data_ini desc", new String[]{String.valueOf(p.get_id_persoa())});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                listaEmpInf.add(new Encarga(consultaPersoa(cursor.getInt(0)), p, Operacions.getDate(cursor.getString(2)), (cursor.getString(3) != null ? Operacions.getDate(cursor.getString(3)) : null)));
                cursor.moveToNext();
            }
        }
        return listaEmpInf;
    }

    public Traballa consultaEmpregoActual(String dni){
        Traballa traballa = new Traballa();

        Persoa p = consultaPersoa(dni);
        Cursor cursor = sqlLiteDB.rawQuery("select * from Traballa where _id_empregado=? and data_fin IS NULL", new String[]{String.valueOf(p.get_id_persoa())});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                traballa.setEmpregado(p);
                traballa.setSucursal(consultaSucursal(cursor.getInt(1)));
                traballa.setPosto(consultaPosto(cursor.getInt(2)));
                traballa.setData_ini(Operacions.getDate(cursor.getString(3)));
                cursor.moveToNext();
            }
        }

        return traballa;
    }

    public ArrayList<Traballa> listadoHistEmprego(String dni){
        ArrayList<Traballa> traballos = new ArrayList<>();
        Persoa p = consultaPersoa(dni);
        Cursor cursor = sqlLiteDB.rawQuery("select * from Traballa where _id_empregado=? order by data_ini desc", new String[]{String.valueOf(p.get_id_persoa())});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                traballos.add(new Traballa(p,consultaSucursal(cursor.getInt(1)),consultaPosto(cursor.getInt(2)),Operacions.getDate(cursor.getString(3)),(cursor.getString(4)!=null ? Operacions.getDate(cursor.getString(4)) : null)));
                cursor.moveToNext();
            }
        }

        return traballos;
    }


    public Posto consultaPosto(int id){
        Posto posto = new Posto();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Posto where _id_posto=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                posto.set_id_posto(id);
                posto.setDescricion(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        return posto;
    }

    public Sucursal consultaSucursal(int id){
        Sucursal sucursal = new Sucursal();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Sucursal where _id_sucursal=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                sucursal.set_id_sucursal(id);
                sucursal.setUbicacion(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        return sucursal;
    }

    public ArrayList<Sucursal> listadoSucursais(){
        ArrayList<Sucursal> sucursais = new ArrayList<>();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Sucursal order by _id_sucursal asc", new String[]{});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                sucursais.add(new Sucursal(cursor.getInt(0),cursor.getString(1)));
                cursor.moveToNext();
            }
        }

        return sucursais;
    }

    public ArrayList<Posto> listadoPostos(){
        ArrayList<Posto> postos = new ArrayList<>();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Posto order by _id_posto asc", new String[]{});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                postos.add(new Posto(cursor.getInt(0),cursor.getString(1)));
                cursor.moveToNext();
            }
        }

        return postos;
    }

    public ArrayList<Conta> listadoContas(){
        ArrayList<Conta> contas = new ArrayList<>();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Conta order by iban asc", new String[]{});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                contas.add(new Conta(cursor.getString(0),new BigDecimal(cursor.getString(1))));
                cursor.moveToNext();
            }
        }

        return contas;
    }

    public ArrayList<Persoa> listadoClientes(){
        ArrayList<Persoa> clientes = new ArrayList<>();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Persoa where cliente=?",new String[]{"1"});
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                Persoa persoa = new Persoa();
                persoa.set_id_persoa(cursor.getInt(0));
                persoa.setDni(cursor.getString(1));
                persoa.setNome(cursor.getString(2));
                persoa.setTelefono(cursor.getString(3));
                persoa.setEnderezo(cursor.getString(4));
                if(cursor.getString(5)!=null){
                    persoa.setData_contrato(Operacions.getDate(cursor.getString(5)));
                }
                persoa.setClave(cursor.getString(6));
                persoa.setEmpregado(cursor.getInt(7) == 1);
                persoa.setCliente(cursor.getInt(8) == 1);
                clientes.add(persoa);
                cursor.moveToNext();
            }
        }
        return clientes;
    }

    public int asignaCliente(String dniEmp, String dniCli){
        Persoa empregado = consultaPersoa(dniEmp);
        Persoa cliente = consultaPersoa(dniCli);
        Cursor cursor = sqlLiteDB.rawQuery("select * from TEN_ASIGNADO where _id_cliente=? and _id_empregado=? and data_fin IS NULL",new String[]{String.valueOf(cliente.get_id_persoa()),String.valueOf(empregado.get_id_persoa())});
        if (cursor.moveToFirst()) {
            return 1;
        }
        Cursor cursor2 = sqlLiteDB.rawQuery("select * from TEN_ASIGNADO where _id_cliente=?",new String[]{String.valueOf(cliente.get_id_persoa())});
        if(!cursor2.moveToFirst()){
            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_cliente", String.valueOf(cliente.get_id_persoa()));
            datosinsertar.put("_id_empregado", String.valueOf(empregado.get_id_persoa()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("TEN_ASIGNADO", null, datosinsertar);

            return 0;
        }
        Cursor cursor3 = sqlLiteDB.rawQuery("select * from TEN_ASIGNADO where _id_cliente=? and data_fin IS NULL",new String[]{String.valueOf(cliente.get_id_persoa())});
        if(cursor3.moveToFirst()){
            ContentValues datos = new ContentValues();
            datos.put("data_fin",Operacions.formatDateToDB(new Date()));
            String condicionwhere = "_id_cliente=? and data_fin IS NULL";
            String[] parametros = new String[]{String.valueOf(cliente.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("TEN_ASIGNADO",datos,condicionwhere,parametros);

            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_cliente", String.valueOf(cliente.get_id_persoa()));
            datosinsertar.put("_id_empregado", String.valueOf(empregado.get_id_persoa()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("TEN_ASIGNADO", null, datosinsertar);

            return 0;
        }

        return 2;
    }

    public int novaTitularidade(String iban, String dniCli){
        Persoa cliente = consultaPersoa(dniCli);
        Cursor cursor = sqlLiteDB.rawQuery("select * from TITULAR where _id_cliente=? and iban=? and data_fin IS NULL",new String[]{String.valueOf(cliente.get_id_persoa()),iban});
        if (cursor.moveToFirst()) {
            return 1;
        }
        ContentValues datosinsertar = new ContentValues();
        datosinsertar.put("_id_cliente", String.valueOf(cliente.get_id_persoa()));
        datosinsertar.put("iban", iban);
        datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
        long idFila1 = sqlLiteDB.insert("TITULAR", null, datosinsertar);

        return 0;
    }

    public int borrarTitularidade(String iban, String dniCli){
        Persoa cliente = consultaPersoa(dniCli);
        Cursor cursor = sqlLiteDB.rawQuery("select * from TITULAR where _id_cliente=? and iban=? and data_fin IS NULL",new String[]{String.valueOf(cliente.get_id_persoa()),iban});
        if (!cursor.moveToFirst()) {
            return 1;
        }

        ContentValues datos = new ContentValues();
        datos.put("data_fin",Operacions.formatDateToDB(new Date()));
        String condicionwhere = "iban=? and _id_cliente=? and data_fin IS NULL";
        String[] parametros = new String[]{iban,String.valueOf(cliente.get_id_persoa())};
        int rexistrosafectados = sqlLiteDB.update("TITULAR",datos,condicionwhere,parametros);

        return 0;
    }

    public ArrayList<Titular> listadoTitularConta(String iban, boolean historico){
        ArrayList<Titular> titulares = new ArrayList<>();
        Cursor cursor = null;
        if(historico){
            cursor = sqlLiteDB.rawQuery("select * from TITULAR where iban=? order by data_ini desc", new String[]{iban});
        }else{
            cursor = sqlLiteDB.rawQuery("select * from TITULAR where iban=? and data_fin IS NULL", new String[]{iban});
        }
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                titulares.add(new Titular(consultaPersoa(cursor.getInt(0)),consultaConta(iban),Operacions.getDate(cursor.getString(2)),(cursor.getString(3)!=null ? Operacions.getDate(cursor.getString(3)) : null)));
                cursor.moveToNext();
            }
        }
        return titulares;
    }

    public int engadirPersoa(Persoa persoa){
        if(consultaPersoa(persoa.getDni()).getDni()!=null){
            return 1;
        }
        ContentValues datosinsertar = new ContentValues();
        datosinsertar.put("_id_persoa", persoa.get_id_persoa());
        datosinsertar.put("dni",persoa.getDni());
        datosinsertar.put("nome",persoa.getNome());
        datosinsertar.put("telefono", persoa.getTelefono());
        datosinsertar.put("enderezo", persoa.getEnderezo());
        if(persoa.isEmpregado()){
            datosinsertar.put("data_contrato",Operacions.formatDateToDB(persoa.getData_contrato()));
        }
        datosinsertar.put("clave",persoa.getClave());
        datosinsertar.put("empregado",persoa.isEmpregado());
        datosinsertar.put("cliente", persoa.isCliente());

        long idFila1 = sqlLiteDB.insert("PERSOA", null, datosinsertar);

        return 0;
    }

    public ArrayList<Persoa> listadoPersoas(){
        ArrayList<Persoa> persoas = new ArrayList<>();
        Cursor cursor = sqlLiteDB.rawQuery("select * from Persoa",null);
        if (cursor.moveToFirst()) {                // Se non ten datos xa non entra
            while (!cursor.isAfterLast()) {     // Quédase no bucle ata que remata de percorrer o cursor. Fixarse que leva un ! (not) diante
                Persoa persoa = new Persoa();
                persoa.set_id_persoa(cursor.getInt(0));
                persoa.setDni(cursor.getString(1));
                persoa.setNome(cursor.getString(2));
                persoa.setTelefono(cursor.getString(3));
                persoa.setEnderezo(cursor.getString(4));
                persoa.setData_contrato(Operacions.getDate(cursor.getString(5)));
                persoa.setClave(cursor.getString(6));
                persoa.setEmpregado(cursor.getInt(7) == 1);
                persoa.setCliente(cursor.getInt(8) == 1);
                persoas.add(persoa);
                cursor.moveToNext();
            }
        }
        return persoas;
    }

    public int encargaEmpInf(String dniEmpSup, String dniEmpInf){
        Persoa empSup = consultaPersoa(dniEmpSup);
        Persoa empInf = consultaPersoa(dniEmpInf);
        Cursor cursor = sqlLiteDB.rawQuery("select * from ENCARGA where _id_emp_sup=? and _id_emp_inf=? and data_fin IS NULL",new String[]{String.valueOf(empSup.get_id_persoa()),String.valueOf(empInf.get_id_persoa())});
        if (cursor.moveToFirst()) {
            return 1;
        }
        Cursor cursor2 = sqlLiteDB.rawQuery("select * from ENCARGA where _id_emp_inf=?",new String[]{String.valueOf(empInf.get_id_persoa())});
        if(!cursor2.moveToFirst()){
            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_emp_sup", String.valueOf(empSup.get_id_persoa()));
            datosinsertar.put("_id_emp_inf", String.valueOf(empInf.get_id_persoa()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("ENCARGA", null, datosinsertar);

            return 0;
        }
        Cursor cursor3 = sqlLiteDB.rawQuery("select * from ENCARGA where _id_emp_inf=? and data_fin IS NULL",new String[]{String.valueOf(empInf.get_id_persoa())});
        if(cursor3.moveToFirst()){
            ContentValues datos = new ContentValues();
            datos.put("data_fin",Operacions.formatDateToDB(new Date()));
            String condicionwhere = "_id_emp_inf=? and data_fin IS NULL";
            String[] parametros = new String[]{String.valueOf(empInf.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("ENCARGA",datos,condicionwhere,parametros);

            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_emp_inf", String.valueOf(empInf.get_id_persoa()));
            datosinsertar.put("_id_emp_sup", String.valueOf(empSup.get_id_persoa()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("ENCARGA", null, datosinsertar);

            return 0;
        }

        return 2;
    }

    public int asignaEmprego(Persoa empregado, Posto posto, Sucursal sucursal){
        Cursor cursor = sqlLiteDB.rawQuery("select * from TRABALLA where _id_empregado=? and _id_posto=? and _id_sucursal=? and data_fin IS NULL",new String[]{String.valueOf(empregado.get_id_persoa()),String.valueOf(posto.get_id_posto()),String.valueOf(sucursal.get_id_sucursal())});
        if (cursor.moveToFirst()) {
            return 1;
        }

        Cursor cursor2 = sqlLiteDB.rawQuery("select * from TRABALLA where _id_empregado=?",new String[]{String.valueOf(empregado.get_id_persoa())});
        if(!cursor2.moveToFirst()){
            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_empregado", String.valueOf(empregado.get_id_persoa()));
            datosinsertar.put("_id_posto", String.valueOf(posto.get_id_posto()));
            datosinsertar.put("_id_sucursal", String.valueOf(sucursal.get_id_sucursal()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("TRABALLA", null, datosinsertar);

            return 0;
        }
        Cursor cursor3 = sqlLiteDB.rawQuery("select * from TRABALLA where _id_empregado=? and data_fin IS NULL",new String[]{String.valueOf(empregado.get_id_persoa())});
        if(cursor3.moveToFirst()){
            ContentValues datos = new ContentValues();
            datos.put("data_fin",Operacions.formatDateToDB(new Date()));
            String condicionwhere = "_id_empregado=? and data_fin IS NULL";
            String[] parametros = new String[]{String.valueOf(empregado.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("TRABALLA",datos,condicionwhere,parametros);

            ContentValues datosinsertar = new ContentValues();
            datosinsertar.put("_id_empregado", String.valueOf(empregado.get_id_persoa()));
            datosinsertar.put("_id_posto", String.valueOf(posto.get_id_posto()));
            datosinsertar.put("_id_sucursal", String.valueOf(sucursal.get_id_sucursal()));
            datosinsertar.put("data_ini", Operacions.formatDateToDB(new Date()));
            long idFila1 = sqlLiteDB.insert("TRABALLA", null, datosinsertar);

            return 0;
        }
        return 2;
    }

    public int cambiaContrasinal(String dni, String vellaClave, String novaClave){
        Persoa persoa = consultaPersoa(dni);
        try{
            if(!persoa.getClave().equals(Operacions.getSHA1(vellaClave))){
                return 1;
            }
            ContentValues datos = new ContentValues();
            datos.put("clave",Operacions.getSHA1(novaClave));
            String condicionwhere = "_id_persoa=?";
            String[] parametros = new String[]{String.valueOf(persoa.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("PERSOA",datos,condicionwhere,parametros);
            return 0;
        }catch (Exception ex){
            ex.printStackTrace();
            return 2;
        }
    }

    public int restablecerContrasinal(String dni){
        Persoa persoa = consultaPersoa(dni);
        try{
            ContentValues datos = new ContentValues();
            datos.put("clave",Operacions.getSHA1("abc123.."));
            String condicionwhere = "_id_persoa=?";
            String[] parametros = new String[]{String.valueOf(persoa.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("PERSOA",datos,condicionwhere,parametros);
            return 0;
        }catch (Exception ex){
            ex.printStackTrace();
            return 2;
        }
    }

    public int modificaPersoa(Persoa persoa){
        Persoa p = consultaPersoa(persoa.getDni());
        if(p.getDni()==null){
            return 1;
        }
        try{
            ContentValues datos = new ContentValues();
            datos.put("dni",persoa.getDni());
            datos.put("nome",persoa.getNome());
            datos.put("telefono",persoa.getTelefono());
            datos.put("enderezo",persoa.getEnderezo());
            datos.put("empregado",persoa.isEmpregado());
            datos.put("cliente",persoa.isCliente());
            if(persoa.isEmpregado()){
                datos.put("data_contrato",Operacions.formatDateToDB(persoa.getData_contrato()));
            }
            String condicionwhere = "_id_persoa=?";
            String[] parametros = new String[]{String.valueOf(persoa.get_id_persoa())};
            int rexistrosafectados = sqlLiteDB.update("PERSOA",datos,condicionwhere,parametros);
            return 0;
        }catch (Exception ex){
            ex.printStackTrace();
            return 2;
        }
    }

}
