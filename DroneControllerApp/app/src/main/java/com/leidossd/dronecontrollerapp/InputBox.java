package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.text.method.KeyListener;
import android.widget.EditText;

public class InputBox extends EditText {

    public InputBox(Context context) {
        super(context);
    }

    public KeyListener disableInputBox(){
        KeyListener listener = getKeyListener();
        setEnabled(false);
        setKeyListener(null);
        return listener;
    }

    public void enableInputBox(KeyListener listener){
        setKeyListener(listener);
        setEnabled(true);
    }
}
