package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/30.
 */

public class ToolsJNI {
    public static native float compareFeatures(String feature1, String feature2, float thre);

    public static native void drawFaceRect(Image image, Face face, int color);

    public static native int testPerformance();

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}
