package com.mcy.airhockey;

/**
 * Created by 海 on 2015/11/13.
 */
public interface TouchHandler {
    void handleTouchPress(float normalX,float normalY);
    void handleTouchDrag(float normalX,float normalY);
}
