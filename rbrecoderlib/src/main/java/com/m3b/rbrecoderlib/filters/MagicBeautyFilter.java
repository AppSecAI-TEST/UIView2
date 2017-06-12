package com.m3b.rbrecoderlib.filters;

import android.content.Context;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.OpenGLUtils2;
import com.m3b.rbrecoderlib.R;


public class MagicBeautyFilter extends GPUImageFilter {

    private int glAaCoef;
    private int glMixCoef;
    private int glIternum;
    private int glWidth;
    private int glHeight;

    private float aaCoef;
    private float mixCoef;
    private int iternum;
    private float mWidth, mHeight;


    public MagicBeautyFilter(Context context) {
        super(NO_FILTER_VERTEX_SHADER, OpenGLUtils2.readShaderFromRawResource(context, R.raw.beautify_fragment));
    }

    public MagicBeautyFilter setBeautyLevel(int flag) {
        switch (flag) {
            case 1:
                a(1, 0.19f, 0.54f);
                break;
            case 2:
                a(2, 0.29f, 0.54f);
                break;
            case 3:
                a(3, 0.17f, 0.39f);
                break;
            case 4:
                a(3, 0.25f, 0.54f);
                break;
            case 5:
                a(4, 0.13f, 0.54f);
                break;
            case 6:
                a(4, 0.19f, 0.69f);
                break;
            default:
                a(0, 0f, 0f);
                break;
        }
        return this;
    }

    private void a(int a, float b, float c) {
        this.iternum = a;
        this.aaCoef = b;
        this.mixCoef = c;
    }

    @Override
    public void onDrawArraysPre() {
        GLES20.glUniform1f(glAaCoef, aaCoef);
        GLES20.glUniform1f(glMixCoef, mixCoef);
        GLES20.glUniform1i(glIternum, iternum);
        GLES20.glUniform1f(glWidth, mWidth);
        GLES20.glUniform1f(glHeight, mHeight);
    }

    @Override
    public void onInit() {
        super.onInit();
        glAaCoef = GLES20.glGetUniformLocation(getProgram(), "uAaCoef");
        glMixCoef = GLES20.glGetUniformLocation(getProgram(), "uMixCoef");
        glIternum = GLES20.glGetUniformLocation(getProgram(), "uIternum");
        glWidth = GLES20.glGetUniformLocation(getProgram(), "uWidth");
        glHeight = GLES20.glGetUniformLocation(getProgram(), "uHeight");
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
    }
}
