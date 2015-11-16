package com.mcy.util;
import com.mcy.geometry.ObjectBuilder;
import com.mcy.geometry.Plane;
import com.mcy.geometry.Ray;
import com.mcy.geometry.Sphere;
import com.mcy.geometry.Vector;

/**
 * Created by 海 on 2015/11/13.
 */
public class GeometryHelper {
    /**
     * 香蕉测试
     * @param boundingSphere
     * @param ray
     * @return
     */
    public static boolean intersects(Sphere boundingSphere,Ray ray){

        return distanceBetween(boundingSphere.center,ray)<boundingSphere.radius;
    }

    /**
     * 碰撞检测
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float value,float min,float max){
        return Math.min(max,Math.max(value,min));
    }

    private static float distanceBetween(ObjectBuilder.Point point, Ray ray) {

        Vector p1ToPoint = vectorBetween(ray.point,point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector),point);

        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lenghtOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTwo/lenghtOfBase;
        return distanceFromPointToRay;
    }

    public static Vector vectorBetween(ObjectBuilder.Point from,ObjectBuilder.Point to){
        return new Vector(to.x-from.x,to.y-from.y,to.z-from.z);
    }

    public static ObjectBuilder.Point intersectionPoint(Ray ray, Plane plane) {

        Vector rayToPlaneVector = vectorBetween(ray.point,plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                /ray.vector.dotProduct(plane.normal);
        ObjectBuilder.Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }
}
