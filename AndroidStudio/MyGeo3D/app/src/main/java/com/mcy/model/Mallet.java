package com.mcy.model;

import com.mcy.glprogram.ColorShaderProgram;
import com.mcy.util.Constants;
import com.mcy.util.VertexArray;

import static android.opengl.GLES20.*;

/**
 * Created by 海 on 2015/11/11.
 */
@Deprecated
public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)
            * Constants.BYTE_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
            0f,-0.4f,0f,0f,1f,
            0f,0.4f,1f,0f,0f
    };

    private VertexArray vertexArray;
    public Mallet(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bind(ColorShaderProgram program){
        vertexArray.setVertexAttributePointer(
                0,
                program.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawArrays(GL_POINTS,0,2);
    }
}
