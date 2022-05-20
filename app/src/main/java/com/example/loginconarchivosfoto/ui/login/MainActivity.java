package com.example.loginconarchivosfoto.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginconarchivosfoto.R;

public class MainActivity extends AppCompatActivity {

    private EditText email,password;
    private TextView mensaje;
    private Button login, registrar;
    private ViewModelMain vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVista();
    }
    public void inicializarVista() {

        email = findViewById(R.id.etUser);
        password = findViewById(R.id.etPass);
        mensaje = findViewById(R.id.tvMensaje);
        login = findViewById(R.id.btLogin);
        registrar = findViewById(R.id.btRegistro);
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ViewModelMain.class);
        vm.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mensaje.setText(s);
                mensaje.setVisibility(View.VISIBLE);
                email.setText("");
                password.setText("");
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.length()>0 && password.length()>0) {
                    vm.autenticar(email.getText().toString(), password.getText().toString());
                    email.setText("");
                    password.setText("");
                }
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.aRegistrar();
            }
        });

    }
}
