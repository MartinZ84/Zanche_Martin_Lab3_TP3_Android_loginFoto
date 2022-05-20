package com.example.loginconarchivosfoto.request;

import android.content.Context;
import android.util.Log;

import com.example.loginconarchivosfoto.model.Usuario;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ApiClient {
    private static File archivo;

    private static void conectar(Context context){
        if(archivo == null){
            archivo  = new File(context.getFilesDir(),"objetos.dat");
        }
    }

    public static void guardar(Context context, Usuario usuario){
        conectar(context);
        try {
            //nodo
            FileOutputStream fo = new FileOutputStream(archivo);
            //buffer
            BufferedOutputStream bo = new BufferedOutputStream(fo);
            //Convertir de objeto a byte
            ObjectOutputStream ous = new ObjectOutputStream(bo);

            ous.writeObject(usuario);
            bo.flush();
            fo.close();
        }catch (IOException ex){
            Log.d("usuario",ex.toString());
        }
    }

    public static Usuario leer(Context context){
        Usuario usuario = null;
        conectar(context);
        try {
            FileInputStream fi=new FileInputStream(archivo);

            BufferedInputStream bi = new BufferedInputStream(fi);

            ObjectInputStream ois = new ObjectInputStream(bi);

             usuario = (Usuario) ois.readObject();

            fi.close();

            return usuario;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException ex){

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public static Usuario login(Context context, String email, String pass){
        conectar(context);
        Usuario usuario = leer(context);

        if(usuario.getMail().equals(email) && usuario.getPassword().equals(pass)){
            return usuario;
        }

        return null;
    }
}
