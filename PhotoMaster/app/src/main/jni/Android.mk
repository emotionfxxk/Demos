# Makefile for libjpeg-turbo
 
ifneq ($(TARGET_SIMULATOR),true)
 
##################################################
###                image compressor            ###
##################################################
LOCAL_PATH := $(my-dir)
include $(CLEAR_VARS)

ifeq ($(ARCH_ARM_HAVE_NEON),true) LOCAL_CFLAGS += -D__ARM_HAVE_NEON
endif

LOCAL_SRC_FILES := com_webeye_photomaster_jni_ImageCompressor.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)
 
AM_CFLAGS := -march=armv7-a -mfpu=neon
AM_CCASFLAGS := -march=armv7-a -mfpu=neon
 
LOCAL_MODULE_TAGS := debug
 
LOCAL_MODULE := libjpegcompressor
 
include $(BUILD_SHARED_LIBRARY)

endif  # TARGET_SIMULATOR != true
