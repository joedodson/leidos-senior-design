package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class GridParentActivity extends AppCompatActivity implements GridFragment.GridInteractionListener, FlightControllerWrapper.PositionListener{

    FragmentManager fragmentManager;
    GridFragment gridFragment;
    TextView positionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_parent);

        positionText = findViewById(R.id.tv_grid_parent_position);
        fragmentManager = getSupportFragmentManager();
        gridFragment = new GridFragment();

        FlightControllerWrapper.getInstance().setPositionListener(this);

        fragmentManager.beginTransaction()
                .add(R.id.grid_fragment_container, gridFragment)
                .show(gridFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendInput(Coordinate coordinate) {
        showToast("GridParent received: " + coordinate.toString());
        FlightControllerWrapper.getInstance().gotoAbsoluteXYZ(coordinate, null);
    }

    public void takeOff(View view) {
        FlightControllerWrapper.getInstance().startTakeoff(null);
    }

    public void land(View view) {
        FlightControllerWrapper.getInstance().startLanding(null);
    }

    public void confirmLand(View view) {
        FlightControllerWrapper.getInstance().confirmLanding(null);
    }

    @Override
    public void updatePosition(Coordinate position){
        runOnUiThread(()->positionText.setText("Position: " + position));
    }
}
