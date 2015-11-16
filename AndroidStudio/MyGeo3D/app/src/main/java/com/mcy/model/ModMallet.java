package com.mcy.model;

import com.mcy.geometry.ObjectBuilder;
import com.mcy.glprogram.ColorShaderProgram;
import com.mcy.glprogram.ShaderProgram;
import com.mcy.util.Constants;
import com.mcy.util.VertexArray;

/**
 * Created by 海 on 2015/11/12.
 */
public class ModMallet extends Model {

    public final float radius;
    public final float height;

    public ModMallet(float radius,float height,int numDivCirclePoints){
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(
                new ObjectBuilder.Point(0f,0f,0f),
                radius,
                height,
                numDivCirclePoints);
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    @Override
    public void bind(ShaderProgram program) {
        vertexArray.setVertexAttributePointer(
                0,
                ((ColorShaderProgram)program).getPositionLocation(),
                Constants.POSITION_COMPNENT_COUNT,
                0);
    }
}
