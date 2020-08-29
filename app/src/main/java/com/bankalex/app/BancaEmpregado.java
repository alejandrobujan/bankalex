package com.bankalex.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bankalex.app.model.Encarga;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Posto;
import com.bankalex.app.model.Sucursal;

import java.util.ArrayList;

public class BancaEmpregado extends AppCompatActivity {
    ListView lv_cli_asig;
    ListView lv_emp_inf;
    ArrayAdapter<String> adaptador_cli;
    ArrayAdapter<String> adaptador_emp;
    LinearLayout ll_emp_sub;
    final static String[] dniEmpSel = new String[1];
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    private static DialogoFragmento2 dialogoFragmento2 = new DialogoFragmento2();



    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banca_empregado);
        TextView tv_benvida_emp = findViewById(R.id.tv_benvida_emp);
        String[] nome = persoa.getNome().split(" ");
        tv_benvida_emp.setText("Encantados de verte, "+nome[0]);
        //lv_cli_asig = findViewById(R.id.lv_cli_asig);
        lv_emp_inf = findViewById(R.id.lv_emp_inf);
        ll_emp_sub = findViewById(R.id.ll_emp_sub);
        enlazarAdaptadores();

    }

    public void onVerPerfilClick(View view) {
        Intent intento = new Intent(BancaEmpregado.this,Perfil.class);
        startActivity(intento);
    }

    public void enlazarAdaptadores(){
        /*final ArrayList<TenAsignado> asignacions = Opening.baseDatos.listadoCliAsig(Opening.currentDni,false);
        ArrayList<String> elementosAsig = new ArrayList<>();
        for (TenAsignado ta : asignacions){
            elementosAsig.add("DNI: "+ta.getCliente().getDni()+"\nNome: "+ta.getCliente().getNome());
        }
        adaptador_cli = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elementosAsig);

        //Enlace do adaptador co ListView
        lv_cli_asig.setAdapter(adaptador_cli);

        //Escoitador
        lv_cli_asig.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(BancaEmpregado.this,Perfil.class);
                String dniCli = asignacions.get(position).getCliente().getDni();
                intento.putExtra("PERSOA",dniCli);
                intento.putExtra("DESDECLIENTE",true);
                startActivity(intento);
            }
        });
        lv_cli_asig.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                ListView lv = (ListView) v;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        if(lv.getAdapter().getCount()>3){
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        if(lv.getAdapter().getCount()>3) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/

        final ArrayList<Encarga> encargas = Opening.baseDatos.listadoEmpInf(Opening.currentDni,false);
        ArrayList<String> elementosEnc = new ArrayList<>();
        for (Encarga e : encargas){
            elementosEnc.add("DNI: "+e.getEmp_inf().getDni()+"\nNome: "+e.getEmp_inf().getNome());
        }
        adaptador_emp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elementosEnc);

        //Enlace do adaptador co ListView
        lv_emp_inf.setAdapter(adaptador_emp);

        //Escoitador
        lv_emp_inf.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(BancaEmpregado.this,Perfil.class);
                String dniInf = encargas.get(position).getEmp_inf().getDni();
                intento.putExtra("PERSOA",dniInf);
                startActivity(intento);
            }
        });
        lv_emp_inf.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                ListView lv = (ListView) v;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        if(lv.getAdapter().getCount()>3){
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        if(lv.getAdapter().getCount()>3) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        registerForContextMenu(lv_emp_inf);
    }

    public void onEmpInfClick(View view) {
        if(Opening.baseDatos.listadoEmpInf(Opening.currentDni,false).isEmpty()){
            Toast.makeText(this,"Non hai empregados subordinados actualmente",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intento = new Intent(BancaEmpregado.this,VerEmpregadosInf.class);
        intento.putExtra("HISTORICO",false);
        startActivity(intento);
    }

    public void onEmpInfHistClick(View view) {
        if(Opening.baseDatos.listadoEmpInf(Opening.currentDni,true).isEmpty()){
            Toast.makeText(this,"Non hai empregados subordinados na historia",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intento = new Intent(BancaEmpregado.this,VerEmpregadosInf.class);
        intento.putExtra("HISTORICO",true);
        startActivity(intento);
    }

    public void onVerContasClick(View view) {
        Intent intento = new Intent(BancaEmpregado.this,VerContas.class);
        startActivity(intento);
    }

    public void onVerClientesClick(View view) {
        Intent intento = new Intent(BancaEmpregado.this,VerClientes.class);
        startActivity(intento);
    }

    @Override
    protected void onResume(){
        super.onResume();
        enlazarAdaptadores();
    }

    public void onNovoUsuarioClick(View view){
        Intent intent = new Intent(BancaEmpregado.this,AltaUsuario.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_encarga, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) lv_emp_inf.getAdapter();
        dniEmpSel[0] = adaptador.getItem(info.position).split("\n")[0].split(" ")[1];
        switch (item.getItemId()) {
            case R.id.delegar:
                FragmentManager fm = getSupportFragmentManager();
                dialogoFragmento.show(fm,"df");
                return true;
            case R.id.emprego:
                FragmentManager fm2 = getSupportFragmentManager();
                dialogoFragmento2.show(fm2,"df");
                return true;
            case R.id.modificar_emp:
                Intent intent = new Intent(BancaEmpregado.this,ModUsuario.class);
                intent.putExtra("dni",dniEmpSel[0]);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static class DialogoFragmento extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String dniEmpInf = dniEmpSel[0];
            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_deleg, null);
            final EditText etDni = (EditText) inflador.findViewById(R.id.et_dni_deleg);
            builder.setTitle("Delegar empregado inferior:\n").setView(inflador);
            builder.setPositiveButton("Delegar", new DialogInterface.OnClickListener() {
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

                    if(Opening.baseDatos.consultaPersoa(etDni.getText().toString()).getDni()==null||Opening.baseDatos.consultaPersoa(etDni.getText().toString()).isEmpregado()==false){
                        Toast.makeText(getContext(),"Non existe ningún empregado asociado a ese DNI",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(Opening.baseDatos.encargaEmpInf(etDni.getText().toString(),dniEmpInf)==1){
                        Toast.makeText(getContext(),"Ese empregado xa está encargado a ese outro",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(),"Asignouse correctamente a ese empregado",Toast.LENGTH_LONG).show();
                    }
                    ((BancaEmpregado)getActivity()).enlazarAdaptadores();
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
            final String dniEmpInf = dniEmpSel[0];
            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_traballa, null);
            final ArrayList<Posto> postos = Opening.baseDatos.listadoPostos();
            final ArrayList<String> postos_view = new ArrayList<>();
            for (Posto p : postos){
                postos_view.add(p.getDescricion());
            }
            final Spinner spPosto = (Spinner) inflador.findViewById(R.id.spPosto);

            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, postos_view);

            // Opcional: layout usuado para representar os datos no Spinner
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Enlace do adaptador co Spinner do Layout.
            spPosto.setAdapter(adaptador);

            final ArrayList<Sucursal> sucursais = Opening.baseDatos.listadoSucursais();
            final ArrayList<String> sucursais_view = new ArrayList<>();
            for (Sucursal s : sucursais){
                sucursais_view.add(s.getUbicacion());
            }
            final Spinner spSucursal = (Spinner) inflador.findViewById(R.id.spSucursal);

            ArrayAdapter<String> adaptador2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sucursais_view);

            // Opcional: layout usuado para representar os datos no Spinner
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Enlace do adaptador co Spinner do Layout.
            spSucursal.setAdapter(adaptador2);

            builder.setTitle("Asignar novo emprego:\n").setView(inflador);
            builder.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    Opening.baseDatos.asignaEmprego(Opening.baseDatos.consultaPersoa(dniEmpInf),postos.get(spPosto.getSelectedItemPosition()),sucursais.get(spSucursal.getSelectedItemPosition()));
                    ((BancaEmpregado)getActivity()).enlazarAdaptadores();
                    Toast.makeText(getContext(),"Asignouse o emprego correctamente",Toast.LENGTH_LONG).show();

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                }
            });
            return builder.create();


        }

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
            Intent intent = new Intent(BancaEmpregado.this, ModClave.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
