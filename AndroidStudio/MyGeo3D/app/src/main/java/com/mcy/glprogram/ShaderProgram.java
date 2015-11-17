package com.mcy.glprogram;

import android.content.Context;

import com.mcy.util.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by gis on 2015/11/11.
 */
public abstract class ShaderProgram {
    //shader program
    protected final int program;

    protected ShaderProgram(Context context,int vsResID,int fsResID){
        program = ShaderHelper.buildProgram(
                ShaderHelper.readTextShaderFromResource(context,vsResID),
                ShaderHelper.readTextShaderFromResource(context,fsResID));
        initUniformsLocation();
        initAttributesLocation();
    }

    public void useProgram(){
        glUseProgram(program);
    }

    protected abstract void initUniformsLocation();
    protected abstract void initAttributesLocation();
}
