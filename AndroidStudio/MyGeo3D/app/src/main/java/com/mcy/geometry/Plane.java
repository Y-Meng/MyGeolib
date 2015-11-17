package com.mcy.geometry;

/**
 * Created by 海 on 2015/11/13.
 */
public class Plane {
    public final Point point;
    public final Vector normal;
    public Plane(Point point,Vector normal){
        this.point = point;
        this.normal = normal;
    }
}
