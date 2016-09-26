package com.example.felipe.appgreen.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.felipe.appgreen.R;
import com.example.felipe.appgreen.Tools.Permissao;

public class MainActivity extends AppCompatActivity {

    private Button bt_calibrar;
    private Button bt_detectar;

    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permissao.validaPermissoes(1, this, permissoesNecessarias );

        bt_calibrar = (Button) findViewById(R.id.calibrarCamera);
        bt_detectar = (Button) findViewById(R.id.detectar);

        bt_calibrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraCalibrationActivity.class);
                startActivity(intent);
            }
        });

//        bt_detectar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent  = new Intent(MainActivity.this, DetectarActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
