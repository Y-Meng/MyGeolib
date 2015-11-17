package com.mcy.util;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderHelper {

    private static final String TAG = "OpenGL ES2.0";

    /**
     * 读取shader文本
     * @param context
     * @param resID
     * @return
     */
    public static String readTextShaderFromResource(Context context,int resID){

        StringBuffer body = new StringBuffer();
        try {
            InputStream inputStream = context.getResources().openRawResource(resID);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine())!=null){
                body.append(nextLine);
                body.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found:"+resID,nfe);
        }
        return body.toString();
    }

    /**
     * 编译顶点着色器
     * @param shaderCode
     * @return
     */
    public static int compileVertexShader(String shaderCode){
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片段着色器
     * @param shaderCode
     * @return
     */
    public static int compileFragmentShader(String shaderCode){
        return  compileShader(GLES20.GL_FRAGMENT_SHADER,shaderCode);
    }

    /**
     * 链接OpenGL程序
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return
     */
    public static int linkProgram(int vertexShaderId,int fragmentShaderId){

        //创建一个程序对象
        final int programId = GLES20.glCreateProgram();
        if(programId==0){
            if(LoggerConfig.ON){
                Log.w(TAG,"could not create program");
            }
            return  0;
        }
        //绑定着色器
        GLES20.glAttachShader(programId,vertexShaderId);
        GLES20.glAttachShader(programId, fragmentShaderId);
        //链接程序
        GLES20.glLinkProgram(programId);
        //检查链接结果
        final int[] linkStatues = new int[1];
        GLES20.glGetProgramiv(programId,GLES20.GL_LINK_STATUS,linkStatues,0);
        if(LoggerConfig.ON){
            Log.v(TAG,"result of link program"+GLES20.glGetProgramInfoLog(programId));
        }
        if(linkStatues[0]==0){
            //链接失败,删除程序
            GLES20.glDeleteProgram(programId);
            if(LoggerConfig.ON){
                Log.w(TAG,"Link program failed");
            }
            return 0;
        }
        return programId;
    }

    /**
     * 判断一个程序是否效率低下，不可运行
     * @param programid
     * @return
     */
    public static boolean validateProgram(int programid){

        GLES20.glValidateProgram(programid);
        int[] validateResult = new int[1];
        GLES20.glGetProgramiv(programid,GLES20.GL_VALIDATE_STATUS,validateResult,0);
        Log.v(TAG,"result of validating program:"+validateResult[0]
                +GLES20.glGetProgramInfoLog(programid));
        return  validateResult[0]!=0;
    }

    //编译着色器
    private static int compileShader(int type, String shaderCode) {
        //新建一个着色器对象
        final int shaderObjId = GLES20.glCreateShader(type);
        if(shaderObjId==0){
            if (LoggerConfig.ON){
                Log.w("OpenGL ES2.0","Could not create shader");
            }
            return 0;
        }
        //上传着色器代码
        GLES20.glShaderSource(shaderObjId, shaderCode);
        //编译着色器代码
        GLES20.glCompileShader(shaderObjId);
        //取出编译状态
        final int[] compileStatues = new int[1];
        GLES20.glGetShaderiv(shaderObjId, GLES20.GL_COMPILE_STATUS, compileStatues, 0);
        if(LoggerConfig.ON){
            Log.v("OpenGL ES2.0","Result of compile source:"
                    +GLES20.glGetShaderInfoLog(shaderObjId));
        }
        //验证编译状态
        if(compileStatues[0]==0){
            //失败删除shader
            GLES20.glDeleteShader(shaderObjId);
            if(LoggerConfig.ON){
                Log.w(TAG,"Compile shader fail");
            }
            return 0;
        }
        return shaderObjId;
    }

    public static int buildProgram(String vsRes,String fsRes){
        int program;
        int vertexShader = compileVertexShader(vsRes);
        int fragmentShader = compileFragmentShader(fsRes);
        program = linkProgram(vertexShader,fragmentShader);
        if(LoggerConfig.ON){
            validateProgram(program);
        }
        return program;
    }
}
