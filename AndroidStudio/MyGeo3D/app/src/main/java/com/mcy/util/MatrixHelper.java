package com.mcy.util;

/**
 * Created by 海 on 2015/11/7.
 */
public class MatrixHelper {
    /**
     * 透视矩阵求解
     * @param m 输出矩阵
     * @param yFovInDegree Y轴可视角度（角度表示）
     * @param aspect 宽高比
     * @param n 焦点到近面距离（投影面）
     * @param f 焦点到远面距离（实际面）
     */
    public static void perspectiveMatrix(float[] m,float yFovInDegree,float aspect
            ,float n,float f){
        //计算透视椎体Y轴视野角（弧度表示）
        float angleInRadians = (float)(yFovInDegree*Math.PI/180.0);
        //计算焦距
        float a = (float)(1.0/Math.tan(angleInRadians/2.0));
        //输出透视矩阵
        m[0] = a/aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -(f+n)/(f-n);
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n)/(f-n));
        m[15] = 0f;
    }
}
