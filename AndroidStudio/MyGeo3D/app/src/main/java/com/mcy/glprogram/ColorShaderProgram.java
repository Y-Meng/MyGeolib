package com.mcy.glprogram;

import android.content.Context;

import com.mcy.mygeo3d.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by 海 on 2015/11/11.
 */
public class ColorShaderProgram extends ShaderProgram {

    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context){
        super(context, R.raw.simple_vertex_shader,R.raw.simple_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);

        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        aColorLocation = glGetAttribLocation(program,A_COLOR);
    }

    public void setUniforms(float[] matrix){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
    }

    public int getPositionLocation(){
        return aPositionLocation;
    }

    public int getColorLocation(){
        return aColorLocation;
    }
}
