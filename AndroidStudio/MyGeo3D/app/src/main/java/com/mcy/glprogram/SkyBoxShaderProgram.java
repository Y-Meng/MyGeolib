package com.mcy.glprogram;

import android.content.Context;
import com.mcy.mygeo3d.R;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by gis on 2015/11/17.
 */
public class SkyBoxShaderProgram extends ShaderProgram{

    private final static String U_MATRIX = "u_Matrix";
    private final static String U_TEXTURE_UNIT = "u_TextureUnit";
    private final static String A_POSITION = "a_Position";

    private int uMatrixLocation;
    private int uTextureUnitLocation;
    private int aPositionLocation;

    public SkyBoxShaderProgram(Context context) {
        super(context,R.raw.skybox_vertex_shader,R.raw.skybox_fragment_shader);
    }

    @Override
    protected void initUniformsLocation() {
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program,U_TEXTURE_UNIT);
    }

    @Override
    protected void initAttributesLocation() {
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix,int textureId){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation,0);
    }

    public int getPositionLocation(){
        return aPositionLocation;
    }
}
