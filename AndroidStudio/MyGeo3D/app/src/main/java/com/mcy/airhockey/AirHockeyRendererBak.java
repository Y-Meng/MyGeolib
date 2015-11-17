package com.mcy.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.mcy.mygeo3d.R;
import com.mcy.util.LoggerConfig;
import com.mcy.util.MatrixHelper;
import com.mcy.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRendererBak implements GLSurfaceView.Renderer {

    //顶点位置元组位数
    private static final int POSITION_COMP_COUNT = 3;
    //顶点颜色元组位数
    private static final int COLOR_COMP_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final int STRIDE = (POSITION_COMP_COUNT+COLOR_COMP_COUNT)*BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;
    private Context mContext;
    int glProgram;

    //attribute 标志和位置
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    //uniform
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    //正交投影
    //private final float[] projectMtrix = new float[16];

    //透视矩阵
    float[] perspectiveMatrix = new float[16];
    //模型转换矩阵
    float[] modelMatrix = new float[16];

    public AirHockeyRendererBak(Context context){
        mContext = context;

        float[] tableTriangleVertices = {
                //order of coordinates :X Y R G B
                //triangle fan 6个点可以表示四个三角形
                   0f,   0f, 0f,  1f,  1f,  1f,
                -0.5f,-0.8f, 0f,0.7f,0.7f,0.7f,
                 0.5f,-0.8f, 0f,0.7f,0.7f,0.7f,
                 0.5f, 0.8f, 0f,0.7f,0.7f,0.7f,
                -0.5f, 0.8f, 0f,0.7f,0.7f,0.7f,
                -0.5f,-0.8f, 0f,0.7f,0.7f,0.7f,

                //line
                -0.5f,  0f, 0f, 1f,  0f,  0f,
                 0.5f,  0f, 0f, 0f,  0f,  1f,
                //mallets
                  0f,-0.4f, 0f, 0f, 0f,  1f,
                  0f, 0.4f, 0f, 1f, 0f,  0f
        };

        //copy data to native environment
        vertexData = ByteBuffer.allocateDirect(tableTriangleVertices.length*BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableTriangleVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清空内容后显示的颜色（黑色）
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
        //读取shader代码
        String vertexShaderCode = ShaderHelper
                .readTextShaderFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderCode = ShaderHelper
                .readTextShaderFromResource(mContext, R.raw.simple_fragment_shader);
        //编译shader
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderCode);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderCode);
        glProgram = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        //验证程序是否可用
        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(glProgram);
        }
        //使用程序
        GLES20.glUseProgram(glProgram);

        //获取attribute的位置
        aPositionLocation = GLES20.glGetAttribLocation(glProgram,A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(glProgram,A_COLOR);
        //获取uniform位置
        uMatrixLocation = GLES20.glGetUniformLocation(glProgram,U_MATRIX);

        //关联属性与顶点数据的数组(position&color)
        vertexData.position(0);//将读取位置设置到开头处
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMP_COUNT, GLES20.GL_FLOAT
                , false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMP_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMP_COUNT, GLES20.GL_FLOAT
                , false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //surface发生变化时更改视窗大小
        GLES20.glViewport(0, 0, width, height);
        //计算透视矩阵
        MatrixHelper.perspectiveMatrix(perspectiveMatrix,45,(float)width/(float)height,1f,0f);
        //将模型矩阵设置为单位矩阵，用于记录对模型的转换操作（平移，缩放，旋转）
        Matrix.setIdentityM(modelMatrix, 0);
        //平移模型矩阵 z-2.5
        Matrix.translateM(modelMatrix,0,0f,0f,-2.8f);
        //旋转模型（矩阵，偏移，角度，旋转轴分量）
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        //将透视和模型转换矩阵合并,并保存到投影矩阵
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, perspectiveMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp,0,perspectiveMatrix,0,temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //绘制每一帧调用
        // 1.清空前一帧内容
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 1.1设置uniform正交投影矩阵
        //GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,projectMtrix,0);
        // 1.1设置uniform透视投影矩阵
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,perspectiveMatrix,0);

        // 2.绘制三角形扇
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0,6);

        // 3.绘制中间直线
        GLES20.glDrawArrays(GLES20.GL_LINES,6,2);
        // 4.绘制两个撞锤
        GLES20.glDrawArrays(GLES20.GL_POINTS,8,1);
        GLES20.glDrawArrays(GLES20.GL_POINTS,9,1);
    }
}
