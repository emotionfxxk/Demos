# Makefile for libjpeg-turbo
 
ifneq ($(TARGET_SIMULATOR),true)
LOCAL_PATH := $(my-dir)
##################################################
###                simd                        ###
##################################################

include $(CLEAR_VARS)

ifeq ($(ARCH_ARM_HAVE_NEON),true) LOCAL_CFLAGS += -D__ARM_HAVE_NEON
endif

# From autoconf-generated Makefile
EXTRA_DIST = libjpeg-turbo/simd/nasm_lt.sh libjpeg-turbo/simd/jcclrmmx.asm libjpeg-turbo/simd/jcclrss2.asm libjpeg-turbo/simd/jdclrmmx.asm libjpeg-turbo/simd/jdclrss2.asm \
	libjpeg-turbo/simd/jdmrgmmx.asm libjpeg-turbo/simd/jdmrgss2.asm libjpeg-turbo/simd/jcclrss2-64.asm libjpeg-turbo/simd/jdclrss2-64.asm \
	libjpeg-turbo/simd/jdmrgss2-64.asm libjpeg-turbo/simd/CMakeLists.txt
 
libsimd_SOURCES_DIST = libjpeg-turbo/simd/jsimd_arm_neon.S \
                       libjpeg-turbo/simd/jsimd_arm.c 

LOCAL_SRC_FILES := $(libsimd_SOURCES_DIST)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libjpeg-turbo/simd \
                    $(LOCAL_PATH)/libjpeg-turbo/android
 
AM_CFLAGS := -march=armv7-a -mfpu=neon
AM_CCASFLAGS := -march=armv7-a -mfpu=neon
 
LOCAL_MODULE_TAGS := debug
 
LOCAL_MODULE := libsimd
 
include $(BUILD_STATIC_LIBRARY)

######################################################
###                libjpeg.so                       ##
###################################################### 
include $(CLEAR_VARS)

# From autoconf-generated Makefile
libjpeg_SOURCES_DIST =  libjpeg-turbo/jcapimin.c libjpeg-turbo/jcapistd.c libjpeg-turbo/jccoefct.c libjpeg-turbo/jccolor.c \
        libjpeg-turbo/jcdctmgr.c libjpeg-turbo/jchuff.c libjpeg-turbo/jcinit.c libjpeg-turbo/jcmainct.c libjpeg-turbo/jcmarker.c libjpeg-turbo/jcmaster.c \
        libjpeg-turbo/jcomapi.c libjpeg-turbo/jcparam.c libjpeg-turbo/jcphuff.c libjpeg-turbo/jcprepct.c libjpeg-turbo/jcsample.c libjpeg-turbo/jctrans.c \
        libjpeg-turbo/jdapimin.c libjpeg-turbo/jdapistd.c libjpeg-turbo/jdatadst.c libjpeg-turbo/jdatasrc.c libjpeg-turbo/jdcoefct.c libjpeg-turbo/jdcolor.c \
        libjpeg-turbo/jddctmgr.c libjpeg-turbo/jdhuff.c libjpeg-turbo/jdinput.c libjpeg-turbo/jdmainct.c libjpeg-turbo/jdmarker.c libjpeg-turbo/jdmaster.c \
        libjpeg-turbo/jdmerge.c libjpeg-turbo/jdphuff.c libjpeg-turbo/jdpostct.c libjpeg-turbo/jdsample.c libjpeg-turbo/jdtrans.c libjpeg-turbo/jerror.c \
        libjpeg-turbo/jfdctflt.c libjpeg-turbo/jfdctfst.c libjpeg-turbo/jfdctint.c libjpeg-turbo/jidctflt.c libjpeg-turbo/jidctfst.c libjpeg-turbo/jidctint.c \
        libjpeg-turbo/jidctred.c libjpeg-turbo/jquant1.c libjpeg-turbo/jquant2.c libjpeg-turbo/jutils.c libjpeg-turbo/jmemmgr.c \
        libjpeg-turbo/jmemnobs.c libjpeg-turbo/jaricom.c libjpeg-turbo/jcarith.c libjpeg-turbo/jdarith.c \
	    libjpeg-turbo/turbojpeg.c libjpeg-turbo/transupp.c libjpeg-turbo/jdatadst-tj.c libjpeg-turbo/jdatasrc-tj.c \
	    libjpeg-turbo/turbojpeg-mapfile

LOCAL_SRC_FILES:= $(libjpeg_SOURCES_DIST)
 
LOCAL_SHARED_LIBRARIES := libcutils
LOCAL_STATIC_LIBRARIES := libsimd
 
LOCAL_C_INCLUDES := $(LOCAL_PATH)/libjpeg-turbo
 
LOCAL_CFLAGS := -DAVOID_TABLES  -O3 -fstrict-aliasing -fprefetch-loop-arrays  -DANDROID \
        -DANDROID_TILE_BASED_DECODE -DENABLE_ANDROID_NULL_CONVERT

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_STATIC_LIBRARY)
 
LOCAL_MODULE_TAGS := debug
 
LOCAL_MODULE := libjpeg-turbo

include $(BUILD_STATIC_LIBRARY)

######################################################
###         cjpeg                                  ###
######################################################

include $(CLEAR_VARS)

# From autoconf-generated Makefile
cjpeg_SOURCES = libjpeg-turbo/cdjpeg.c libjpeg-turbo/cjpeg.c libjpeg-turbo/rdbmp.c libjpeg-turbo/rdgif.c \
        libjpeg-turbo/rdppm.c libjpeg-turbo/rdswitch.c libjpeg-turbo/rdtarga.c

LOCAL_SRC_FILES:= $(cjpeg_SOURCES)

#LOCAL_SHARED_LIBRARIES := libjpeg-turbo
LOCAL_STATIC_LIBRARIES := libjpeg-turbo

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libjpeg-turbo/ \
                    $(LOCAL_PATH)/libjpeg-turbo//android

LOCAL_CFLAGS := -DBMP_SUPPORTED -DGIF_SUPPORTED -DPPM_SUPPORTED -DTARGA_SUPPORTED \
         -DANDROID -DANDROID_TILE_BASED_DECODE -DENABLE_ANDROID_NULL_CONVERT

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLE)

LOCAL_MODULE_TAGS := debug

LOCAL_MODULE := cjpeg

include $(BUILD_EXECUTABLE)

######################################################
###            djpeg                               ###
######################################################

include $(CLEAR_VARS)

# From autoconf-generated Makefile
djpeg_SOURCES = libjpeg-turbo/cdjpeg.c libjpeg-turbo/djpeg.c libjpeg-turbo/rdcolmap.c libjpeg-turbo/rdswitch.c \
        libjpeg-turbo/wrbmp.c libjpeg-turbo/wrgif.c libjpeg-turbo/wrppm.c libjpeg-turbo/wrtarga.c

LOCAL_SRC_FILES:= $(djpeg_SOURCES)

#LOCAL_SHARED_LIBRARIES := libjpeg-turbo
LOCAL_STATIC_LIBRARIES := libjpeg-turbo

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libjpeg-turbo \
                    $(LOCAL_PATH)/libjpeg-turbo/android

LOCAL_CFLAGS := -DBMP_SUPPORTED -DGIF_SUPPORTED -DPPM_SUPPORTED -DTARGA_SUPPORTED \
            -DANDROID -DANDROID_TILE_BASED_DECODE -DENABLE_ANDROID_NULL_CONVERT

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLE)

LOCAL_MODULE_TAGS := debug

LOCAL_MODULE := djpeg

include $(BUILD_EXECUTABLE)

##################################################
###                image compressor            ###
##################################################
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
