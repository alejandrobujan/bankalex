package com.bankalex.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bankalex.app.model.Encarga;
import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Posto;
import com.bankalex.app.model.Sucursal;

import java.util.ArrayList;

public class VerEmpregadosInf extends AppCompatActivity {
    static ArrayAdapter<String> adaptador;
    static Persoa persoa;
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    private static DialogoFragmento2 dialogoFragmento2 = new DialogoFragmento2();
    final static String[] dniEmpSel = new String[1];
    static ListView lv_empreg;
    static boolean historico;
    static ArrayList<Encarga> encargan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_empreg);
        Intent intent = getIntent();
        persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
        historico = intent.getExtras().getBoolean("HISTORICO");
        TextView tv_tit_emp_inf = findViewById(R.id.tv_tit_emp_imf);
        lv_empreg = findViewById(R.id.lv_empreg);
        refrescarAdaptador(this);
        if(historico){
            tv_tit_emp_inf.setText("Histórico de empregados subordinados: ");
        }else{
            tv_tit_emp_inf.setText("Empregados subordinados: ");
            registerForContextMenu(lv_empreg);
        }
        lv_empreg.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(VerEmpregadosInf.this,Perfil.class);
                String dniInf = encargan.get(position).getEmp_inf().getDni();
                intento.putExtra("PERSOA",dniInf);
                startActivity(intento);
            }
        });
    }

    public static void refrescarAdaptador(Context context){
        encargan = Opening.baseDatos.listadoEmpInf(persoa.getDni(),historico);
        ArrayList<String> encargan_view = new ArrayList<>();

        for(Encarga e : encargan){
            String linha = "DNI: "+e.getEmp_inf().getDni()+"\nNome: "+e.getEmp_inf().getNome();
            if(historico){
                linha += "\nPeríodo: "+ Operacions.formatDate(e.getData_ini()).split(" ")[0]+" - "+(e.getData_fin()!=null ? Operacions.formatDate(e.getData_fin()).split(" ")[0] : "Actualidade");
            }
            encargan_view.add(linha);
        }

        adaptador = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, encargan_view);
        lv_empreg.setAdapter(adaptador);


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
        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) lv_empreg.getAdapter();
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
                Intent intent = new Intent(VerEmpregadosInf.this,ModUsuario.class);
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
                    refrescarAdaptador(getContext());
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
                    Toast.makeText(getContext(),"Asignouse o emprego correctamente",Toast.LENGTH_LONG).show();
                    refrescarAdaptador(getContext());
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
