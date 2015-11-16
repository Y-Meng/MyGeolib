package com.mcy.geometry;

/**
 * Created by 海 on 2015/11/16.
 */
public class Cylinder {
    public final float radius;
    public final float height;
    public final Point center;
    public Cylinder(Point center,float radius,float height){
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
}
