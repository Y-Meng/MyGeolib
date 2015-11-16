package com.mcy.glprogram;

import android.content.Context;

import com.mcy.mygeo3d.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by 海 on 2015/11/11.
 */
public class ColorShaderProgram extends ShaderProgram {

    //uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";

    //attribute constants
    protected static final String A_POSITION = "a_Position";


    private final int uMatrixLocation;
    private final int uColorLocation;
    private final int aPositionLocation;

    public ColorShaderProgram(Context context){
        super(context, R.raw.simple_vertex_shader,R.raw.simple_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        aPositionLocation = glGetAttribLocation(program,A_POSITION);
    }

    public void setUniforms(float[] matrix,float r,float g,float b,float a){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform4f(uColorLocation,r,g,b,a);
    }

    public int getPositionLocation(){
        return aPositionLocation;
    }

}
