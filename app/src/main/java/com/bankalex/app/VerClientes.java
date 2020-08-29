package com.bankalex.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bankalex.app.model.Persoa;

import java.util.ArrayList;

public class VerClientes extends AppCompatActivity {
    ListView lv_t_clientes;
    static ArrayAdapter<String> adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clientes);
        lv_t_clientes = (ListView) findViewById(R.id.lv_t_clientes);
        ArrayList<Persoa> clientes = Opening.baseDatos.listadoClientes();
        ArrayList<String> contas_view = new ArrayList<>();

        for(Persoa c : clientes){
            contas_view.add(c.getDni()+" - "+c.getNome());
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contas_view);
        lv_t_clientes.setAdapter(adaptador);
        EditText et_search_cli = findViewById(R.id.et_search_cli);
        et_search_cli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                VerClientes.adaptador.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv_t_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VerClientes.this,VerContasCliente.class);
                intent.putExtra("dni",parent.getItemAtPosition(position).toString().split(" ")[0]);
                startActivity(intent);


            }
        });
        registerForContextMenu(lv_t_clientes);
    }

    /*public static class DialogoFragmento extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Engadir nova titularidade:\n");
            builder.setPositiveButton("Engadir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {

                }
            });
            return builder.create();


        }

    }*/
    /*
    * Intent intento = new Intent(VerClientes.this,Perfil.class);
                String dniCli = parent.getItemAtPosition(position).toString().split(" ")[0];
                intento.putExtra("PERSOA",dniCli);
                intento.putExtra("DESDECLIENTE",true);
                startActivity(intento);
    * */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_asig, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) lv_t_clientes.getAdapter();
        switch (item.getItemId()) {
            case R.id.asignar:
                Persoa cliente = Opening.baseDatos.consultaPersoa(adaptador.getItem(info.position).split(" - ")[0]);
                int resultado = Opening.baseDatos.asignaCliente(Opening.currentDni,cliente.getDni());
                if(resultado==1){
                    Toast.makeText(this,"O cliente xa estaba asignado a ti", Toast.LENGTH_LONG).show();
                }else if(resultado==0){
                    Toast.makeText(this,"O cliente foi asignado correctamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"Erro ao asignar o usuario", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.modificar_cli:
                Intent intent = new Intent(VerClientes.this,ModUsuario.class);
                intent.putExtra("dni",adaptador.getItem(info.position).split(" - ")[0]);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}
