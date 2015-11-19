package com.mcy.particles;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

public class ParticlesActivity extends AppCompatActivity {

    private GLSurfaceView mSurfaceView;
    private ParticleRender mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_particles);
//        mSurfaceView = (GLSurfaceView)findViewById(R.id.glView);

        mSurfaceView = new GLSurfaceView(this);
        setContentView(mSurfaceView);

        //check is system support gles 2.0
        final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final boolean isSupportES2 = am.getDeviceConfigurationInfo().reqGlEsVersion>=0x20000;

        if(isSupportES2){
            //request a es2.0 compatible context
            mSurfaceView.setEGLContextClientVersion(2);
            mRender = new ParticleRender(this);
            mSurfaceView.setRenderer(mRender);
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                float preX,preY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event!=null){
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                preX = event.getX();
                                preY = event.getY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                final float detX = event.getX()-preX;
                                final float detY = event.getY()-preY;
                                preX = event.getX();
                                preY = event.getY();

                                mSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRender.handleTouchDrag(detX,detY);
                                    }
                                });
                                break;
                        }
                        return  true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSurfaceView.onResume();
    }
}
