package com.bankalex.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Titular;

import java.util.ArrayList;

public class VerHistContas extends AppCompatActivity {
    static ArrayAdapter<String> adaptador;
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_hist_contas);
        if(getIntent().getExtras()!=null){
            persoa = Opening.baseDatos.consultaPersoa(getIntent().getExtras().getString("PERSOA"));
        }
        ListView lv_hist_tit = findViewById(R.id.lv_hist_tit);
        final ArrayList<Titular> histTitular = Opening.baseDatos.listadoTitular(persoa.getDni(),true);
        ArrayList<String> histTitular_view = new ArrayList<>();

        for(Titular t : histTitular){
            String linha = "IBAN: "+t.getConta().getIban();
            linha += "\nPeríodo: "+ Operacions.formatDate(t.getData_ini()).split(" ")[0]+" - "+(t.getData_fin()!=null ? Operacions.formatDate(t.getData_fin()).split(" ")[0] : "Actualidade");
            histTitular_view.add(linha);
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, histTitular_view);
        lv_hist_tit.setAdapter(adaptador);
    }
}
