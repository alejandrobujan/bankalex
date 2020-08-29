package com.bankalex.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Encarga;
import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;
import com.bankalex.app.model.Traballa;

public class Perfil extends AppCompatActivity {
    Persoa persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    Encarga encarga = Opening.baseDatos.consultaEmpSupActual(Opening.currentDni);
    Button btn_perf_esq;
    Button btn_perf_der;
    private final int CODIGO_IDENTIFICADOR=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        if(getIntent().getExtras()!=null) {
            if (getIntent().getExtras().getString("PERSOA") != null) {
                persoa = Opening.baseDatos.consultaPersoa(getIntent().getExtras().getString("PERSOA"));
                encarga = Opening.baseDatos.consultaEmpSupActual(getIntent().getExtras().getString("PERSOA"));
            }
            if (getIntent().getExtras().getBoolean("DESDECLIENTE") == true) {
                persoa.setEmpregado(false);
            }
        }
        TextView tv_nome = findViewById(R.id.tv_nome);
        tv_nome.setText(persoa.getNome());
        TextView tv_dni = findViewById(R.id.tv_dni);
        tv_dni.setText("DNI: "+persoa.getDni());
        TextView tv_ubicacion = findViewById(R.id.tv_ubicacion);
        tv_ubicacion.setText("Enderezo: "+persoa.getEnderezo());
        TextView tv_telefono = findViewById(R.id.tv_telefono);
        tv_telefono.setText("Teléfono: "+persoa.getTelefono());
        TextView tv_responsable = findViewById(R.id.tv_responsable);
        LinearLayout ll_responsable = findViewById(R.id.linear_layout_responsable);
        LinearLayout ll_traballa = findViewById(R.id.linear_layout_traballa);
        btn_perf_esq = findViewById(R.id.btn_perf_esq);
        btn_perf_der = findViewById(R.id.btn_perf_der);
        if(persoa.isEmpregado()) {
            btn_perf_esq.setText("HIST. EMPREGO");
            btn_perf_der.setText("HIST. SUPERIORES");
        }else {
            btn_perf_esq.setText("HIST. CONTAS");
            btn_perf_der.setText("HIST. ENCARGADOS");
        }
        if(getIntent().getExtras()!=null) {
                if (getIntent().getExtras().getBoolean("DESDECLIENTE") == true) {
                    persoa.setEmpregado(Opening.baseDatos.consultaPersoa(persoa.getDni()).isEmpregado());
                }
            }
        if(persoa.isEmpregado()){
            ll_traballa.setVisibility(View.VISIBLE);
            TextView tv_traballa = findViewById(R.id.tv_traballa);
            Traballa traballa = Opening.baseDatos.consultaEmpregoActual(persoa.getDni());
            if(traballa.getPosto()!=null&&traballa.getSucursal()!=null){
                tv_traballa.setText(traballa.getPosto().getDescricion()+" en "+traballa.getSucursal().getUbicacion()+" dende "+ Operacions.formatDate(traballa.getData_ini()).split(" ")[0]);
            }else{
                tv_traballa.setText("Sen posto asignado actualmente");
            }
            if(encarga.getEmp_sup()==null){
                ll_responsable.setVisibility(View.INVISIBLE);
            }else{
                tv_responsable.setText("Empregado Superior: "+encarga.getEmp_sup().getNome()+" - "+encarga.getEmp_sup().getTelefono());
                tv_responsable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT>=23){
                            int permiso = checkSelfPermission(Manifest.permission.CALL_PHONE);
                            if (permiso == PackageManager.PERMISSION_GRANTED){
                                chamarTelefono(encarga.getEmp_sup().getTelefono());
                            }
                            else{
                                Perfil.this.requestPermissions( new String[]{Manifest.permission.CALL_PHONE},CODIGO_IDENTIFICADOR);
                            }
                        }else{
                            int permiso = checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE);
                            if (permiso == PackageManager.PERMISSION_GRANTED){
                                chamarTelefono(encarga.getEmp_sup().getTelefono());
                            }
                        }
                    }
                });
            }

        }else{
            ll_traballa.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void chamarTelefono(String telefono){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+telefono));
        startActivity(callIntent);
    }

    public void onPerfilEsqClick(View view) {
        if(btn_perf_esq.getText().toString().equals("HIST. EMPREGO")){
            if(Opening.baseDatos.listadoHistEmprego(persoa.getDni()).isEmpty()){
                Toast.makeText(this,"Non hai empregos na historia",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intento = new Intent(Perfil.this,VerHistEmprego.class);
            intento.putExtra("PERSOA",persoa.getDni());
            startActivity(intento);
        }else{
            if(Opening.baseDatos.listadoTitular(persoa.getDni(),true).isEmpty()){
                Toast.makeText(this,"Non hai contas na historia",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intento = new Intent(Perfil.this,VerHistContas.class);
            intento.putExtra("PERSOA",persoa.getDni());
            startActivity(intento);
        }
    }

    public void onPerfilDerClick(View view) {
        if(btn_perf_der.getText().toString().equals("HIST. SUPERIORES")){
            if(Opening.baseDatos.listadoHistEmpSup(persoa.getDni()).isEmpty()){
                Toast.makeText(this,"Non hai empregados superiores na historia",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intento = new Intent(Perfil.this,VerHistSuperiores.class);
            intento.putExtra("PERSOA",persoa.getDni());
            startActivity(intento);
        }
    }
}
