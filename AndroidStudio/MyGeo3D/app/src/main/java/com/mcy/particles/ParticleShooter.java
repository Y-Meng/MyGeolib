package com.mcy.particles;

import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;

/**
 * Created by 海 on 2015/11/16.
 */
public class ParticleShooter {

    private final Point position;
    private final Vector direction;
    private int color;
    public ParticleShooter(Point position,Vector direction,int color){
        this.position = position;
        this.direction = direction;
        this.color = color;
    }

    public void addParticle(ParticleSystem particleSystem,float currentTime,int count){
        for(int i = 0;i<count;i++){
            particleSystem.addParticle(position,color,direction,currentTime);
        }
    }
}
