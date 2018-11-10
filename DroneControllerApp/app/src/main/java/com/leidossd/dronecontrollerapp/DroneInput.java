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
import com.leidossd.djiwrapper.FlightControllerWrapper;

public class DroneInput extends AppCompatActivity {

    private TextView testText;
    private EditText xBox;
    private EditText yBox;
    private EditText zBox;
    private KeyListener zBoxListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drone_input);
        testText = findViewById(R.id.text_1);

        testText.setText("Off");
        xBox = (EditText) findViewById(R.id.x_box);
        yBox = (EditText) findViewById(R.id.y_box);
        zBox = (EditText) findViewById(R.id.z_box);
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
        String zString = yBox.getText().toString();

        int x = xString.isEmpty() ? Integer.parseInt(xString) : 0;
        int y = Integer.parseInt(yString);
        int z = Integer.parseInt(zString);

        if (x != 0 || y != 0) {
            if (zBoxListener == null) {
                zBoxListener = zBox.getKeyListener();
                zBox.setEnabled(false);
                zBox.setKeyListener(null);
            }
        } else if (zBox.getKeyListener() == null) {
            zBox.setKeyListener(zBoxListener);
            zBoxListener = null;
            zBox.setEnabled(true);
        }
    }
}
