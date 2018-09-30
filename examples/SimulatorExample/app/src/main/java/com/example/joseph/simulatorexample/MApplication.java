/**
 * Created by Joseph on 9/25/2018.
 */

package com.example.joseph.simulatorexample;
import android.app.Application;
import android.content.Context;
import com.secneo.sdk.Helper;

public class MApplication extends Application {
    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
    }
}