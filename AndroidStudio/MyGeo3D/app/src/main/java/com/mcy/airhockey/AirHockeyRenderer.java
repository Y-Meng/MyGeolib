package com.mcy.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.mcy.geometry.ObjectBuilder;
import com.mcy.geometry.Plane;
import com.mcy.geometry.Ray;
import com.mcy.geometry.Sphere;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.ColorShaderProgram;
import com.mcy.glprogram.TextureShaderProgram;
import com.mcy.model.ModMallet;
import com.mcy.model.ModPuck;
import com.mcy.model.Table;
import com.mcy.mygeo3d.R;
import com.mcy.util.GeometryHelper;
import com.mcy.util.MatrixHelper;
import com.mcy.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by 海 on 2015/11/11.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer,TouchHandler{

    private final Context context;
    private final float[] projectMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private ModMallet mallet;
    private ModPuck puck;
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    private boolean isRoam = false;
    private float detR = 0f;

    private boolean malletPressed = false;
    private ObjectBuilder.Point blueMalletPosition;
    private ObjectBuilder.Point previousMalletPosition;
    private ObjectBuilder.Point puckPosition;
    private Vector puckVector;

    public AirHockeyRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.1f,0.1f,0.1f,0f);
        table = new Table();
        mallet = new ModMallet(0.08f,0.15f,256);
        puck = new ModPuck(0.06f,0.02f,256);

        blueMalletPosition = new ObjectBuilder.Point(0f,mallet.height/2f,0.4f);
        puckPosition = new ObjectBuilder.Point(0f,puck.height/2f,0f);
        puckVector = new Vector(0f,0f,0f);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //surface发生变化时更改视窗大小
        GLES20.glViewport(0, 0, width, height);
        //计算透视矩阵
        MatrixHelper.perspectiveMatrix(projectMatrix, 45, (float) width / (float) height, 1f, 0f);
        //计算视图矩阵
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
        //将视图模型和投影模型相乘
        multiplyMM(viewProjectionMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    private void positionTableInScene(){
        //将模型矩阵设置为单位矩阵，用于记录对模型的转换操作（平移，缩放，旋转）
        setIdentityM(modelMatrix, 0);
        //旋转模型（矩阵，偏移，角度，旋转轴分量）
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        //将视图投影矩阵与模型矩阵相乘计算最终矩阵
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x,float y,float z){
        setIdentityM(modelMatrix,0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        if(isRoam){
            //计算视图矩阵
            setLookAtM(viewMatrix, 0,
                    2.2f*(float)Math.sin(detR), 1.2f, 2.2f*(float)Math.cos(detR),
                    0f, 0f, 0f,
                    0f, 1f, 0f);

            //将视图和投影相乘
            multiplyMM(viewProjectionMatrix, 0, projectMatrix, 0, viewMatrix, 0);
            detR += 0.005f;
            if(detR>Math.PI){
                detR = 0f;
            }
        }

        //Draw the table
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bind(textureShaderProgram);
        table.draw();

        //Draw the mallets
        positionObjectInScene(0f,mallet.height/2f,-0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,1f,0f,0f,1f);
        mallet.bind(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x,blueMalletPosition.y,blueMalletPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,0f,0f,1f,1f);
        mallet.draw();

        //Draw the puck
        positionObjectInScene(puckPosition.x,puckPosition.y,puckPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,0.8f,0.8f,0.8f,1f);
        puck.bind(colorShaderProgram);
        puck.draw();
    }

    public void enableRoam(boolean isEnable){
        isRoam = isEnable;
    }

    @Override
    public void handleTouchPress(float normalX, float normalY) {
        Ray ray = Ray.convertNormalized2DPointToRay(viewProjectionMatrix,normalX,normalY);
        Sphere malletBoundingSphere = new Sphere(
                new ObjectBuilder.Point(
                        blueMalletPosition.x,
                        blueMalletPosition.y,
                        blueMalletPosition.z),
                mallet.height/2f);
        //相交测试
        malletPressed = GeometryHelper.intersects(malletBoundingSphere, ray);
        Log.w("Touch","On touch press intersection"+malletPressed);
    }

    @Override
    public void handleTouchDrag(float normalX, float normalY) {
        if(malletPressed){
            previousMalletPosition = blueMalletPosition;
            Ray ray = Ray.convertNormalized2DPointToRay(viewProjectionMatrix,normalX,normalY);
            Plane plane = new Plane(
                    new ObjectBuilder.Point(0,0,0),
                    new Vector(0,1,0));
            ObjectBuilder.Point touchedPoint = GeometryHelper.intersectionPoint(ray, plane);
            //增加碰撞检测
            blueMalletPosition = new ObjectBuilder.Point(
                    GeometryHelper.clamp(touchedPoint.x,leftBound+mallet.radius,rightBound-mallet.radius),
                    mallet.height/2f,
                    GeometryHelper.clamp(touchedPoint.z,0f+mallet.radius,nearBound-mallet.radius));
            //撞锤和球的距离，判断是否碰撞
            float distance = GeometryHelper.vectorBetween(blueMalletPosition,puckPosition).length();
            if(distance<(puck.radius+mallet.radius)){
                puckVector = GeometryHelper.vectorBetween(previousMalletPosition,blueMalletPosition);
            }
            //获得速度后球的位置
            puckPosition = puckPosition.translate(puckVector);
            //给球增加碰撞，防止飞出桌面（反弹速度改变）
            if(puckPosition.x<leftBound+puck.radius||puckPosition.x>rightBound-puck.radius){
                puckVector = new Vector(-puckVector.x,puckVector.y,puckVector.z);
            }
            if(puckPosition.z<farBound+puck.radius||puckPosition.z>nearBound-puck.radius){
                puckVector = new Vector(puckVector.x,puckVector.y,-puckVector.z);
            }
            puckPosition = new ObjectBuilder.Point(
                    GeometryHelper.clamp(puckPosition.x,leftBound+puck.radius,rightBound-puck.radius),
                    puckPosition.y,
                    GeometryHelper.clamp(puckPosition.z,farBound+puck.radius,nearBound-puck.radius)
            );
            //增加摩擦力
            puckVector = puckVector.scale(0.99f);
            puckVector = puckVector.scale(0.9f);
            Log.w("Touch","On touch drag");
        }
    }
}
