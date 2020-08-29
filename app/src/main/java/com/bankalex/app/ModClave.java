package com.bankalex.app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Persoa;

public class ModClave extends AppCompatActivity {
    EditText et_clave_actual;
    EditText et_nova_clave;
    EditText et_repite_nova_clave;
    Persoa persoa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_clave);
        et_clave_actual = (EditText)findViewById(R.id.et_clave_actual);
        et_nova_clave = (EditText)findViewById(R.id.et_nova_clave);
        et_repite_nova_clave = (EditText)findViewById(R.id.et_repite_nova_clave);
        persoa = Opening.baseDatos.consultaPersoa(Opening.currentDni);
    }


    public void onModClaveClick(View view) {
        if(!haiErros()){
            int resultado = Opening.baseDatos.cambiaContrasinal(persoa.getDni(),et_clave_actual.getText().toString(),et_nova_clave.getText().toString());
            if(resultado==1){
                Toast.makeText(this,"Clave actual incorrecta", Toast.LENGTH_LONG).show();
            }else if(resultado==0){
                Toast.makeText(this,"Clave modificada correctamente", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(this,"Erro ao cambiar a clave", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean haiErros(){
        et_clave_actual.setError(null);
        et_nova_clave.setError(null);
        et_repite_nova_clave.setError(null);
        boolean erros = false;
        if(et_clave_actual.getText().toString().equals("")){
            et_clave_actual.setError("A clave non pode estar en branco");
            et_clave_actual.requestFocus();
            erros = true;
        }else{
            if(et_clave_actual.getText().toString().length()<8){
                et_clave_actual.setError("A clave ten que ter como mínimo 8 caracteres");
                et_clave_actual.requestFocus();
                erros = true;
            }
        }
        if(et_nova_clave.getText().toString().equals("")){
            et_nova_clave.setError("A clave non pode estar en branco");
            et_nova_clave.requestFocus();
            erros = true;
        }else{
            if(et_nova_clave.getText().toString().length()<8){
                et_nova_clave.setError("A clave ten que ter como mínimo 8 caracteres");
                et_nova_clave.requestFocus();
                erros = true;
            }
        }
        if(!et_nova_clave.getText().toString().equals(et_repite_nova_clave.getText().toString())){
            et_repite_nova_clave.setError("As claves non coinciden");
            et_repite_nova_clave.requestFocus();
            erros = true;
        }
        return erros;
    }
}
