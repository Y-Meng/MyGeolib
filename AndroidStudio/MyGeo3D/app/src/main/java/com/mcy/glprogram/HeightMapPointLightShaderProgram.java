package com.mcy.glprogram;

import android.content.Context;

import com.mcy.mygeo3d.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by gis on 2015/11/18.
 */
public class HeightMapPointLightShaderProgram extends ShaderProgram{

    private final static String U_MV_ATRIX = "u_MVMatrix";
    private final static String U_IT_MATRIX = "u_IT_MVMatrix";
    private final static String U_MVP_MATRIX = "u_MVPMatrix";

    private final static String U_POINT_LIGHT_POSITIONS="u_PointLightPositions";
    private final static String U_POINT_LIGHT_COLORS="u_PointLightColors";
    private final static String U_VECTOR_TO_LIGHT = "u_VectorToLight";

    private final static String A_POSITION = "a_Position";
    private final static String A_NORMAL = "a_Normal";

    private int uMVMatrixLocation;
    private int uITMatrixLocation;
    private int uMVPMatrixLocation;
    private int uPointLightPositionsLocation;
    private int uPointLightColorsLocation;
    private int uVectorToLightLocation;

    private int aPositionLocation;
    private int aNormalLocation;

    public HeightMapPointLightShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_point_light_shader, R.raw.heightmap_fragment_shader);
    }

    @Override
    protected void initUniformsLocation() {
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_ATRIX);
        uITMatrixLocation = glGetUniformLocation(program,U_IT_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program,U_MVP_MATRIX);

        uPointLightPositionsLocation = glGetUniformLocation(program,U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation = glGetUniformLocation(program,U_POINT_LIGHT_COLORS);
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

    public void setUniforms(float[] mvMatrix,
                            float[] itMvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectLight,
                            float[] pointLightPositions,
                            float[] pointLightColors) {
        glUniformMatrix4fv(uMVMatrixLocation,1,false,mvMatrix,0);
        glUniformMatrix4fv(uITMatrixLocation,1,false,itMvMatrix,0);
        glUniformMatrix4fv(uMVPMatrixLocation,1,false,mvpMatrix,0);

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectLight, 0);
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation,3,pointLightColors,0);
    }


}
