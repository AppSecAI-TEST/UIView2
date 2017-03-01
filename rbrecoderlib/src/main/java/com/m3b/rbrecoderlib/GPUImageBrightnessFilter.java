
package com.m3b.rbrecoderlib;

import android.opengl.GLES20;


/**
 * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
 */
public class GPUImageBrightnessFilter extends GPUImageFilter {
    public static final String BRIGHTNESS_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
            " }";

    private int mBrightnessLocation;
    private float mBrightness;

    public GPUImageBrightnessFilter() {
        this(0.0f);
    }

    public GPUImageBrightnessFilter(final float brightness) {
        super(NO_FILTER_VERTEX_SHADER, BRIGHTNESS_FRAGMENT_SHADER);
        mBrightness = brightness;
    }

    @Override
    public void onInit() {
        super.onInit();
        mBrightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setBrightness(mBrightness);
    }

    public void setBrightness(final float brightness) {
        mBrightness = brightness;
        setFloat(mBrightnessLocation, mBrightness);
    }
}
