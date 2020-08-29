package com.bankalex.app;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Operacions;
import com.bankalex.app.model.Persoa;

import java.util.ArrayList;
import java.util.Date;

public class AltaUsuario extends AppCompatActivity {
    EditText etDni;
    EditText etNome;
    EditText etTelefono;
    EditText etEnderezo;
    EditText etClave;
    EditText etRepiteClave;
    CheckBox chk_emp;
    CheckBox chk_cli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);
        etDni = (EditText)findViewById(R.id.et_n_dni);
        etNome = (EditText)findViewById(R.id.et_n_nome);
        etTelefono = (EditText)findViewById(R.id.et_telefono);
        etEnderezo = (EditText)findViewById(R.id.et_enderezo);
        etClave = (EditText)findViewById(R.id.et_n_clave);
        etRepiteClave = (EditText)findViewById(R.id.et_repite_clave);
        chk_emp = (CheckBox)findViewById(R.id.chk_emp);
        chk_cli = (CheckBox)findViewById(R.id.chk_cli);
    }

    public void onAltaClick(View view) {
        try {
            if(!haiErros()){
                ArrayList<Persoa> persoas = Opening.baseDatos.listadoPersoas();
                Persoa persoa = new Persoa(persoas.size()+1, etDni.getText().toString(), etNome.getText().toString(), etTelefono.getText().toString(), etEnderezo.getText().toString(), null, Operacions.getSHA1(etClave.getText().toString()), chk_emp.isChecked(), chk_cli.isChecked());
                if(persoa.isEmpregado()){
                    persoa.setData_contrato(new Date());
                }
                int resultado = Opening.baseDatos.engadirPersoa(persoa);
                if (resultado==0){
                    etDni.setError(null);
                    Toast.makeText(this,"Usuario engadido correctamente",Toast.LENGTH_LONG).show();
                    etDni.setText("");
                    etNome.setText("");
                    etTelefono.setText("");
                    etEnderezo.setText("");
                    etClave.setText("");
                    etRepiteClave.setText("");
                    chk_cli.setChecked(false);
                    chk_emp.setChecked(false);
                    if(persoa.isEmpregado()){
                        Opening.baseDatos.encargaEmpInf(Opening.currentDni,persoa.getDni());
                    }
                }else{
                    etDni.setError("Xa existe un usuario con ese DNI");
                }
                etDni.requestFocus();

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean haiErros(){
        etDni.setError(null);
        etNome.setError(null);
        etTelefono.setError(null);
        etEnderezo.setError(null);
        etClave.setError(null);
        etRepiteClave.setError(null);
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
            if(!etDni.getText().toString().substring(etDni.getText().toString().length()-1).equals(Operacions.calcularLetra(Long.parseLong(etDni.getText().toString().substring(0,etDni.getText().toString().length()-1))))){
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
        if(etClave.getText().toString().equals("")){
            etClave.setError("A clave non pode estar en branco");
            etClave.requestFocus();
            erros = true;
        }else{
            if(etClave.getText().toString().length()<8){
                etClave.setError("A clave ten que ter como mínimo 8 caracteres");
                etClave.requestFocus();
                erros = true;
            }
        }
        if(!etClave.getText().toString().equals(etRepiteClave.getText().toString())){
            etRepiteClave.setError("As claves non coinciden");
            etRepiteClave.requestFocus();
            erros = true;
        }
        if(chk_cli.isChecked()==false&&chk_emp.isChecked()==false){
            chk_cli.setError("Como mínimo hai que marcar unha das dúas opcións");
            erros = true;
        }
        return erros;
    }
}
