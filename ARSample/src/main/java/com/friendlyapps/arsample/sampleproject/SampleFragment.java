package com.friendlyapps.arsample.sampleproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlyapps.arsample.R;
import com.friendlyapps.arsample.library.ARFramgment;
import com.friendlyapps.arsample.library.AROverlay;

/**
 * Created by Stuart on 11/06/2013.
 */
public class SampleFragment extends ARFramgment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view =  super.onCreateView(inflater, container, saved);

        addOverLay();

        return view;
    }


    private void addOverLay(){

        AROverlay overlay = new AROverlay(getActivity().getApplicationContext(), 55.967986, -3.181017, R.layout.sample_overlay);
        getARView().addOverlay(overlay);

    }
}
