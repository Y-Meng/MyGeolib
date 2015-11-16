package com.mcy.glprogram;

import android.content.Context;

import com.mcy.mygeo3d.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by 海 on 2015/11/16.
 */
public class ParticleShaderProgram extends ShaderProgram {

    //uniform names
    private static final String U_MATRIX = "u_Matrix";
    private static final String U_TIME = "u_Time";

    //attribute names
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    private static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    //uniform and attribute location
    private final int uMatrixLocation;
    private final int uTimeLocation;

    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);
        //获取参数位置
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uTimeLocation = glGetUniformLocation(program,U_TIME);

        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        aColorLocation = glGetAttribLocation(program,A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program,A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program,A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix,float time){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform1f(uTimeLocation,time);
    }

    public int getaPositionLocation(){
        return aPositionLocation;
    }

    public int getaColorLocation(){
        return aColorLocation;
    }

    public int getaDirectionVectorLocation(){
        return aDirectionVectorLocation;
    }

    public int getaParticleStartTimeLocation(){
        return aParticleStartTimeLocation;
    }
}
