package com.bankalex.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bankalex.app.model.Conta;
import com.bankalex.app.model.Movemento;
import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Titular;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class VerMovementosEmp extends AppCompatActivity {
    boolean sdDisponhible = false;
    boolean sdAccesoEscritura = false;
    static String iban;
    static Conta conta;
    static ArrayAdapter<String> adaptador;
    static ListView lv_movementos;
    static TextView tv_saldo;
    private final int CODIGO_IDENTIFICADOR=10;
    final int[] selectedPosition = new int[1];
    final ArrayList<String> movementos_view = new ArrayList<>();
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    private static DialogoFragmento2 dialogoFragmento2 = new DialogoFragmento2();
    private static DialogoFragmento3 dialogoFragmento3 = new DialogoFragmento3();
    public final static boolean[] ingreso = new boolean[1];
    String ibanActual;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_movementos);
        comprobarEstadoSD();
        Intent intent = getIntent();
        iban = intent.getStringExtra("IBAN");
        conta = Opening.baseDatos.consultaConta(iban);
        TextView tv_iban = findViewById(R.id.tv_iban);
        tv_iban.setText(iban);
        tv_saldo = findViewById(R.id.tv_saldo);
        tv_saldo.setText(conta.getSaldo().toString());
        TextView tv_titular_dende = findViewById(R.id.tv_titular_dende);
        tv_titular_dende.setText("Titulares: ");
        TextView tv_data_ini = findViewById(R.id.tv_data_ini);
        ArrayList<Titular> titulars = Opening.baseDatos.listadoTitularConta(iban,false);
        tv_data_ini.setText("");
        for (Titular t : titulars){
            tv_data_ini.append(t.getCliente().getNome()+", ");
        }
        tv_data_ini.setText(tv_data_ini.getText().toString().substring(0,tv_data_ini.getText().toString().length()-2));
        lv_movementos = findViewById(R.id.lv_movementos);
        ArrayList<Movemento> movementos = Opening.baseDatos.listadoMovemento(iban);
        Button btn_transferencia = findViewById(R.id.btn_trans);
        btn_transferencia.setVisibility(View.INVISIBLE);


        for(Movemento m : movementos){
            movementos_view.add("Data: "+Operacions.formatDate(m.getData())+"\nConcepto: "+m.getConcepto()+"\nImporte: "+m.getImporte().toString());
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movementos_view);
        lv_movementos.setAdapter(adaptador);
        registerForContextMenu(lv_movementos);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mov_emp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings_emp) {
            Intent intent = new Intent(VerMovementosEmp.this,Preferencias.class);
            startActivity(intent);
            return true;
        }
        ibanActual = iban;
        FragmentManager fm3 = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.novoTit:
                FragmentManager fm = getSupportFragmentManager();
                dialogoFragmento.show(fm, "et");
                return true;
            case R.id.borrarTit:
                FragmentManager fm2 = getSupportFragmentManager();
                dialogoFragmento2.show(fm2, "et");
                return true;
            case R.id.ingreso:
                ingreso[0] = true;
                dialogoFragmento3.show(fm3, "et");
                return true;
            case R.id.cargo:
                ingreso[0] = false;
                dialogoFragmento3.show(fm3, "et");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mov, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) lv_movementos.getAdapter();
        switch (item.getItemId()) {
            case R.id.pdf:
                selectedPosition[0] = info.position;
                if (Build.VERSION.SDK_INT>=23){
                    int permiso = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permiso == PackageManager.PERMISSION_GRANTED){
                        xerarPdf(movementos_view.get(info.position));
                    }else{
                        requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CODIGO_IDENTIFICADOR);
                    }
                }else {
                    int permiso = checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(permiso == PackageManager.PERMISSION_GRANTED){
                        xerarPdf(movementos_view.get(info.position));
                    }
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    public void comprobarEstadoSD() {
        String estado = Environment.getExternalStorageState();
        Log.e("SD", estado);

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            sdDisponhible = true;
            sdAccesoEscritura = true;
        } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
            sdDisponhible = true;
    }
    public void xerarPdf(String sometext){
        if(sdDisponhible&&sdAccesoEscritura) {
            String[] linhas = sometext.split("\n");

            SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String valorCartafol = preferencias.getString("cartafol", "pdf");

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            int y = 10;
            for(String linha : linhas){
                canvas.drawText(linha, 10, y, paint);
                y+=10;
            }
            document.finishPage(page);
            String directory_path = Environment.getExternalStorageDirectory()+"/"+valorCartafol+"/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String data = Operacions.formatDate(new Date());
            String targetPdf = directory_path+"bankalex-"+data.replace("/","-")+" - "+new Random().nextInt(100)+".pdf";
            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Toast.makeText(this, "Documento gardado na Tarxeta SD", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("main", "error "+e.toString());
                Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
            }
            document.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODIGO_IDENTIFICADOR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    xerarPdf(movementos_view.get(selectedPosition[0]));

                }
                return;
            }

        }
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

                    if(Opening.baseDatos.novaTitularidade(iban,etDni.getText().toString())==1){
                        Toast.makeText(getContext(),"Ese cliente xa é titular desa conta",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(),"Engadiuse correctamente a nova titularidade",Toast.LENGTH_LONG).show();
                    }
                    TextView tv_data_ini = getActivity().findViewById(R.id.tv_data_ini);
                    tv_data_ini.setText("");
                    ArrayList<Titular> titulars = Opening.baseDatos.listadoTitularConta(iban,false);
                    for (Titular t : titulars){
                        tv_data_ini.append(t.getCliente().getNome()+", ");
                    }
                    tv_data_ini.setText(tv_data_ini.getText().toString().substring(0,tv_data_ini.getText().toString().length()-2));
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

            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_borra_tit, null);
            final Spinner spDni = (Spinner) inflador.findViewById(R.id.spDni);
            ArrayList<Titular> titActuais = Opening.baseDatos.listadoTitularConta(iban,false);
            ArrayList<String> dnis = new ArrayList<>();
            for(Titular t : titActuais){
                dnis.add(t.getCliente().getDni());
            }
            if(dnis.isEmpty()){
                Toast.makeText(getContext(),"A conta xa non ten titulares",Toast.LENGTH_LONG).show();
                dismiss();
            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, dnis);

            // Opcional: layout usuado para representar os datos no Spinner
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Enlace do adaptador co Spinner do Layout.
            spDni.setAdapter(adaptador);
            final FragmentManager fm = this.getFragmentManager();
            builder.setTitle("Eliminar titularidade:\n").setView(inflador);
            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {

                    if(Opening.baseDatos.borrarTitularidade(iban,spDni.getSelectedItem().toString())==1){
                        Toast.makeText(getContext(),"Ese cliente non é titular desa conta",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(),"Eliminouse correctamente a titularidade",Toast.LENGTH_LONG).show();
                    }
                    TextView tv_data_ini = getActivity().findViewById(R.id.tv_data_ini);
                    tv_data_ini.setText("");
                    ArrayList<Titular> titulars = Opening.baseDatos.listadoTitularConta(iban,false);
                    for (Titular t : titulars){
                        tv_data_ini.append(t.getCliente().getNome()+", ");
                    }
                    tv_data_ini.setText(tv_data_ini.getText().toString().substring(0,tv_data_ini.getText().toString().length()-2));
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                }
            });
            return builder.create();


        }

    }

    public static class DialogoFragmento3 extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_conta_emp, null);
            final EditText etConcepto = (EditText) inflador.findViewById(R.id.et_concepto_emp);
            final EditText etImporteNovo = (EditText) inflador.findViewById(R.id.et_importe_novo_emp);
            if(ingreso[0]) {
                builder.setTitle("Ingreso:\n").setView(inflador);
            }else {
                builder.setTitle("Cargo:\n").setView(inflador);
            }
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    Conta conta = Opening.baseDatos.consultaConta(iban);
                    ArrayList<Movemento> movementosPrevios = Opening.baseDatos.listadoMovemento(iban);
                    Movemento movemento = null;
                    if(ingreso[0]){
                        movemento = new Movemento(conta,movementosPrevios.size()+1,new BigDecimal(etImporteNovo.getText().toString()),etConcepto.getText().toString(),new Date());
                        Toast.makeText(getContext(), "Ingreso realizado correctamente",Toast.LENGTH_LONG).show();
                    }else{
                        movemento = new Movemento(conta,movementosPrevios.size()+1,new BigDecimal(etImporteNovo.getText().toString()).negate(),etConcepto.getText().toString(),new Date());
                        Toast.makeText(getContext(), "Cargo realizado correctamente",Toast.LENGTH_LONG).show();
                    }
                    Opening.baseDatos.engadirMovemento(movemento);
                    ArrayList<Movemento> movementos = Opening.baseDatos.listadoMovemento(iban);
                    ArrayList<String> movementos_view = new ArrayList<>();

                    for(Movemento m : movementos){
                        movementos_view.add("Data: "+Operacions.formatDate(m.getData())+" \nConcepto: "+m.getConcepto()+" \nImporte: "+m.getImporte().toString());
                    }
                    adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, movementos_view);
                    lv_movementos.setAdapter(adaptador);
                    conta = Opening.baseDatos.consultaConta(iban);
                    tv_saldo.setText(conta.getSaldo().toString());

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
