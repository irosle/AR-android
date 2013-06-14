package com.friendlyapps.arsample.library;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Stuart on 11/06/2013.
 *
 * The point of this fragment is to look after the life cycle of the ARView
 * It also creates the RelativeLayout that sits on top of the ARView holing the overlays
 *
 */
public class ARFragment extends Fragment{

    private ARView arView;


    /**
     * Method to progrmatically create fragment
     * @return Fragment
     */
    public static ARFragment newInstance() {
        ARFragment fragment = new ARFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        super.onCreateView(inflater, container, saved);
        Context context = getActivity().getApplicationContext();

        FrameLayout parent = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        AROverlayCanvas foreground = new AROverlayCanvas(context);

        if(supportsAR()){
             arView = new ARView(context);
             arView.setCamera(supportsCamera());

             arView.setForeground(foreground);

             parent.addView(arView, lp);
             arView.onStartGPS();
        }else{
             TextView textView = new TextView(context);
             textView.setText("Devise does not support AR"); // Shouldn't be hard coded but don't want to use resources files
             foreground.addView(textView);
        }

        parent.addView(foreground, lp);

        return parent;
    }

    public ARView getARView(){
        return arView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(arView != null){
            arView.onPause();
            arView.onStopGPS();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(arView != null){
            arView.onStart();
       }
    }

    private boolean supportsAR(){
        PackageManager PM = getActivity().getPackageManager();
        boolean acc = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        boolean compass = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        return (acc && compass);
   }

   private boolean supportsCamera(){
         PackageManager PM = getActivity().getPackageManager();
         return PM.hasSystemFeature(PackageManager.FEATURE_CAMERA);
   }
}