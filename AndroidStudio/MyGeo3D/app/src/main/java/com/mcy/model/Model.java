package com.mcy.model;

import com.mcy.geometry.ObjectBuilder;
import com.mcy.glprogram.ShaderProgram;
import com.mcy.util.VertexArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 海 on 2015/11/12.
 */
public abstract class Model {
    protected VertexArray vertexArray;
    protected List<ObjectBuilder.DrawCommand> drawList;

    public abstract void bind(ShaderProgram program);

    public void draw(){
        if(drawList!=null&&drawList.size()>0){
            for (ObjectBuilder.DrawCommand command:drawList){
                command.draw();
            }
        }
    }

    public void addDrawCommand(ObjectBuilder.DrawCommand command){
        if(drawList==null)
            drawList = new ArrayList<>();
        drawList.add(command);
    }
}
