package com.mcy.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by gis on 2015/11/18.
 */
public class VertexBuffer {
    private final int bufferId;
    public VertexBuffer(float[] vertexData){
        //分配buffer
        final int[] buffers = new int[1];
        glGenBuffers(buffers.length,buffers,0);
        if(buffers[0]==0){
            throw new RuntimeException("Couldn't create a new vertex buffer object");
        }
        bufferId = buffers[0];
        //绑定buffer
        glBindBuffer(GL_ARRAY_BUFFER,buffers[0]);
        //转换数据到 native Memory
        FloatBuffer vertexArray = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexArray.position(0);
        //转换数据到 GPU Memory
        glBufferData(GL_ARRAY_BUFFER,vertexArray.capacity()* Constants.BYTE_PER_FLOAT,
                vertexArray,GL_STATIC_DRAW);
        //解绑buffer(目标指针置为0)
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    public void setVertexAttributePointer(int dataOffset,int attributeLocation,
                                          int componentCount,int stride){
        //先绑定
        glBindBuffer(GL_ARRAY_BUFFER,bufferId);
        //设置并启用
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        //解绑
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }
}
