package com.friendlyapps.arsample.sampleproject;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.friendlyapps.arsample.R;

public class SampleActivity extends FragmentActivity {


    /** Fragment is created in layout file so activity needs to do nothing progrmatically.
     * Fragment can be added using newInstance method. (Untested)

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
}
