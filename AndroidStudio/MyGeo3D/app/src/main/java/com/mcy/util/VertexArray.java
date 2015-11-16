package com.mcy.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by 海 on 2015/11/11.
 */
public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData){
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * Constants.BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttributePointer(int dataOffset,int attLocation,int comCount,int stride){

        //指向Attribute起始位置
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attLocation, comCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attLocation);
        //指回 0 起始位置
        floatBuffer.position(0);
    }

    /**
     * 更新native数据
     * @param vertexData 输入数据
     * @param start      输入起点
     * @param count      总步长
     */
    public void updateBuffer(float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData,start,count);
        floatBuffer.position(0);
    }
}
