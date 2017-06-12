

package com.m3b.rbrecoderlib;

public class GPUImageNativeLibrary {
    static {
        System.loadLibrary("gpuimage-library");
    }

    public static native void YUVtoRBGA(byte[] yuv, int width, int height, int[] out);

    public static native void YUVtoARBG(byte[] yuv, int width, int height, int[] out);
}
