package com.mcy.glprogram;

import android.content.Context;

import com.mcy.util.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by 海 on 2015/11/11.
 */
public class ShaderProgram {

    //uniform constants
    protected static final String U_MATRIX = "u_Matrix";

    //attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";



    //shader program
    protected final int program;
    protected ShaderProgram(Context context,int vsResID,int fsResID){
        program = ShaderHelper.buildProgram(
                ShaderHelper.readTextShaderFromResource(context,vsResID),
                ShaderHelper.readTextShaderFromResource(context,fsResID));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
