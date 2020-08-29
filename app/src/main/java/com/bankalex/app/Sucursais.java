package com.bankalex.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bankalex.app.model.Dia;
import com.bankalex.app.model.Horario;
import com.bankalex.app.model.Sucursal;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Sucursais extends AppCompatActivity {

    TextView tv_horarios;
    File rutaCompleta;
    public static enum TIPOREDE{MOBIL,ETHERNET,WIFI,SENREDE};
    private TIPOREDE conexion;
    String urlXML = "https://dl.dropboxusercontent.com/s/psl96q8ajbqtrwr/horarios.xml?dl=0";
    ArrayList<Horario> horarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursais);
        conexion=comprobarRede();
        if(conexion==TIPOREDE.SENREDE){
            Toast.makeText(this,"NON SE PODE ACCEDER SEN CONEXIÓN Á REDE", Toast.LENGTH_SHORT).show();
            finish();
        }
        tv_horarios = findViewById(R.id.tv_horarios);
        tv_horarios.setText("");
        Thread fio = new Thread() {
            @Override
            public void run() {
                descargarArquivo();
                mostrarXML();
            }
        };
        try {
            fio.start();
            fio.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        final Spinner spin_sucursais = findViewById(R.id.spin_sucursais);
        ArrayList<Sucursal> sucursais = Opening.baseDatos.listadoSucursais();
        ArrayList<String> sucursais_view = new ArrayList<>();
        for(Sucursal s : sucursais){
            sucursais_view.add(s.get_id_sucursal()+ " - "+s.getUbicacion());
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sucursais_view);

        // Opcional: layout usuado para representar os datos no Spinner
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Enlace do adaptador co Spinner do Layout.
        spin_sucursais.setAdapter(adaptador);

        // Escoitador
        spin_sucursais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                tv_horarios.setText("Horario da Sucursal de "+((String)spin_sucursais.getSelectedItem()).split(" - ")[1]+":\n\n");
                for(Dia d: horarios.get(pos).getDias()){
                    tv_horarios.append("\t"+d.toString()+"\n");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); // Fin da clase anónima

    }

    public void descargarArquivo(){

        URL url=null;
        try {
            url = new URL(urlXML);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        }
        HttpURLConnection conn=null;
        String nomeArquivo = Uri.parse(urlXML).getLastPathSegment();
        rutaCompleta = new File(getFilesDir(), nomeArquivo);
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);  	/* milliseconds */
            conn.setConnectTimeout(15000);  /* milliseconds */
            conn.setRequestMethod("POST");
            conn.setDoInput(true);			/* Indicamos que a conexión vai recibir datos */

            conn.connect();

            int response = conn.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK){
                return;
            }
            OutputStream os = new FileOutputStream(rutaCompleta);
            InputStream in = conn.getInputStream();
            byte data[] = new byte[1024];	// Buffer a utilizar
            int count;
            while ((count = in.read(data)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
            os.close();
            in.close();
            conn.disconnect();
            Log.i("COMUNICACION","ACABO");
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("COMUNICACION",e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("COMUNICACION",e.getMessage());
        }
    }

    private TIPOREDE comprobarRede(){
        NetworkInfo networkInfo=null;

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch(networkInfo.getType()){
                case ConnectivityManager.TYPE_MOBILE:
                    return TIPOREDE.MOBIL;
                case ConnectivityManager.TYPE_ETHERNET:
                    // ATENCION API LEVEL 13 PARA ESTA CONSTANTE
                    return TIPOREDE.ETHERNET;
                case ConnectivityManager.TYPE_WIFI:
                    // NON ESTEAS MOITO TEMPO CO WIFI POSTO
                    // MAIS INFORMACION EN http://www.avaate.org/
                    return TIPOREDE.WIFI;
            }
        }
        return TIPOREDE.SENREDE;
    }

    public void mostrarXML(){
        ArrayList<Dia> dias = null;
        try {
            InputStream is = new FileInputStream(rutaCompleta);

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");

            int evento = parser.nextTag();
            Horario horario = null;

            while(evento != XmlPullParser.END_DOCUMENT) {
                if(evento == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("horario")) {
                        int id = Integer.parseInt(parser.getAttributeValue(0));
                        evento = parser.nextTag();
                        dias = new ArrayList<>();
                        while(parser.getName().equals("dia")) {
                            String nome = parser.getAttributeValue(0);
                            evento = parser.nextTag();
                            String horaApertura = parser.nextText();
                            evento = parser.nextTag();
                            String horaPeche = parser.nextText();
                            evento = parser.nextTag();
                            dias.add(new Dia(nome,horaApertura,horaPeche));
                            evento = parser.nextTag();
                        }
                        horario = new Horario(id,dias);
                    }
                }
                if(evento == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("horario")) {
                        horarios.add(horario);
                    }
                }

                evento = parser.next();
            }

            is.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
