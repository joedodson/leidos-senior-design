package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private int testValue = 0;
    private TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        testText = findViewById(R.id.text_1);
        testText.setText(String.valueOf(testValue));
    }

    public void onClicked(View view) {
        switch (view.getId()) {
            case(R.id.button_1): { //+ Button
                testText.setText(String.valueOf(++testValue));
                break;
            }
            case(R.id.button_2): { //- Button
                testText.setText(String.valueOf(--testValue));
                break;
            }
            case(R.id.button_3): { //Back button
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            }
        }
    }
}
