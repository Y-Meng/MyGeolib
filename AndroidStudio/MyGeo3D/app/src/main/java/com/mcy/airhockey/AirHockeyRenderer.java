package com.mcy.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.mcy.airhockey.Model.Mallet;
import com.mcy.airhockey.Model.Table;
import com.mcy.glprogram.ColorShaderProgram;
import com.mcy.glprogram.TextureShaderProgram;
import com.mcy.mygeo3d.R;
import com.mcy.util.MatrixHelper;
import com.mcy.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

/**
 * Created by 海 on 2015/11/11.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] projectMatrix = new float[16];
    private final float[] modelMatrix = new float[16];


    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1f,1f,1f,0f);
        table = new Table();
        mallet = new Mallet();

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
        //将模型矩阵设置为单位矩阵，用于记录对模型的转换操作（平移，缩放，旋转）
        Matrix.setIdentityM(modelMatrix, 0);
        //平移模型矩阵 z-2.5
        Matrix.translateM(modelMatrix,0,0f,0f,-2.8f);
        //旋转模型（矩阵，偏移，角度，旋转轴分量）
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        //将透视和模型转换矩阵合并,并保存到投影矩阵
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0,projectMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp,0,projectMatrix,0,temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        //Draw the table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectMatrix, texture);
        table.bind(textureShaderProgram);
        table.draw();

        //Draw the mallets
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectMatrix);
        mallet.bind(colorShaderProgram);
        mallet.draw();
    }
}
