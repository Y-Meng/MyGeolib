package com.mcy.geometry;

import android.opengl.Matrix;

import com.mcy.util.GeometryHelper;

/**
 * Created by 海 on 2015/11/13.
 */
public class Ray {
    public final ObjectBuilder.Point point;
    public final Vector vector;
    public Ray(ObjectBuilder.Point point,Vector vector){
        this.point = point;
        this.vector = vector;
    }

    public static Ray convertNormalized2DPointToRay(float[] viewProjectMatrix,float normalX,float normalY){
        //反转视图投影矩阵
        final float[] invertedViewProjectMatrix = new float[16];
        Matrix.invertM(invertedViewProjectMatrix,0,viewProjectMatrix,0);
        //计算归一化设备二维点到三维空间射线
        final float[] nearPointNdc = {normalX,normalY,-1,1};
        final float[] farPointNdc = {normalX,normalY,1,1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld,0,invertedViewProjectMatrix,0,nearPointNdc,0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        ObjectBuilder.Point rayNearPoint = new ObjectBuilder.Point(
                nearPointWorld[0],
                nearPointWorld[1],
                nearPointWorld[2]);
        ObjectBuilder.Point rayFarPoint = new ObjectBuilder.Point(
                farPointWorld[0],
                farPointWorld[1],
                farPointWorld[2]);

        return new Ray(rayNearPoint, GeometryHelper.vectorBetween(rayNearPoint, rayFarPoint));
    }

    private static void divideByW(float[] vector){
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}
