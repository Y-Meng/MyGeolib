package com.mcy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * Created by 海 on 2015/11/11.
 */
public class TextureHelper {

    private final static String TAG = "GL ES TextureHelper";

    public static int loadTexture(Context context,int resId){
        final int[] textureObjIds = new int[1];

        glGenTextures(1,textureObjIds,0);

        if(textureObjIds[0]==0){
            if(LoggerConfig.ON){
                Log.w(TAG,"generate new texture object fail");
            }
            return 0;
        }

        //解压为android位图
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                resId,options);
        if(bitmap==null){
            if(LoggerConfig.ON){
                Log.e(TAG,"decode resource:"+resId+" failed");
            }
            return 0;
        }

        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureObjIds[0]);
        //设置纹理过滤参数（纹理类型，过滤器类型，过滤规则）
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        //加载纹理
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //回收位图
        bitmap.recycle();
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,0);
        //返回纹理ID
        return textureObjIds[0];
    }
}
