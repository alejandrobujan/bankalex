package com.bankalex.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Titular;

import java.util.ArrayList;

public class BancaCliente extends AppCompatActivity {
    ArrayAdapter<String> adaptador;
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    ListView lv_contas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banca_cliente);
        TextView tv_benvida = findViewById(R.id.tv_benvida);
        String[] nome = persoa.getNome().split(" ");
        tv_benvida.setText("Encantados de verte, "+nome[0]);

        lv_contas = findViewById(R.id.lv_contas);

        enlazarAdaptador();
    }

    public void onVerPerfilClick(View view) {
        Intent intento = new Intent(BancaCliente.this,Perfil.class);
        intento.putExtra("DESDECLIENTE",true);
        startActivity(intento);
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
                Intent intento = new Intent(BancaCliente.this,VerMovementos.class);
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

    public void onVerSucursaisClick(View view) {
        Intent intento = new Intent(BancaCliente.this,Sucursais.class);
        startActivity(intento);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.clave) {
            Intent intent = new Intent(BancaCliente.this, ModClave.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
