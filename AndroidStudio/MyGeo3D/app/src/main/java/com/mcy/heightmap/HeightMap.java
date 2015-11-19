package com.mcy.heightmap;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.mcy.geometry.Point;
import com.mcy.geometry.Vector;
import com.mcy.glprogram.HeightMapPointLightShaderProgram;
import com.mcy.glprogram.HeightMapShaderProgram;
import com.mcy.glprogram.ShaderProgram;
import com.mcy.model.Model;
import com.mcy.util.Constants;
import com.mcy.util.GeometryHelper;
import com.mcy.util.IndexBuffer;
import com.mcy.util.VertexBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by gis on 2015/11/18.
 */
public class HeightMap extends Model{

    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TOTAL_COMPONENT_COUNT = Constants.POSITION_3D+NORMAL_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT*Constants.BYTE_PER_FLOAT;

    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;
    public HeightMap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if(width*height>65536){
           throw new RuntimeException("Height map is too larger for index buffer");
        }
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for(int row = 0;row<height-1;row++){
            for(int col = 0;col<width-1;col++){

                short topLeftIndexNum = (short)(row*width+col);
                short topRightIndexNum = (short)(row*width+col+1);
                short bottomLeftIndexNum = (short)((row+1)*width+col);
                short bottomRightIndexNum = (short)((row+1)*width+col+1);

                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }
        return indexData;
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float[] heightVertices = new float[width*height* TOTAL_COMPONENT_COUNT];
        int offset = 0;

        for(int row = 0;row<height;row++){
            for (int col = 0;col<width;col++){

                final Point point = getPoint(pixels,row,col);
                heightVertices[offset++] = point.x;
                heightVertices[offset++] = point.y;
                heightVertices[offset++] = point.z;

                //获取临近点，计算当前点表面法线
                final Point top = getPoint(pixels,row-1,col);
                final Point left = getPoint(pixels,row,col-1);
                final Point right = getPoint(pixels,row,col+1);
                final Point bottom = getPoint(pixels,row+1,col);

                final Vector rightToLeft = GeometryHelper.vectorBetween(right, left);
                final Vector topToBottom = GeometryHelper.vectorBetween(top,bottom);
                final Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightVertices[offset++] = normal.x;
                heightVertices[offset++] = normal.y;
                heightVertices[offset++] = normal.z;
            }
        }

        return heightVertices;
    }

    private int calculateNumElements() {
        return (width-1)*(height-1)*2*3;
    }

    private Point getPoint(int[] pixels,int row,int col){

        float x = ((float)col/(float)(width-1))-0.5f;
        float z = ((float)row/(float)(height-1))-0.5f;

        row = clamp(row,0,width-1);
        col = clamp(col,0,height-1);

        float y = (float)Color.red(pixels[(row*height)+col])/(float)255;

        return new Point(x,y,z);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min,Math.min(max,val));
    }

    @Override
    public void bind(ShaderProgram program) {
        if(program instanceof HeightMapShaderProgram){
            vertexBuffer.setVertexAttributePointer(
                    0,
                    ((HeightMapShaderProgram)program).getPositionLocation(),
                    Constants.POSITION_3D,
                    STRIDE);

            vertexBuffer.setVertexAttributePointer(
                    Constants.POSITION_3D*Constants.BYTE_PER_FLOAT,
                    ((HeightMapShaderProgram)program).getNormalLocation(),
                    NORMAL_COMPONENT_COUNT,
                    STRIDE);
        }else if(program instanceof HeightMapPointLightShaderProgram){
            vertexBuffer.setVertexAttributePointer(
                    0,
                    ((HeightMapPointLightShaderProgram)program).getPositionLocation(),
                    Constants.POSITION_3D,
                    STRIDE);

            vertexBuffer.setVertexAttributePointer(
                    Constants.POSITION_3D*Constants.BYTE_PER_FLOAT,
                    ((HeightMapPointLightShaderProgram)program).getNormalLocation(),
                    NORMAL_COMPONENT_COUNT,
                    STRIDE);
        }else{
            throw new RuntimeException("wrong shader program");
        }

    }

    @Override
    public void draw(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
    }
}
