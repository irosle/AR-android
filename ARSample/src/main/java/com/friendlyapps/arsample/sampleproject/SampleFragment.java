package com.friendlyapps.arsample.sampleproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlyapps.arsample.R;
import com.friendlyapps.arsample.library.ARFragment;
import com.friendlyapps.arsample.library.AROverlay;

/**
 * Created by Stuart on 11/06/2013.
 */
public class SampleFragment extends ARFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view =  super.onCreateView(inflater, container, saved);

        addOverLay();

        return view;
    }


    private void addOverLay(){


        AROverlay overlay = new AROverlay(getActivity().getApplicationContext(), 55.948671, -3.200199, R.layout.sample_overlay);
        getARView().addOverlay(overlay);

        overlay.setOnTapListener(new AROverlay.OnTapListener() {
            @Override
            public void onTap(AROverlay overlay) {
                Toast.makeText(getActivity().getApplicationContext(), "Tap..", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
