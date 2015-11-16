package com.mcy.particles;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.ParticleShaderProgram;
import com.mcy.util.MatrixHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by 海 on 2015/11/16.
 */
public class ParticleRender implements Renderer {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;

    public ParticleRender(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(1f,1f,1f,0f);

        ParticleShaderProgram program = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final Vector particleDirection = new Vector(0f,0.5f,0f);

        redParticleShooter = new ParticleShooter(
                new Point(-1f,0f,0f),
                particleDirection,
                Color.rgb(255,50,5));

        greenParticleShooter = new ParticleShooter(
                new Point(0f,0f,0f),
                particleDirection,
                Color.rgb(25,255,25));

        blueParticleShooter = new ParticleShooter(
                new Point(1f,0f,0f),
                particleDirection,
                Color.rgb(5,50,255));

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
        MatrixHelper.perspectiveMatrix(projectionMatrix, 45, (float) width / (float) height, 10f, 1f);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);

        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;

        redParticleShooter.addParticle(particleSystem,currentTime,5);
        greenParticleShooter.addParticle(particleSystem,currentTime,5);
        blueParticleShooter.addParticle(particleSystem,currentTime,5);

        particleShaderProgram.useProgram();;
        particleShaderProgram.setUniforms(viewProjectionMatrix,currentTime);
        particleSystem.bind(particleShaderProgram);
        particleSystem.draw();
    }
}
