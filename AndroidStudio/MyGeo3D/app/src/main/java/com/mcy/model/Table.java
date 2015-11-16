package com.mcy.model;

import com.mcy.glprogram.TextureShaderProgram;
import com.mcy.util.Constants;
import com.mcy.util.VertexArray;

import static android.opengl.GLES20.*;

/**
 * Created by 海 on 2015/11/11.
 */
public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            +TEXTURE_COORDINATES_COMPONENT_COUNT)* Constants.BYTE_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
            // X,Y,S,T
               0f,   0f, 0.5f,0.5f,
            -0.5f,-0.8f,   0f,0.9f,
             0.5f,-0.8f,   1f,0.9f,
             0.5f, 0.8f,   1f,0.1f,
            -0.5f, 0.8f,   0f,0.1f,
            -0.5f,-0.8f,   0f,0.9f };

    private VertexArray vertexArray;
    public Table(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bind(TextureShaderProgram textureProgram){
        vertexArray.setVertexAttributePointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
    }
}
