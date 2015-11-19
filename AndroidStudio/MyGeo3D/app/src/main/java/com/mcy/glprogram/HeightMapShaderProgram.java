package com.mcy.glprogram;

import android.content.Context;

import com.mcy.geometry.Vector;
import com.mcy.mygeo3d.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by gis on 2015/11/18.
 */
public class HeightMapShaderProgram extends ShaderProgram{

    private final static String U_MATRIX = "u_Matrix";
    private final static String U_VECTOR_TO_LIGHT = "u_VectorToLight";
    private final static String A_POSITION = "a_Position";
    private final static String A_NORMAL = "a_Normal";

    private int uMatrixLocation;
    private int uVectorToLightLocation;
    private int aPositionLocation;
    private int aNormalLocation;

    public HeightMapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);
    }

    @Override
    protected void initUniformsLocation() {
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uVectorToLightLocation = glGetUniformLocation(program,U_VECTOR_TO_LIGHT);
    }

    @Override
    protected void initAttributesLocation() {
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getNormalLocation() {
        return aNormalLocation;
    }

    public void setUniforms(float[] matrix,Vector vectorToLight) {
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform3f(uVectorToLightLocation,vectorToLight.x,vectorToLight.y,vectorToLight.z);
    }


}
