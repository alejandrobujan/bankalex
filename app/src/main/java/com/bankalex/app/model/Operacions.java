package com.bankalex.app.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Operacions {

    public static String getSHA1(String palabra) throws Exception{
        String sha1 = "";
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(palabra.getBytes("utf8"));
        sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        return sha1;
    }

    public static Date getDate(String data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(data);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(Date data){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(data);
    }

    public static String formatDateToDB(Date data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(data);
    }

    public static String calcularLetra(long dni){
        String caracteres="TRWAGMYFPDXBNJZSQVHLCKE";
        int resto = (int)(dni%23);
        return String.valueOf(caracteres.charAt(resto));
    }
}
