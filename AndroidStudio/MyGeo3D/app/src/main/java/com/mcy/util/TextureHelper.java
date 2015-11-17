package com.mcy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by gis on 2015/11/11.
 */
public class TextureHelper {

    private final static String TAG = "GL ES TextureHelper";

    /**
     * 加载单个纹理文件到GLES环境
     * @param context 资源环境
     * @param resId   资源ID
     * @return
     */
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
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //回收位图
        bitmap.recycle();
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,0);
        //返回纹理ID
        return textureObjIds[0];
    }

    /**
     * 加载立方体纹理
     * @param context 资源环境
     * @param cubeResources 资源id
     * @return
     */
    public static int loadCubeMap(Context context,int[] cubeResources){
        final int[] textureObjectIds = new int[1];
        glGenTextures(1,textureObjectIds,0);

        if(textureObjectIds[0]==0){
            if(LoggerConfig.ON){
                Log.w(TAG,"Couldn't generate a new gl texture object");
            }
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];

        for(int i=0;i<6;i++){
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(),cubeResources[i]);
            if(cubeBitmaps[i]==null){
                if(LoggerConfig.ON){
                    Log.w(TAG,"Couldn't decode resource:"+cubeResources[i]);
                }
                glDeleteTextures(1,textureObjectIds,0);
                return  0;
            }
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //加载位图数据
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        glBindTexture(GL_TEXTURE_2D,0);

        for(Bitmap bitmap:cubeBitmaps){
            bitmap.recycle();
        }

        return textureObjectIds[0];
    }
}
