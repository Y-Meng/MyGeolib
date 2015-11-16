package com.mcy.airhockey;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mcy.util.GeometryHelper;

public class AirHockeyActivity extends AppCompatActivity {

    private GLSurfaceView mSurfaceView;
    private boolean renderSet = false;
    private boolean renderRoam = true;

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

            final AirHockeyRenderer mRenderer = new AirHockeyRenderer(this);
            mSurfaceView.setRenderer(mRenderer);
            renderSet = true;

//            mSurfaceView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mRenderer.enableRoam(!renderRoam);
//                    renderRoam = !renderRoam;
//                }
//            });

            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event!=null){
                        //从屏幕坐标转换到设备归一化坐标，y轴反转
                        final float normalX = GeometryHelper.normalizeScreenX(event.getX(),v.getWidth());
                        final float normalY = GeometryHelper.normalizeScreenY(event.getY(),v.getHeight());
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                //Android的UI运行在主线程，而GLSurfaceView运行在单独线程
                                mSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRenderer.handleTouchPress(normalX,normalY);
                                    }
                                });
                                break;

                            case MotionEvent.ACTION_MOVE:
                                mSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRenderer.handleTouchDrag(normalX,normalY);
                                    }
                                });
                                break;

                            default:
                                break;
                        }
                    }
                    return true;
                }
            });
        }else{
            Toast.makeText(this,"system do not support openGLES 2.0",Toast.LENGTH_LONG)
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
