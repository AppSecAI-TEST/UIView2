package com.m3b.rbrecoderlib.filters;

import android.opengl.GLES20;
import android.view.View;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.GPUImageView;
import com.m3b.rbrecoderlib.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by m3b on 17/3/20.
 */

public class RBCropFilter extends GPUImageFilter {
    private CropRegion mCropRegion;

    public RBCropFilter() {
        this(new CropRegion(0f, 0f, 0.5f, 1.0f));

    }

    public RBCropFilter(final CropRegion cropRegion) {
        super(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
        mCropRegion = cropRegion;

    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setCropRegion(mCropRegion);
    }

    public void setCropRegion(final CropRegion cropRegion) {
        mCropRegion = cropRegion;
    }


    @Override
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        float minX = mCropRegion.mX;
        float minY = mCropRegion.mY;
        float maxX = mCropRegion.mX + mCropRegion.mWidth;
        float maxY = mCropRegion.mY + mCropRegion.mHeight;

        super.onDraw(textureId, convertArrayToBuffer(new float[]{

                1.0f, 1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                -1.0f, -1.0f,


        }), convertArrayToBuffer(new float[]{
                minX, minY,
                maxX, minY,
                minX, maxY,
                maxX, maxY
        }));
        /*
        super.onDraw(textureId, convertArrayToBuffer(new float[]{
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        }), convertArrayToBuffer(new float[]{
                maxX, maxY,
                minX, maxY,
                maxX, minY,
                minX, minY
        }));
        */

    }


    private FloatBuffer convertArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(array);
        return buffer;
    }

    public static class CropRegion {
        float mX;
        float mY;
        float mWidth;
        float mHeight;

        public CropRegion(float x, float y, float width, float height) {
            mX = x;
            mY = y;
            mWidth = width;
            mHeight = height;
        }
    }


}