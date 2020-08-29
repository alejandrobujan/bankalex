package com.bankalex.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Encarga;
import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;

import java.util.ArrayList;

public class VerHistSuperiores extends AppCompatActivity {
    static ArrayAdapter<String> adaptador;
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_hist_sup);
        if(getIntent().getExtras()!=null){
            persoa = Opening.baseDatos.consultaPersoa(getIntent().getExtras().getString("PERSOA"));
        }
        ListView lv_empreg_sup = findViewById(R.id.lv_empreg_sup);
        final ArrayList<Encarga> encargan = Opening.baseDatos.listadoHistEmpSup(persoa.getDni());
        ArrayList<String> encargan_view = new ArrayList<>();

        for(Encarga e : encargan){
            String linha = "DNI: "+e.getEmp_sup().getDni()+"\nNome: "+e.getEmp_sup().getNome();
            linha += "\nPeríodo: "+ Operacions.formatDate(e.getData_ini()).split(" ")[0]+" - "+(e.getData_fin()!=null ? Operacions.formatDate(e.getData_fin()).split(" ")[0] : "Actualidade");
            encargan_view.add(linha);
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, encargan_view);
        lv_empreg_sup.setAdapter(adaptador);
    }
}
