package com.bankalex.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Traballa;

import java.util.ArrayList;

public class VerHistEmprego extends AppCompatActivity {
    static ArrayAdapter<String> adaptador;
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_hist_emprego);
        if(getIntent().getExtras()!=null){
            persoa = Opening.baseDatos.consultaPersoa(getIntent().getExtras().getString("PERSOA"));
        }
        ListView lv_empregos = findViewById(R.id.lv_empregos);
        final ArrayList<Traballa> traballos = Opening.baseDatos.listadoHistEmprego(persoa.getDni());
        ArrayList<String> traballos_view = new ArrayList<>();

        for(Traballa t : traballos){
            String linha = "Sucursal: "+t.getSucursal().getUbicacion()+"\nPosto: "+t.getPosto().getDescricion();
            linha += "\nPeríodo: "+ Operacions.formatDate(t.getData_ini()).split(" ")[0]+" - "+(t.getData_fin()!=null ? Operacions.formatDate(t.getData_fin()).split(" ")[0] : "Actualidade");
            traballos_view.add(linha);
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, traballos_view);
        lv_empregos.setAdapter(adaptador);
    }
}
