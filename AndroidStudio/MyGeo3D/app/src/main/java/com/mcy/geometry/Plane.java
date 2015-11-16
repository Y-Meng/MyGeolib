package com.mcy.geometry;

/**
 * Created by 海 on 2015/11/13.
 */
public class Plane {
    public final ObjectBuilder.Point point;
    public final Vector normal;
    public Plane(ObjectBuilder.Point point,Vector normal){
        this.point = point;
        this.normal = normal;
    }
}
