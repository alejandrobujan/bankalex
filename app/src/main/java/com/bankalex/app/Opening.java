package com.bankalex.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bankalex.app.connectivity.BaseDatos;
import com.bankalex.app.model.*;

public class Opening extends FragmentActivity {


    public static BaseDatos baseDatos;
    public static String currentDni;
    private MediaPlayer mediaPlayer;
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    private void cargarMusica() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();

    }
    private void liberarRecursos(){
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void copiarBD(){
        String bddestino = "/data/data/" + getPackageName() + "/databases/"
                + BaseDatos.NOME_BD;
        File file = new File(bddestino);
        if (file.exists()) {
            return; // XA EXISTE A BASE DE DATOS
        }

        String pathbd = "/data/data/" + getPackageName()
                + "/databases/";
        File filepathdb = new File(pathbd);
        filepathdb.mkdirs();

        InputStream inputstream;
        try {
            inputstream = getAssets().open(BaseDatos.NOME_BD);
            OutputStream outputstream = new FileOutputStream(bddestino);

            int tamread;
            byte[] buffer = new byte[2048];

            while ((tamread = inputstream.read(buffer)) > 0) {
                outputstream.write(buffer, 0, tamread);
            }

            inputstream.close();
            outputstream.flush();
            outputstream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();

        if (!mediaPlayer.isPlaying()){
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        liberarRecursos();
    }

    private void xestionarEventos(){
        Button btn = (Button)findViewById(R.id.button_presentacion_aceptar);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentManager fm = getSupportFragmentManager();
                dialogoFragmento.show(fm, "et");

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        cargarMusica();
        xestionarEventos();
        copiarBD();
        if (baseDatos==null) {	// Abrimos a base de datos para escritura
            baseDatos = new BaseDatos(this);
            baseDatos.sqlLiteDB = baseDatos.getWritableDatabase();
        }
    }


    public static class DialogoFragmento extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_entrada_usuario, null);
            final EditText etDni = (EditText) inflador.findViewById(R.id.et_dni);
            final EditText etClave = (EditText) inflador.findViewById(R.id.et_clave);
            final FragmentManager fm = this.getFragmentManager();
            final DialogoFragmento2 dialogoFragmento2 = new DialogoFragmento2();
            builder.setTitle("Indica as credenciais:\n").setView(inflador);
            builder.setPositiveButton("Acceder", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    if(etDni.getText().toString().length()!=9){
                        Toast.makeText(getContext(),"O DNI non ten a lonxitude adecuada",Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        Long.parseLong(etDni.getText().toString().substring(0,etDni.getText().toString().length()-1));
                    }catch (NumberFormatException ex){
                        Toast.makeText(getContext(),"O DNI non ten o formato correcto",Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                        return;
                    }
                    if(!etDni.getText().toString().substring(etDni.getText().toString().length()-1).matches("[A-Z]")){
                        Toast.makeText(getContext(),"O DNI non ten o formato correcto",Toast.LENGTH_LONG).show();
                        return;
                    }

                    Persoa persoa = baseDatos.consultaPersoa(etDni.getText().toString());
                    try{
                        if(Operacions.getSHA1(etClave.getText().toString()).equals(persoa.getClave())) {
                            Opening.currentDni = etDni.getText().toString();
                            /*Intent intento = new Intent(getContext(), BancaEmpregado.class);
                            startActivity(intento);*/
                            Intent intento = null;
                            if(persoa.isEmpregado()&&(!persoa.isCliente())){
                                intento = new Intent(getContext(), BancaEmpregado.class);
                            }else if((!persoa.isEmpregado())&&persoa.isCliente()){
                                intento = new Intent(getContext(), BancaCliente.class);
                            }else if(persoa.isEmpregado()&&persoa.isCliente()){
                                dialogoFragmento2.show(fm, "et");
                                return;
                            }
                            startActivity(intento);
                        }else{
                            Toast.makeText(getContext(),"O usuario ou contrasinal non son válidos",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                   }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                }
            });
            return builder.create();


        }

    }

    public static class DialogoFragmento2 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Indica o rol co que acceder:\n");
            builder.setPositiveButton("Empregado", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    Intent intento = new Intent(getContext(), BancaEmpregado.class);
                    startActivity(intento);

                }
            });
            builder.setNegativeButton("Cliente", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    Intent intento = new Intent(getContext(), BancaCliente.class);
                    startActivity(intento);
                }
            });
            return builder.create();


        }

    }

}