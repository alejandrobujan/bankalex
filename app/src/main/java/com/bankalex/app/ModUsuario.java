package com.bankalex.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Persoa;

import java.util.Date;

public class ModUsuario extends AppCompatActivity {
    EditText etDni;
    EditText etNome;
    EditText etTelefono;
    EditText etEnderezo;
    Button btnClave;
    CheckBox chk_emp;
    CheckBox chk_cli;
    Persoa persoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_usuario);
        etDni = (EditText)findViewById(R.id.et_m_dni);
        etNome = (EditText)findViewById(R.id.et_m_nome);
        etTelefono = (EditText)findViewById(R.id.et_m_telefono);
        etEnderezo = (EditText)findViewById(R.id.et_m_enderezo);
        btnClave = (Button) findViewById(R.id.btn_r_clave);
        chk_emp = (CheckBox)findViewById(R.id.chk_m_emp);
        chk_cli = (CheckBox)findViewById(R.id.chk_m_cli);
        String dniPersoa = getIntent().getExtras().getString("dni");
        persoa = Opening.baseDatos.consultaPersoa(dniPersoa);
        etDni.setText(dniPersoa);
        etNome.setText(persoa.getNome());
        etTelefono.setText(persoa.getTelefono());
        etEnderezo.setText(persoa.getEnderezo());
        chk_emp.setChecked(persoa.isEmpregado());
        if(persoa.isEmpregado()){
            chk_emp.setEnabled(false);
        }
        chk_cli.setChecked(persoa.isCliente());
        if(persoa.isCliente()){
            chk_cli.setEnabled(false);
        }
    }

    public void onRestablecerClick(View view) {
        if(Opening.baseDatos.restablecerContrasinal(persoa.getDni())==0){
            Toast.makeText(this,"Contrasinal restablecida",Toast.LENGTH_LONG).show();
        }

    }

    public void onModificarClick(View view) {
        try {
            if (!haiErros()) {
                persoa.setDni(etDni.getText().toString());
                persoa.setNome(etNome.getText().toString());
                persoa.setEnderezo(etEnderezo.getText().toString());
                persoa.setTelefono(etTelefono.getText().toString());
                persoa.setEmpregado(chk_emp.isChecked());
                persoa.setCliente(chk_cli.isChecked());
                if (chk_emp.isEnabled()&&chk_emp.isChecked()) {
                    persoa.setData_contrato(new Date());
                    Opening.baseDatos.encargaEmpInf(Opening.currentDni,persoa.getDni());
                }
                int resultado = Opening.baseDatos.modificaPersoa(persoa);
                if (resultado == 0) {
                    etDni.setError(null);
                    Toast.makeText(this, "Usuario modificado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this,"Erro ao modificar o usuario",Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

        public boolean haiErros(){
            etDni.setError(null);
            etNome.setError(null);
            etTelefono.setError(null);
            etEnderezo.setError(null);
            chk_cli.setError(null);
            boolean erros = false;
            if(etDni.getText().toString().equals("")){
                etDni.setError("O DNI non pode estar en branco");
                etDni.requestFocus();
                erros = true;
            }else{
                if(etDni.getText().toString().length()!=9){
                    etDni.setError("O DNI non ten a lonxitude adecuada");
                    etDni.requestFocus();
                    erros = true;
                }
                try {
                    Long.parseLong(etDni.getText().toString().substring(0,etDni.getText().toString().length()-1));
                }catch (NumberFormatException ex){
                    etDni.setError("O DNI non ten o formato correcto");
                    etDni.requestFocus();
                    erros = true;
                }
                if(!etDni.getText().toString().substring(etDni.getText().toString().length()-1).matches("[A-Z]")){
                    etDni.setError("O DNI non ten o formato correcto");
                    etDni.requestFocus();
                    erros = true;
                }
            }
            if(etNome.getText().toString().equals("")){
                etNome.setError("O nome non pode estar en branco");
                etNome.requestFocus();
                erros = true;
            }
            if(etTelefono.getText().toString().equals("")){
                etTelefono.setError("O teléfono non pode estar en branco");
                etTelefono.requestFocus();
                erros = true;
            }
            if(etEnderezo.getText().toString().equals("")){
                etEnderezo.setError("O enderezo non pode estar en branco");
                etEnderezo.requestFocus();
                erros = true;
            }
            if(chk_cli.isChecked()==false&&chk_emp.isChecked()==false){
                chk_cli.setError("Como mínimo hai que marcar unha das dúas opcións");
                erros = true;
            }
        return erros;
    }
}
