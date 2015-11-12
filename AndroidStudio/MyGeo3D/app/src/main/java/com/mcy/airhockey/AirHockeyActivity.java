package com.mcy.airhockey;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class AirHockeyActivity extends AppCompatActivity {

    private GLSurfaceView mSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_air_hockey);
        mSurfaceView = new GLSurfaceView(this);
        setContentView(mSurfaceView);

        //check is system support gles 2.0
        final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final boolean isSupportES2 = am.getDeviceConfigurationInfo().reqGlEsVersion>=0x20000;

        if(isSupportES2){
            //request a es2.0 compatible context
            mSurfaceView.setEGLContextClientVersion(2);

            AirHockeyRenderer mRenderer = new AirHockeyRenderer(this);
            mSurfaceView.setRenderer(mRenderer);
            renderSet = true;
        }else{
            Toast.makeText(this,"system do not support opengles 2.0",Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(renderSet)
            mSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(renderSet)
            mSurfaceView.onResume();
    }
}
