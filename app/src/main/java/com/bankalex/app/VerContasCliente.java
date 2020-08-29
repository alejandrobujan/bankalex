package com.bankalex.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Titular;

import java.util.ArrayList;

public class VerContasCliente extends AppCompatActivity {
    ArrayAdapter<String> adaptador;
    Persoa persoa;
    ListView lv_contas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contas_cliente);
        persoa = Opening.baseDatos.consultaPersoa(getIntent().getStringExtra("dni"));
        String[] nome = persoa.getNome().split(" ");
        lv_contas = findViewById(R.id.lv_contas);
        enlazarAdaptador();
    }


    public void enlazarAdaptador(){
        final ArrayList<Titular> titulares = Opening.baseDatos.listadoTitular(Opening.currentDni,false);
        ArrayList<String> contas = new ArrayList<>();
        for (Titular t : titulares){
            contas.add("IBAN: "+t.getConta().getIban()+ " Saldo: "+t.getConta().getSaldo());
        }
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contas);

        //Enlace do adaptador co ListView
        lv_contas.setAdapter(adaptador);

        //Escoitador
        lv_contas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(VerContasCliente.this,VerMovementosEmp.class);
                String iban = titulares.get(position).getConta().getIban();
                intento.putExtra("IBAN",iban);
                startActivity(intento);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        enlazarAdaptador();
    }

}
