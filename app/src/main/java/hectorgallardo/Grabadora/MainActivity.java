package hectorgallardo.Grabadora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private Button grabarBoton, detenerGrabacionBoton, reproducirBoton, detenerReproduccion, vibrarBoton;
    private EditText nombreGrabacionTexto;
    private TextView nombreLabel;
    private MediaRecorder grabadora;
    private MediaPlayer reproductor;
    private static final String LOG_TAG = "AudioRecording";
    private static String nombreArchivo = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    int numero=0;
    File dir = Environment.getExternalStorageDirectory();
    File carpeta;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grabarBoton = (Button)findViewById(R.id.grabar);
        detenerGrabacionBoton = (Button)findViewById(R.id.detenerGrabacion);
        reproducirBoton = (Button)findViewById(R.id.reproducir);
        detenerReproduccion = (Button)findViewById(R.id.dejarReproducir);
        nombreGrabacionTexto = (EditText)findViewById(R.id.nombreGrabacion);
        nombreLabel = (TextView)findViewById(R.id.label);
        vibrarBoton = (Button)findViewById(R.id.vibrar);
        detenerGrabacionBoton.setEnabled(false);
        reproducirBoton.setEnabled(false);
        detenerReproduccion.setEnabled(false);
        nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();
        nombreArchivo += "/AudioRecording.3gpp";
        carpeta = new File("/storage/emulated/0/GrabadoraRecords");
        int existe = 0;

        final File listaCarpetas[] = dir.listFiles();
        carpeta.listFiles();


        if (listaCarpetas != null) {
            for (int i = 0; i < listaCarpetas.length; i++) {

                if (listaCarpetas[i].toString() == "/storage/emulated/0/GrabadoraRecords"){
                    existe = 1;
                }
            }
        }


        if (existe==0){
            carpeta.mkdir();
        }

        vibrarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
            }
        });
        grabarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean vacio = true;
                String str = Integer.toString(numero);

                if(CheckPermissions()) {
                    boolean existeFichero=false;
                    if(nombreGrabacionTexto.getText().toString().equals("")){
                        vacio = true;
                        nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                        nombreArchivo +="/GrabadoraRecords/";
                        nombreArchivo +="AudioRecording.3gpp";
                    }
                    else {
                        vacio=false;
                        nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                        nombreArchivo +="/GrabadoraRecords/";
                        nombreArchivo += nombreGrabacionTexto.getText().toString()+".3gpp";
                    }
                    final File listFile[] = carpeta.listFiles();
                    if (listFile != null) {
                        for (int i = 0; i < listFile.length; i++){
                            if(listFile[i].toString().equals(nombreArchivo)){
                                existeFichero=true;
                            }
                        }
                    }
                    if (existeFichero==false && vacio ){
                        nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                        nombreArchivo +="/GrabadoraRecords/";
                        nombreArchivo +="AudioRecording.3gpp";
                    }
                    if (existeFichero==false && !vacio ){
                        nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                        nombreArchivo +="/GrabadoraRecords/";
                        nombreArchivo += nombreGrabacionTexto.getText().toString();
                        nombreArchivo +=".3gpp";
                    }
                    while (existeFichero){
                        if (nombreGrabacionTexto.getText().toString().equals("")){
                            numero +=1;
                            nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                            nombreArchivo +="/GrabadoraRecords/";
                            nombreArchivo +="AudioRecording";
                            str = Integer.toString(numero);
                            nombreArchivo +=str;
                            nombreArchivo +=".3gpp";

                            existeFichero=false;
                            if (listFile != null) {
                                for (int i = 0; i < listFile.length; i++){
                                    if(listFile[i].toString()!= nombreArchivo){
                                        existeFichero=false;
                                    }
                                }
                            }
                        }
                        else{
                            numero +=1;
                            nombreArchivo = Environment.getExternalStorageDirectory().getAbsolutePath();

                            nombreArchivo +="/GrabadoraRecords/";
                            nombreArchivo += nombreGrabacionTexto.getText().toString();
                            str = Integer.toString(numero);
                            nombreArchivo +=str;
                            nombreArchivo +=".3gpp";
                            existeFichero=false;
                            if (listFile != null) {
                                for (int i = 0; i < listFile.length; i++){
                                    if(listFile[i].toString()!= nombreArchivo){
                                        existeFichero=false;
                                    }
                                }
                            }
                        }
                    }
                    detenerGrabacionBoton.setEnabled(true);
                    grabarBoton.setEnabled(false);
                    reproducirBoton.setEnabled(false);
                    detenerReproduccion.setEnabled(false);
                    grabadora = new MediaRecorder();
                    grabadora.setAudioSource(MediaRecorder.AudioSource.MIC);
                    grabadora.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    grabadora.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    grabadora.setOutputFile(nombreArchivo);
                    try {
                        grabadora.prepare();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    grabadora.start();
                }
                else
                {
                    RequestPermissions();
                }
            }
        });
        detenerGrabacionBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerGrabacionBoton.setEnabled(false);
                grabarBoton.setEnabled(true);
                reproducirBoton.setEnabled(true);
                detenerReproduccion.setEnabled(true);
                grabadora.stop();
                grabadora.release();
                grabadora = null;
            }
        });
        reproducirBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerGrabacionBoton.setEnabled(false);
                grabarBoton.setEnabled(true);
                reproducirBoton.setEnabled(false);
                detenerReproduccion.setEnabled(true);
                reproductor = new MediaPlayer();
                try {
                    reproductor.setDataSource(nombreArchivo);
                    reproductor.prepare();
                    reproductor.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
        detenerReproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproductor.release();
                reproductor = null;
                detenerGrabacionBoton.setEnabled(false);
                grabarBoton.setEnabled(true);
                reproducirBoton.setEnabled(true);
                detenerReproduccion.setEnabled(false);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int permisoEscritura = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permisoGrabadora = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return permisoEscritura == PackageManager.PERMISSION_GRANTED && permisoGrabadora == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}