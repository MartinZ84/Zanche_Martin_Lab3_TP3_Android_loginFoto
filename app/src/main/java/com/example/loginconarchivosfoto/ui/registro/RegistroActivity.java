package com.example.loginconarchivosfoto.ui.registro;

import static android.Manifest.permission.CAMERA;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginconarchivosfoto.R;
import com.example.loginconarchivosfoto.model.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private EditText dni,apellido,nombre,email,password;
    private ImageView ivFoto;
    private Button guardar, btnFoto;
    private  ViewModelRegistro vm;
    private static int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        validaPermisos();
        inicializar();
    }

    public void inicializar(){
        ivFoto = findViewById(R.id.ivFoto);
        btnFoto = findViewById(R.id.btnFoto);
        dni= findViewById(R.id.etDniPerfil);
        apellido=findViewById(R.id.etApellidoPerfil);
        nombre=findViewById(R.id.etNombrePerfil);
        email=findViewById(R.id.etEmailPerfil);
        password=findViewById(R.id.etContraseñaPerfil);
        guardar=findViewById(R.id.btGuardarPerfil);
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ViewModelRegistro.class);
        vm.getUsuario().observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                dni.setText(usuario.getDni() + "");
                apellido.setText(usuario.getApellido());
                nombre.setText(usuario.getNombre());
                email.setText(usuario.getMail());
                password.setText(usuario.getPassword());
                ivFoto.setImageBitmap(BitmapFactory.decodeByteArray(usuario.getB(),0,usuario.getB().length));

            }
        });

        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tranformar la img a bitmap
                vm.registrar(dni.getText().toString(), apellido.getText().toString(), nombre.getText().toString(), email.getText().toString(), password.getText().toString());
            }
        });

        vm.mostrar((Usuario) getIntent().getSerializableExtra("usuario"));

    }


    public void tomarFoto(View v){
        vm.guardarSinFoto(dni.getText().toString(), apellido.getText().toString(), nombre.getText().toString(), email.getText().toString(), password.getText().toString());
        //startActivityForResult es otra forma de iniciar una activity, pero esperando desde donde la llamé un resultado
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        vm.respuetaDeCamara(requestCode,resultCode,data,REQUEST_IMAGE_CAPTURE);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){

            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistroActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{CAMERA},100);
            }
        });
        dialogo.show();
    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{CAMERA},100);
        }

        return false;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        System.exit(0);
    }
}