package com.friendlyapps.arsample.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stuart on 11/06/2013.
 */
public class ARView extends SurfaceView implements SensorEventListener, LocationListener {

    private Context context;
    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private Camera camera;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
    private boolean sensorStarted = false;

    // Settings

    private boolean show_compass = true;

    private RelativeLayout forground = null;

    private SurfaceHolder previewHolder = null;


    private float[] rotationMatrix = new float[16];
    private float[] orientation = new float[3];
    private float geomag[] = new float[3];
    private float gravity[] = new float[3];

    private Location mylocation;

    private float screenScaleX;
    private float screenScaleY;

    private float widthDegrees;
    private float heightDegrees;

    private List<AROverlay> overlays = new ArrayList<AROverlay>();


    public ARView(Context context) {
        super(context);
        init(context);
    }

    public ARView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context;

        setWillNotDraw(false);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        previewHolder = getHolder();
        previewHolder.addCallback(surfaceCallback);
    }

    public void onStart() {
        startGPS();

        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME );

        camera = Camera.open();
        startPreview();
    }

    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
            mSensorManager.unregisterListener(this);
        }
        locationManager.removeUpdates(this);
        camera.release();
        camera = null;
        inPreview = false;
    }


    @Override
    protected void onDraw(Canvas canvas){

        screenScaleX = (canvas.getWidth() / widthDegrees); // pixel to degree
        screenScaleY = (canvas.getHeight() / heightDegrees);

        if(camera != null){
            Camera.Parameters parameters = camera.getParameters();
            widthDegrees = parameters.getHorizontalViewAngle();
            heightDegrees = parameters.getVerticalViewAngle();
        }

        if(mylocation == null || forground == null || !sensorStarted){
            return;
        }

            // --- Get sensor variables
        float compassBearing = getCompassBearing() + 90f; // Landscape only
        if(compassBearing > 180f){
             compassBearing = compassBearing - 360f;
        }
        float yTilt = gravity[2] * 9f;

            // --- Overlay Items
            //float[] X = loadXPoints();
            //float[] Y = loadYPoints();

            // --- Compass
        if(show_compass){
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            canvas.drawText(Float.toString(compassBearing), canvas.getWidth()/2, canvas.getHeight()/2, paint);
                /*
                canvas.drawBitmap(compass, 10, 10, defaultPaint);

                canvas.save();
                canvas.rotate(compassBearing, 10 + compass.getWidth()/2, 10 + compass.getWidth()/2);
                canvas.drawBitmap(bitmap, 10, 10, defaultPaint);
                canvas.restore();*/
        }
            // -----

            // Add overlay items

            for (int i = 0; i < overlays.size(); i ++){

               double Xangle = ModSym(compassBearing - mylocation.bearingTo(overlays.get(i).getLocation()));
               double Yangle = yTilt - 0;

               if((Math.abs(Xangle) < widthDegrees/2) && (Math.abs(Yangle) < heightDegrees/2)){

                        float drawLocationX = (float) ((canvas.getWidth()/2) - (Xangle * screenScaleX));
                        float drawLocationY = (float) ((canvas.getHeight()/2) - (Yangle * screenScaleY));

                         overlays.get(i).draw((int)drawLocationX, (int)drawLocationY, forground);
               }else{
                         overlays.get(i).close(forground);
               }
        }
    }


    public float getCompassBearing(){
        rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomag);
        orientation = new float[3];

        SensorManager.getOrientation(rotationMatrix, orientation);
        return (float) (orientation[0]*360/(2*Math.PI));
    }

    private double ModSym(float angle){

        angle = (float) (angle - (360 * Math.floor(angle/360)));
        if (angle > 180){
            angle = angle - 360;
        }
        return angle;
    }

    // Location listener

    // ---- Location Code


    public boolean startGPS(){

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean GPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(GPSenabled){
            String provider = LocationManager.GPS_PROVIDER;
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }else{
            Log.d("AR", "GPS no enabled");
        }
        return GPSenabled;

    }


    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    // Sensor data

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();

        //Smoothing the sensor data a bit

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomag[0] = (geomag[0] * 0.9f) + (0.1f * sensorEvent.values[0]);
            geomag[1] = (geomag[1] * 0.9f) + (0.1f * sensorEvent.values[1]);
            geomag[2] = (geomag[2] * 0.9f) + (0.1f * sensorEvent.values[2]);
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = (gravity[0] * 0.9f) + (0.1f * sensorEvent.values[0]);
            gravity[1] = (gravity[1] * 0.9f) + (0.1f * sensorEvent.values[1]);
            gravity[2] = (gravity[2] * 0.9f) + (0.1f * sensorEvent.values[2]);
        }

        sensorStarted = true;
        // Re-draw
        invalidate();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Camera code

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);

            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera!=null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };


    // Gettings + Setters

    public void setForground(RelativeLayout forground){
        this.forground = forground;
    }

    public void setComapss(boolean on){
        show_compass = on;
    }

    public boolean showingCompass(){
        return show_compass;
    }

    public void addOverlay(AROverlay overlay){
        overlays.add(overlay);
    }

}
