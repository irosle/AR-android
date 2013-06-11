package com.friendlyapps.arsample.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Stuart on 11/06/2013.
 */
public class ARFramgment extends Fragment {

    private ARView arView;

    public static ARFramgment newInstance() {
        ARFramgment fragment = new ARFramgment();
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

        arView = new ARView(context);
        RelativeLayout forground = new RelativeLayout(context);

        arView.setForground(forground);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        parent.addView(arView, lp);
        parent.addView(forground, lp);

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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(arView != null){
            arView.onStart();
        }
    }
}
