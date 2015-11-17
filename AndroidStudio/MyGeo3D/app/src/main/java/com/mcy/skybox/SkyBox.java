package com.mcy.skybox;

import com.mcy.glprogram.ShaderProgram;
import com.mcy.glprogram.SkyBoxShaderProgram;
import com.mcy.model.Model;
import com.mcy.util.Constants;
import com.mcy.util.VertexArray;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by gis on 2015/11/17.
 */
public class SkyBox extends Model{

    private final ByteBuffer indexArray;

    //t:top b:bottom l:left r:right n:near f:far
    public SkyBox(){
        vertexArray = new VertexArray(new float[]{
               -1.0f, 1.0f, 1.0f,//(0) t l n
                1.0f, 1.0f, 1.0f,//(1) t r n
               -1.0f,-1.0f, 1.0f,//(2) b l n
                1.0f,-1.0f, 1.0f,//(3) b r n

               -1.0f, 1.0f,-1.0f,//(4) t l f
                1.0f, 1.0f,-1.0f,//(5) t r f
               -1.0f,-1.0f,-1.0f,//(6) b l f
                1.0f,-1.0f,-1.0f //(7) b r f
        });

        indexArray = ByteBuffer.allocateDirect(6*6)
                .put(new byte[]{
                //front
                1,3,0,
                0,3,2,
                //back
                4,6,5,
                5,6,7,
                //left
                0,2,4,
                4,2,6,
                //right
                5,7,1,
                1,7,3,
                //top
                5,1,4,
                4,1,0,
                //bottom
                6,2,7,
                7,2,3
        });
        indexArray.position(0);
    }

    @Override
    public void bind(ShaderProgram program) {
        vertexArray.setVertexAttributePointer(
                0, ((SkyBoxShaderProgram) program).getPositionLocation(),
                Constants.POSITION_3D, 0);
    }

    @Override
    public void draw(){
        super.draw();
        glDrawElements(GL_TRIANGLES,36,GL_UNSIGNED_BYTE,indexArray);
    }
}
