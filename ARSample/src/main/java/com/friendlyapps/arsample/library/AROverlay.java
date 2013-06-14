package com.friendlyapps.arsample.library;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Stuart on 11/06/2013.
 */
public class AROverlay {

    private Location location;
    private View view;
    private boolean mIsVisible;

    // Listener
    private OnTapListener listener = null;

    public void setOnTapListener(OnTapListener listener) {
        this.listener = listener;
    }
    public interface OnTapListener{
        public void onTap(AROverlay overlay);
    }

    /**
     * Constructor
     * @param lat of overlay
     * @param lng of overlay
     * @param layout resources id of overlay layout
     */
    public AROverlay(Context context, double lat, double lng, int layout){
        location = new Location("Point");
        location.setLongitude(lat);
        location.setLongitude(lng);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null, false);

        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(listener != null){
                    listener.onTap(AROverlay.this);
                }
                return false;
            }
        });
    }

    public Location getLocation(){
        return location;
    }

    public void draw(int x, int y, RelativeLayout parent){

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.leftMargin = x - view.getWidth()/2;
        lp.topMargin = y - view.getHeight()/2;

        close();

        parent.addView(view, lp);
        view.setVisibility(View.VISIBLE);
        mIsVisible = true;

    }

    public void close() {
        if (mIsVisible) {
            mIsVisible = false;
            ((ViewGroup)view.getParent()).removeView(view);
        }
    }

}
