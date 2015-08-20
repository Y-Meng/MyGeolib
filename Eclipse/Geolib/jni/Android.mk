LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS += -llog
LOCAL_MODULE    := mgeo
LOCAL_SRC_FILES := mgeo.cpp

include $(BUILD_SHARED_LIBRARY)
