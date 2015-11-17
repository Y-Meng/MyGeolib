package com.mcy.particles;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.mcy.airhockey.TouchHandler;
import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.ParticleShaderProgram;
import com.mcy.glprogram.SkyBoxShaderProgram;
import com.mcy.mygeo3d.R;
import com.mcy.skybox.SkyBox;
import com.mcy.util.MatrixHelper;
import com.mcy.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by gis on 2015/11/16.
 */
public class ParticleRender implements Renderer,TouchHandler {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private final float angleVarianceInDegree = 5f;
    private final float speedVariance = 1f;

    private SkyBoxShaderProgram skyboxShaderProgram;

    private SkyBox skyBox;
    private int skyBoxTexture;

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int texture;

    private float xRotation,yRotation;

    public ParticleRender(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(0f,0f,0f,0f);

        //粒子着色器
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

        //天空盒着色器
        skyboxShaderProgram = new SkyBoxShaderProgram(context);
        skyBox = new SkyBox();
        skyBoxTexture = TextureHelper.loadCubeMap(context,
                new int[]{
                        R.drawable.left,
                        R.drawable.right,
                        R.drawable.bottom,
                        R.drawable.top,
                        R.drawable.front,
                        R.drawable.back
                });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveMatrix(projectionMatrix, 45, (float) width / (float) height, 10f, 1f);

//        setIdentityM(viewMatrix, 0);
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        Matrix.multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);


    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);

        //draw sky box
        drawSkyBox();

        //draw particles
        drawParticles();
    }

    private void drawSkyBox(){
        setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(viewProjectionMatrix,skyBoxTexture);
        skyBox.bind(skyboxShaderProgram);
        skyBox.draw();
    }

    private void drawParticles(){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;

        redParticleShooter.addParticle(particleSystem,currentTime,1);
        greenParticleShooter.addParticle(particleSystem, currentTime, 1);
        blueParticleShooter.addParticle(particleSystem, currentTime, 1);

        setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f,0f);
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //启用累加混合
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram.useProgram();;
        particleShaderProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bind(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
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
    }
}
