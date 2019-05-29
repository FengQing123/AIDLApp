package com.fengqing.aidlapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fengqing.aidlapp.aidl.BookManagerActivity;
import com.fengqing.aidlapp.binderpool.BinderPoolActivity;
import com.fengqing.aidlapp.provider.ProviderActivity;
import com.fengqing.aidlapp.socket.TCPClientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_to_aidl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookManagerActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_to_provider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProviderActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_to_tcp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TCPClientActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_to_binderpool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BinderPoolActivity.class);
                startActivity(intent);
            }
        });
    }
}
