package com.example.loginconarchivosfoto.ui.registro;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginconarchivosfoto.model.Usuario;
import com.example.loginconarchivosfoto.request.ApiClient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ViewModelRegistro extends AndroidViewModel {

    private MutableLiveData<Usuario> usuario;
    private MutableLiveData<String> mensaje;
    private Context context;
    private byte [] foto;
    private ApiClient apiClient;


    public ViewModelRegistro(@NonNull Application application) {
        super(application);
        context=application.getApplicationContext();
        apiClient=new ApiClient();
    }

    public LiveData<Usuario> getUsuario(){
        if(usuario==null){
            usuario=new MutableLiveData<>();
        }
        return usuario;
    }
    public LiveData<String> getMensaje(){
        if(mensaje==null){
            mensaje=new MutableLiveData<>();
        }
        return mensaje;
    }

    public void registrar(String dni, String apellido, String nombre, String email, String pass){
        byte [] fotoArchivo;
        Usuario usuario;
        if(foto== null){
            usuario= apiClient.leer(context);
            foto= usuario.getB();
        }
        Usuario u=new Usuario(dni,apellido,nombre,email,pass,foto);
        apiClient.guardar(context,u);
        mensaje.setValue("El usuarios se guardo con exito");
    }

    public void guardarSinFoto(String dni, String apellido, String nombre, String email, String pass){
               Usuario u=new Usuario(dni,apellido,nombre,email,pass,foto);
        apiClient.guardar(context,u);

    }

    public void mostrar(Usuario u){
        if(u != null){
            u = apiClient.leer(context);
            usuario.setValue(u);
        }
    }

    public void respuetaDeCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE){
        Log.d("salida",requestCode+"");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Recupero los datos provenientes de la camara.
            Bundle extras = data.getExtras();
            //Casteo a bitmap lo obtenido de la camara.
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Rutina para optimizar la foto,
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            foto = baos.toByteArray();

            Usuario u= apiClient.leer(context);
            u.setB(foto);
            apiClient.guardar(context,u);
            usuario.setValue(u);


        }
    }

}
