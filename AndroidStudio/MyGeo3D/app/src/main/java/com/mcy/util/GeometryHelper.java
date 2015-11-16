package com.mcy.util;

import com.mcy.geometry.Plane;
import com.mcy.geometry.Point;
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

    private static float distanceBetween(Point point, Ray ray) {

        Vector p1ToPoint = vectorBetween(ray.point,point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector),point);

        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTwo/lengthOfBase;
        return distanceFromPointToRay;
    }

    /**
     * 计算两点之间向量
     * @param from
     * @param to
     * @return
     */
    public static Vector vectorBetween(Point from,Point to){
        return new Vector(to.x-from.x,to.y-from.y,to.z-from.z);
    }

    /**
     * 计算射线与平面相交点
     * @param ray
     * @param plane
     * @return
     */
    public static Point intersectionPoint(Ray ray, Plane plane) {

        Vector rayToPlaneVector = vectorBetween(ray.point,plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                /ray.vector.dotProduct(plane.normal);
        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }

    /**
     * 计算X轴归一化坐标
     * @param x
     * @param width
     * @return
     */
    public static float normalizeScreenX(float x,float width){
        return 2*x/width - 1f;
    }

    /**
     * 计算X轴归一化坐标
     * @param y
     * @param height
     * @return
     */
    public static float normalizeScreenY(float y,float height){
        return 1f - 2*y/height;
    }
}
