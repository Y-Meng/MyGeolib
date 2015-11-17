package com.mcy.geometry;

/**
 * Created by 海 on 2015/11/16.
 */
public class Circle {
    public final float radius;
    public final Point center;
    public Circle(Point center,float radius){
        this.center = center;
        this.radius = radius;
    }
    public Circle scale(float scale){
        return new Circle(center,radius*scale);
    }
}
