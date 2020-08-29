package com.bankalex.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bankalex.app.model.Conta;
import com.bankalex.app.model.Sucursal;
import com.bankalex.app.model.Titular;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class VerContas extends AppCompatActivity {
    static ArrayAdapter<String> adaptador;
    static ListView lv_contas;
    static String ibanActual;
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    private static DialogoFragmento2 dialogoFragmento2 = new DialogoFragmento2();

    private static ArrayList<String> iban = new ArrayList<>();
    final static ArrayList<String> contas_view = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_contas);
        lv_contas = (ListView) findViewById(R.id.lv_contas);
        ArrayList<Conta> contas = Opening.baseDatos.listadoContas();

        for(Conta c : contas){
            contas_view.add("IBAN: "+ c.getIban());
        }


        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contas_view);
        lv_contas.setAdapter(adaptador);
        EditText et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                VerContas.adaptador.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv_contas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VerContas.this,VerMovementosEmp.class);
                intent.putExtra("IBAN",contas_view.get(position).split(" ")[1]);
                ArrayList<Titular> titulars = Opening.baseDatos.listadoTitularConta(contas_view.get(position).split(" ")[1],false);
                if(titulars.isEmpty()){
                    FragmentManager fm = getSupportFragmentManager();
                    dialogoFragmento.show(fm, "et");
                    iban.clear();
                    iban.add(contas_view.get(position).split(" ")[1]);
                    return;
                }
                startActivity(intent);

            }
        });


    }


    public static class DialogoFragmento extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_nova_tit, null);
            final EditText etDni = (EditText) inflador.findViewById(R.id.et_dni_nt);
            final FragmentManager fm = this.getFragmentManager();
            final String ibanEmb = iban.get(0);
            builder.setTitle("Engadir nova titularidade:\n").setView(inflador);
            builder.setPositiveButton("Engadir", new DialogInterface.OnClickListener() {
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

                    if(Opening.baseDatos.consultaPersoa(etDni.getText().toString()).getDni()==null||Opening.baseDatos.consultaPersoa(etDni.getText().toString()).isCliente()==false){
                        Toast.makeText(getContext(),"Non existe ningún cliente asociado a ese DNI",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(Opening.baseDatos.novaTitularidade(ibanEmb,etDni.getText().toString())==1){
                        Toast.makeText(getContext(),"Ese cliente xa é titular desa conta",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(),"Engadiuse correctamente a nova titularidade",Toast.LENGTH_LONG).show();
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

    public void onNovaContaClick(View view){
        FragmentManager fm = getSupportFragmentManager();
        dialogoFragmento2.show(fm, "et");
    }

    public static void novaConta(Context context, String parte2){
        long segundaParte = (long) (new Random().nextDouble() * 999999L);
        long terceiraParte = (long) (new Random().nextDouble() * 9999999999L);
        parte2 += String.valueOf(segundaParte);
        String parte3 = String.valueOf(terceiraParte);
        if(parte2.length()!=10){
            int dixitosFaltan = 10 - parte2.length();
            for (int i = 0;i < dixitosFaltan ; i++){
                parte2 = parte2 + "0";
            }
        }
        if(parte3.length()!=10){
            int dixitosFaltan = 10 - parte3.length();
            for (int i = 0;i < dixitosFaltan ; i++){
                parte3 = parte3 + "0";
            }
        }
        Conta conta = new Conta("ES83"+parte2+parte3,new BigDecimal("0"));
        Opening.baseDatos.engadirConta(conta);
        ArrayList<Conta> contas = Opening.baseDatos.listadoContas();
        VerContas.contas_view.clear();

        for(Conta c : contas){
            VerContas.contas_view.add("IBAN: "+ c.getIban());
        }
        adaptador = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, VerContas.contas_view);
        lv_contas.setAdapter(adaptador);
    }

    public static class DialogoFragmento2 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_borra_tit, null);
            final Spinner spDni = (Spinner) inflador.findViewById(R.id.spDni);
            ArrayList<Sucursal> sucursals = Opening.baseDatos.listadoSucursais();
            ArrayList<String> sucursais_view = new ArrayList<>();
            for(Sucursal s : sucursals){
                sucursais_view.add(s.getUbicacion());
            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sucursais_view);

            // Opcional: layout usuado para representar os datos no Spinner
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Enlace do adaptador co Spinner do Layout.
            spDni.setAdapter(adaptador);
            final FragmentManager fm = this.getFragmentManager();
            builder.setTitle("Selecciona a sucursal:\n").setView(inflador);
            builder.setPositiveButton("Engadir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    String parte2 = ""+(spDni.getSelectedItemPosition()+1);
                    for (; parte2.length()<4;){
                        parte2 = "0"+parte2;
                    }
                    novaConta(getContext(),parte2);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                }
            });
            return builder.create();


        }

    }

}
