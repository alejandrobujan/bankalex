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
import android.widget.EditText;
import android.widget.ListView;
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

public class VerMovementos extends AppCompatActivity {
    boolean sdDisponhible = false;
    boolean sdAccesoEscritura = false;
    static String iban;
    static Conta conta;
    private static DialogoFragmento dialogoFragmento = new DialogoFragmento();
    static ArrayAdapter<String> adaptador;
    static ListView lv_movementos;
    static TextView tv_saldo;
    private final int CODIGO_IDENTIFICADOR=10;
    final int[] selectedPosition = new int[1];
    final ArrayList<String> movementos_view = new ArrayList<>();
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);

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
        Titular titular = Opening.baseDatos.consultaTitularidadActual(Opening.currentDni,iban);
        TextView tv_data_ini = findViewById(R.id.tv_data_ini);
        tv_data_ini.setText(Operacions.formatDate(titular.getData_ini()));
        lv_movementos = findViewById(R.id.lv_movementos);
        ArrayList<Movemento> movementos = Opening.baseDatos.listadoMovemento(iban);


        for(Movemento m : movementos){
            movementos_view.add("Data: "+Operacions.formatDate(m.getData())+"\nConcepto: "+m.getConcepto()+"\nImporte: "+m.getImporte().toString());
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movementos_view);
        lv_movementos.setAdapter(adaptador);
        registerForContextMenu(lv_movementos);
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

    public void onRealizarTransferenciaClick(View view) {
        FragmentManager fm = getSupportFragmentManager();
        dialogoFragmento.show(fm, "et");
    }

    public static class DialogoFragmento extends DialogFragment {

        EditText etIban;

        private boolean validarCampos() {
            if (!(Character.isLetter(etIban.getText().toString().charAt(0))&&Character.isLetter(etIban.getText().toString().charAt(1)))) {
                Toast.makeText(getContext(),"Débese introducir un código de país válido (dous primeiros caracteres).",Toast.LENGTH_LONG).show();
                return false;
            } else {
                try {
                    String valNum = etIban.getText().toString().substring(2);
                    int longitud = (valNum.length()%2!=0 ? valNum.length()/2+1 : valNum.length()/2);
                    Long.parseLong(valNum.substring(0,longitud));
                    Long.parseLong(valNum.substring(longitud));
                    return true;
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getContext(),"Débese introducir un código IBAN válido",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String infService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(infService);
            View inflador = li.inflate(R.layout.dialogo_transferencia, null);
            etIban = (EditText) inflador.findViewById(R.id.et_iban);
            final EditText etConcepto = (EditText) inflador.findViewById(R.id.et_concepto);
            final EditText etImporteNovo = (EditText) inflador.findViewById(R.id.et_importe_novo);

            builder.setTitle("Nova Transferencia:\n").setView(inflador);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int boton) {
                    if(conta.getSaldo().compareTo(new BigDecimal(etImporteNovo.getText().toString()))==-1){
                        Toast.makeText(getContext(),"Non hai suficiente saldo na conta",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(!validarCampos()){
                        return;
                    }
                    Conta orixe = conta;
                    Conta destino = Opening.baseDatos.consultaConta(etIban.getText().toString());
                    ArrayList<Movemento> movementosPrevios = Opening.baseDatos.listadoMovemento(iban);
                    Movemento movemento = new Movemento(orixe,movementosPrevios.size()+1,new BigDecimal(etImporteNovo.getText().toString()).negate(),etConcepto.getText().toString(),new Date());
                    Opening.baseDatos.engadirMovemento(movemento);
                    if(destino.getIban()!=null){
                        ArrayList<Movemento> movementosPreviosDestino = Opening.baseDatos.listadoMovemento(destino.getIban());
                        Movemento movementoDestino = new Movemento(destino,movementosPreviosDestino.size()+1,new BigDecimal(etImporteNovo.getText().toString()),etConcepto.getText().toString(),new Date());
                        Opening.baseDatos.engadirMovemento(movementoDestino);
                    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_mov_bar; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mov_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(VerMovementos.this,Preferencias.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
