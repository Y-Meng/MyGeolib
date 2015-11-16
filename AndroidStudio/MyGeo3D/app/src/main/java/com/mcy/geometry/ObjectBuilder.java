package com.mcy.geometry;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 海 on 2015/11/12.
 */
public class ObjectBuilder {
    public static class Point{
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

    public static class Circle{
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

    public static class Cylinder{
        public final float radius;
        public final float height;
        public final Point center;
        public Cylinder(Point center,float radius,float height){
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class GeneratedData{
        public final float[] vertexData;
        public final List<ObjectBuilder.DrawCommand> drawList;
        public GeneratedData(float[] vertexData,List<ObjectBuilder.DrawCommand> drawList){
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    public interface DrawCommand{
        void draw();
    }

    //ObjectBuilder
    private static final int FLOAT_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offSet = 0;
    private final List<DrawCommand> drawList = new ArrayList<>();

    public ObjectBuilder(int sizeInVertices){
        vertexData = new float[sizeInVertices*FLOAT_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints){
        return 1+numPoints+1;
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints){
        return (numPoints+1)*2;
    }

    /**
     * 生成球数据
     * @param puck
     * @param numPoints
     * @return
     */
    public static GeneratedData createPuck(Cylinder puck,int numPoints){
        int size = sizeOfCircleInVertices(numPoints)
                +sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);

        Circle puckTop = new Circle(puck.center.translateY(puck.height/2f),puck.radius);

        builder.appendCircle(puckTop,numPoints);
        builder.appendOpenCylinder(puck,numPoints);

        return builder.build();
    }

    /**
     * 生成撞锤数据
     * @param center
     * @param radius
     * @param height
     * @param numPoints
     * @return
     */
    public static GeneratedData createMallet(Point center,float radius,float height,int numPoints){

        int size = sizeOfCircleInVertices(numPoints)*2
                +sizeOfOpenCylinderInVertices(numPoints)*2;

        ObjectBuilder builder = new ObjectBuilder(size);

        //Base Part
        float baseHeight = height*0.25f;
        Circle baseCircle = new Circle(center.translateY(-baseHeight),radius);
        Cylinder baseCylinder = new Cylinder(baseCircle.center.translateY(-baseHeight/2f),
                radius,baseHeight);
        builder.appendCircle(baseCircle,numPoints);
        builder.appendOpenCylinder(baseCylinder,numPoints);

        //Handle Part
        float handleHeight = height*0.75f;
        float handleRadius = radius/3f;

        Circle handleCircle = new Circle(center.translateY(height*0.5f),handleRadius);
        Cylinder handleCylinder = new Cylinder(handleCircle.center.translateY(-handleHeight/2f),
                handleRadius,handleHeight);
        builder.appendCircle(handleCircle,numPoints);
        builder.appendOpenCylinder(handleCylinder,numPoints);

        return builder.build();
    }

    /**
     * 生成数据
     * @return
     */
    private GeneratedData build(){
        return new GeneratedData(vertexData,drawList);
    }

    /**
     * 在数据中添加一个圆的三角扇顶点数据
     * @param circle     圆对象
     * @param numPoints  分割圆的点个数
     */
    private void appendCircle(Circle circle, final int numPoints){
        final int startVertex = offSet/FLOAT_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        //圆心点
        vertexData[offSet++] = circle.center.x;
        vertexData[offSet++] = circle.center.y;
        vertexData[offSet++] = circle.center.z;
        //扇形点
        for(int i = 0;i<=numPoints;i++){
            float angleInRadius = ((float)i/(float)numPoints)*((float)Math.PI*2f);

            vertexData[offSet++] = circle.center.x + circle.radius* (float)Math.cos(angleInRadius);
            vertexData[offSet++] = circle.center.y;
            vertexData[offSet++] = circle.center.z+circle.radius*(float)Math.sin(angleInRadius);
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,startVertex,numVertices);
            }
        });
    }

    /**
     * 在数据中添加一个开口圆柱体
     * @param cylinder
     * @param numPoints
     */
    private void appendOpenCylinder(Cylinder cylinder,int numPoints){
        final int startVertex = offSet/FLOAT_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height/2);
        final float yEnd = cylinder.center.y + (cylinder.height/2);
        //生成三角带
        for(int i = 0;i<=numPoints;i++){
            float angleInRadians = ((float)i/(float)numPoints)*((float)Math.PI*2f);
            float xPosition = cylinder.center.x+cylinder.radius*(float)Math.cos(angleInRadians);
            float zPosition = cylinder.center.z+cylinder.radius*(float)Math.sin(angleInRadians);

            //底点
            vertexData[offSet++] = xPosition;
            vertexData[offSet++] = yStart;
            vertexData[offSet++] = zPosition;
            //顶点
            vertexData[offSet++] = xPosition;
            vertexData[offSet++] = yEnd;
            vertexData[offSet++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,startVertex,numVertices);
            }
        });
    }
}
