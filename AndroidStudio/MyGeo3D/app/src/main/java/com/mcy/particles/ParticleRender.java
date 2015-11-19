package com.mcy.particles;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView.Renderer;

import com.mcy.airhockey.TouchHandler;
import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.HeightMapPointLightShaderProgram;
import com.mcy.glprogram.ParticleShaderProgram;
import com.mcy.glprogram.SkyBoxShaderProgram;
import com.mcy.heightmap.HeightMap;
import com.mcy.mygeo3d.R;
import com.mcy.skybox.SkyBox;
import com.mcy.util.MatrixHelper;
import com.mcy.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;

/**
 * Created by gis on 2015/11/16.
 */
public class ParticleRender implements Renderer,TouchHandler {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkyBox = new float[16];

    private final float[] modelMatrix = new float[16];

    private final float[] modelViewMatrix = new float[16];
    private final float[] itModelViewMatrix = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];



    private final float angleVarianceInDegree = 5f;
    private final float speedVariance = 1f;

    //地形shader,model
    //private HeightMapShaderProgram heightMapShaderProgram;

    private HeightMapPointLightShaderProgram heightMapPointLightShaderProgram;
    private HeightMap heightMap;

    //天空盒shader,model,texture
    private SkyBoxShaderProgram skyboxShaderProgram;
    private SkyBox skyBox;
    private int skyBoxTexture;

    //粒子系统shader,model,texture,lifetime...
    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int texture;

    //直线光源
    private final Vector vectorToSunLight = new Vector(0.61f,0.64f,-0.47f).normalize();
    private final Vector vectorToMoonLight = new Vector(0.30f,0.35f,-0.89f).normalize();

    private final float[] vectorToLight = {0.30f,0.35f,-0.89f,0f};
    private final float[] pointLightPositions = new float[]{
            -1f,1f,0f,1f,
            0f, 1f,0f,1f,
            1f, 1f,0f,1f
    };
    private final float[] pointLightColors = new float[]{
            1.00f,0.20f,0.02f,
            0.02f,0.25f,0.02f,
            0.02f,0.20f,1.00f
    };

    //旋转变量
    private float xRotation,yRotation;

    //时间变量
    private boolean onDay = false;

    public ParticleRender(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //清理后背景颜色
        glClearColor(0f, 0f, 0f, 0f);
        //启用深度缓冲区测试
        glEnable(GL_DEPTH_TEST);
        //关闭两面绘制（使用剔除）
        glEnable(GL_CULL_FACE);

        //粒子系统
        particleShaderProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final Vector particleDirection = new Vector(0f,0.5f,0f);

        redParticleShooter = new ParticleShooter(
                new Point(-1f,0f,0f),
                particleDirection,
                Color.rgb(255,50,5),
                angleVarianceInDegree,
                speedVariance);

        greenParticleShooter = new ParticleShooter(
                new Point(0f,0f,0f),
                particleDirection,
                Color.rgb(25,255,25),
                angleVarianceInDegree,
                speedVariance);

        blueParticleShooter = new ParticleShooter(
                new Point(1f,0f,0f),
                particleDirection,
                Color.rgb(5,50,255),
                angleVarianceInDegree,
                speedVariance);

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        //天空盒
        skyboxShaderProgram = new SkyBoxShaderProgram(context);
        skyBox = new SkyBox();

        if(onDay){
            skyBoxTexture = TextureHelper.loadCubeMap(context,
                    new int[]{
                            R.drawable.left,
                            R.drawable.right,
                            R.drawable.bottom,
                            R.drawable.top,
                            R.drawable.front,
                            R.drawable.back
                    });
        }else{
            skyBoxTexture = TextureHelper.loadCubeMap(context,
                    new int[]{
                            R.drawable.night_left,
                            R.drawable.night_right,
                            R.drawable.night_bottom,
                            R.drawable.night_top,
                            R.drawable.night_front,
                            R.drawable.night_back
                    });
        }


        //地形
        //heightMapShaderProgram = new HeightMapShaderProgram(context);
        heightMapPointLightShaderProgram = new HeightMapPointLightShaderProgram(context);
        heightMap = new HeightMap(
                ((BitmapDrawable)(context.getResources().getDrawable(R.drawable.heightmap)))
                .getBitmap());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveMatrix(projectionMatrix, 45, (float) width / (float) height, 100f, 1f);
        updateViewMatrix();

//        setIdentityM(viewMatrix, 0);
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        Matrix.multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);


    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //清空帧缓存
        //清空深度缓冲区缓存
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        //draw height map
        drawHeightMap();

        //draw sky box
        drawSkyBox();

        //draw particles
        drawParticles();
    }

    private void drawHeightMap(){
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();

//        heightMapShaderProgram.useProgram();
//        if(onDay){
//            heightMapShaderProgram.setUniforms(modelViewProjectionMatrix,vectorToSunLight);
//        }else{
//            heightMapShaderProgram.setUniforms(modelViewProjectionMatrix,vectorToMoonLight);
//        }
//
//        heightMap.bind(heightMapShaderProgram);
//        heightMap.draw();



        heightMapPointLightShaderProgram.useProgram();

        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];

        multiplyMV(vectorToLightInEyeSpace, 0,viewMatrix,0,vectorToLight,0);
        multiplyMV(pointPositionsInEyeSpace,0,viewMatrix,0,pointLightPositions,0);
        multiplyMV(pointPositionsInEyeSpace,4,viewMatrix,0,pointLightPositions,4);
        multiplyMV(pointPositionsInEyeSpace,8,viewMatrix,0,pointLightPositions,8);

        heightMapPointLightShaderProgram.setUniforms(
                modelViewMatrix,
                itModelViewMatrix,
                modelViewProjectionMatrix,
                vectorToLightInEyeSpace,
                pointPositionsInEyeSpace,
                pointLightColors);
        heightMap.bind(heightMapPointLightShaderProgram);
        heightMap.draw();
    }

    private void drawSkyBox(){

        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkyBox();

        glDepthFunc(GL_LEQUAL);//修改深度测试算法

        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(modelViewProjectionMatrix, skyBoxTexture);
        skyBox.bind(skyboxShaderProgram);
        skyBox.draw();


        glDepthFunc(GL_LESS);//恢复默认
    }

    private void drawParticles(){

        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;

        redParticleShooter.addParticle(particleSystem,currentTime,1);
        greenParticleShooter.addParticle(particleSystem, currentTime, 1);
        blueParticleShooter.addParticle(particleSystem, currentTime, 1);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glDepthMask(false);

        //启用累加混合
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram.useProgram();;
        particleShaderProgram.setUniforms(modelViewProjectionMatrix, currentTime, texture);
        particleSystem.bind(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    @Override
    public void handleTouchPress(float x, float y) {

    }



    @Override
    public void handleTouchDrag(float detX, float detY) {
        xRotation += detX/16f;
        yRotation += detY/16f;

        if(yRotation<-90){
            yRotation = -90;
        }else if(yRotation>90){
            yRotation = 90;
        }
        updateViewMatrix();
    }

    /**
     * 更新相机矩阵
     */
    private void updateViewMatrix(float xrotation,float yrotation,float zrotation){
        setIdentityM(viewMatrix,0);
        //屏幕跟gl的x,y轴反转
        rotateM(viewMatrix, 0, -yrotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xrotation, 0f, 1f, 0f);
        rotateM(viewMatrix, 0, -zrotation, 0f, 0f, 1f);

        System.arraycopy(viewMatrix, 0, viewMatrixForSkyBox, 0, viewMatrix.length);

        translateM(viewMatrix,0,0,-1.5f,-5f);
    }

    private void updateViewMatrix(){
        updateViewMatrix(xRotation, yRotation, 0f);
    }
    /**
     * 更新模型视图投影矩阵
     */
    private void updateMvpMatrix(){
        multiplyMM(modelViewMatrix,0,viewMatrix,0,modelMatrix,0);

        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix,0,tempMatrix,0);

        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
    }

    private void updateMvpMatrixForSkyBox(){
        multiplyMM(tempMatrix,0,viewMatrixForSkyBox,0,modelMatrix,0);
        multiplyMM(modelViewProjectionMatrix,0,projectionMatrix,0,tempMatrix,0);
    }
}
