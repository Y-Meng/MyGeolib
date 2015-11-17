package com.mcy.particles;

import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

/**
 * Created by 海 on 2015/11/16.
 */
public class ParticleShooter {

    //起点、方向、颜色
    private final Point position;
    private final Vector direction;
    private int color;

    //角度与速度
    private final float angleVariance;
    private final float speedVariance;

    private final Random random;

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    public ParticleShooter(Point position,Vector direction,int color,
                           float angleVariance,float speedVariance){
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.angleVariance = angleVariance;
        this.speedVariance = speedVariance;
        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
        random = new Random();
    }

    public void addParticle(ParticleSystem particleSystem,float currentTime,int count){
        for(int i = 0;i<count;i++){
            particleSystem.addParticle(position, color, direction, currentTime);
            //应用角度和速度变化量
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);

            multiplyMV(resultVector,0,rotationMatrix,0,directionVector,0);

            float speedAdjustment = 1f+random.nextFloat()*speedVariance;

            Vector thisDirection = new Vector(
                    resultVector[0]*speedAdjustment,
                    resultVector[1]*speedAdjustment,
                    resultVector[2]*speedAdjustment
            );

            particleSystem.addParticle(position,color,thisDirection,currentTime);
        }
    }
}
