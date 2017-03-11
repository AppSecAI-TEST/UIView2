

package com.m3b.rbrecoderlib;

import android.opengl.GLES20;


public class GPUImageMixBlendFilter extends GPUImageTwoInputFilter {

    private int mMixLocation;
    private float mMix;

    public GPUImageMixBlendFilter(String fragmentShader) {
        this(fragmentShader, 0.5f);
    }

    public GPUImageMixBlendFilter(String fragmentShader, float mix) {
        super(fragmentShader);
        mMix = mix;
    }

    @Override
    public void onInit() {
        super.onInit();
        mMixLocation = GLES20.glGetUniformLocation(getProgram(), "mixturePercent");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setMix(mMix);
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    public void setMix(final float mix) {
        mMix = mix;
        setFloat(mMixLocation, mMix);
    }
}
