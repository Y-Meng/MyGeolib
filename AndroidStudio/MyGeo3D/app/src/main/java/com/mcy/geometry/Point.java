package com.mcy.geometry;

/**
 * Created by 海 on 2015/11/16.
 */
public class Point {
    public final float x,y,z;
    public Point(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point translateY(float distance){
        return new Point(x,y+distance,z);
    }
    public Point translate(Vector v){
        return new Point(
                x+v.x,
                y+v.y,
                z+v.z
        );
    }
}
