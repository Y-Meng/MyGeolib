package com.mcy.particles;

import android.graphics.Color;
import android.opengl.GLES20;

import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.ParticleShaderProgram;
import com.mcy.glprogram.ShaderProgram;
import com.mcy.model.Model;
import com.mcy.util.Constants;
import com.mcy.util.VertexArray;

import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by gis on 2015/11/16.
 */
public class ParticleSystem extends Model{
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT =1;

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT
            +VECTOR_COMPONENT_COUNT+PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT* Constants.BYTE_PER_FLOAT;

    private final float[] particles;
    private final int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;
    public ParticleSystem(int maxParticleCount){
        this.maxParticleCount = maxParticleCount;
        particles = new float[maxParticleCount*TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
    }

    public void addParticle(Point position,int color,Vector direction,float starttime){
        final int particleOffset = nextParticle*TOTAL_COMPONENT_COUNT;
        int currentOffset = particleOffset;

        //当前离子数增加
        nextParticle++;
        if(currentParticleCount<maxParticleCount){
            currentParticleCount++;
        }
        //是否达到最大粒子数
        if(nextParticle == maxParticleCount){
            //从零开始，以便回收粒子
            nextParticle = 0;
        }
        //添加数据
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color)/255f;
        particles[currentOffset++] = Color.green(color)/255f;
        particles[currentOffset++] = Color.blue(color)/255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = starttime;
        //更新native数据
        vertexArray.updateBuffer(particles,particleOffset,TOTAL_COMPONENT_COUNT);
    }

    @Override
    public void bind(ShaderProgram program) {
        //位置属性
        int dataOffset = 0;
        vertexArray.setVertexAttributePointer(dataOffset,
                ((ParticleShaderProgram)program).getaPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        //颜色属性
        dataOffset += POSITION_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                ((ParticleShaderProgram)program).getaColorLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);

        //速度方向
        dataOffset += COLOR_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                ((ParticleShaderProgram)program).getaDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT,
                STRIDE);

        //时间属性
        dataOffset += VECTOR_COMPONENT_COUNT;
        vertexArray.setVertexAttributePointer(dataOffset,
                ((ParticleShaderProgram) program).getaParticleStartTimeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT,
                STRIDE);

    }

    @Override
    public void draw(){
        super.draw();
        glDrawArrays(GLES20.GL_POINTS,0,currentParticleCount);
    }
}
