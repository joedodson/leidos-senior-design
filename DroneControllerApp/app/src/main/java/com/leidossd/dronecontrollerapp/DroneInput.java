package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DroneInput extends AppCompatActivity {

    private TextView testText;
    private InputBox xBox;
    private InputBox yBox;
    private InputBox zBox;
    private KeyListener xBoxListener = null;
    private KeyListener yBoxListener = null;
    private KeyListener zBoxListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drone_input);
        testText = findViewById(R.id.text_1);

        testText.setText("Off");
        xBox = findViewById(R.id.x_box);
        yBox = findViewById(R.id.y_box);
        zBox = findViewById(R.id.z_box);
        addCoordinateListener(xBox);
        addCoordinateListener(yBox);
    }

    public void onClicked(View view) {
        switch (view.getId()) {
            case(R.id.button_1): { //Back Button
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            }
            case(R.id.button_2): { //Next Button
            }
            case(R.id.button_3): { //Confirm button
                if(zBoxListener != null){ //Z is disabled
//                    FlightControllerWrapper.getInstance().goToAbsoluteXYZ(new Coordinate(xBox.getText(), yBox.getText(), 0));
                } else { //Dummy code for now.
//                    FlightControllerWrapper.getInstance().goToAbsoluteXYZ(new Coordinate(xBox.getText(), yBox.getText(), 0));
                }
            }
        }
    }

    private void addCoordinateListener(EditText box){
        box.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkTextStatus();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }
        });
    }

    private void checkTextStatus(){
        String xString = xBox.getText().toString();
        String yString = yBox.getText().toString();
        String zString = zBox.getText().toString();

        int x = xString.isEmpty() ? Integer.parseInt(xString) : 0;
        int y = yString.isEmpty() ? Integer.parseInt(yString) : 0;
        int z = zString.isEmpty() ? Integer.parseInt(zString) : 0;

        if (x != 0 || y != 0) {
            if (zBoxListener == null) {
                zBoxListener = zBox.disableInputBox();
            }
        } else if (zBox.getKeyListener() == null) {
            zBox.enableInputBox(zBoxListener);
            zBoxListener = null;
        }
        if (z != 0) {
            if (xBoxListener == null && yBoxListener == null) {
                xBoxListener = xBox.disableInputBox();
                yBoxListener = yBox.disableInputBox();
            }
        } else if (xBox.getKeyListener() == null && yBox.getKeyListener() == null) {
            xBox.enableInputBox(xBoxListener);
            yBox.enableInputBox(yBoxListener);
            xBoxListener = null;
            yBoxListener = null;
        }
    }
}
