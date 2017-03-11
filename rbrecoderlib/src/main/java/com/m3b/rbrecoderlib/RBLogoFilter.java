package com.m3b.rbrecoderlib;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;

import com.m3b.rbrecoderlib.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by m3b on 17/2/22.
 */
public class RBLogoFilter extends GPUImageFilter {
        private static final String VERTEX_SHADER = "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                "attribute vec4 inputTextureCoordinate2;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 textureCoordinate2;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
                "}";

        public static final String LOGO_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " uniform vec4 imageRect;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "      lowp vec4 c1 = texture2D(inputImageTexture, textureCoordinate);\n" +
                "      lowp vec2 vCamTextureCoord2 = vec2(1.0-textureCoordinate.y,textureCoordinate.x);\n" +
                "      if(vCamTextureCoord2.x>imageRect.r && vCamTextureCoord2.x<imageRect.b && vCamTextureCoord2.y>imageRect.g && vCamTextureCoord2.y<imageRect.a)\n" +
                "  {\n" +
                "      vec2 textureCoordinateToUse2 = vec2((vCamTextureCoord2.x-imageRect.r)/(imageRect.b-imageRect.r),(vCamTextureCoord2.y-imageRect.g)/(imageRect.a-imageRect.g));\n" +
                "\t    lowp vec4 c2 = texture2D(inputImageTexture2, textureCoordinateToUse2);\n" +
                "      lowp vec4 outputColor = c2+c1*c1.a*(1.0-c2.a);\n" +
                "      outputColor.a = 1.0;\n" +
                "      gl_FragColor = outputColor;\n" +
                "   }else\n" +
                "   {\n" +
                "       gl_FragColor = c1;\n" +
                "   }\n" +
                "}";

        public int mFilterSecondTextureCoordinateAttribute;
        public int mFilterInputTextureUniform2;
        public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
        private ByteBuffer mTexture2CoordinatesBuffer;
        private Bitmap mBitmap;

        private int glImageRectLoc;
        protected RectF iconRectF;
        protected Rect iconRect;


        public RBLogoFilter(@NonNull Rect _rect) {
            this(VERTEX_SHADER, LOGO_FRAGMENT_SHADER);
            iconRectF = new RectF();
            iconRect = _rect;
        }

        public RBLogoFilter(String vertexShader, String fragmentShader) {
            super(vertexShader, fragmentShader);
            setRotation(Rotation.NORMAL, false, false);
        }

        @Override
        public void onInit() {
            super.onInit();

            mFilterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
            mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
            glImageRectLoc = GLES20.glGetUniformLocation(getProgram(), "imageRect");

            GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);

            if (mBitmap != null&&!mBitmap.isRecycled()) {
                setBitmap(mBitmap);
            }
        }

        public void setBitmap(final Bitmap bitmap) {
            if (bitmap != null && bitmap.isRecycled()) {
                return;
            }
            mBitmap = bitmap;
            if (mBitmap == null) {
                return;
            }
            runOnDraw(new Runnable() {
                public void run() {
                    if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                        if (bitmap == null || bitmap.isRecycled()) {
                            return;
                        }
                        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                        mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                    }
                }
            });
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void recycleBitmap() {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
                mBitmap = null;
            }
        }

        public void onDestroy() {
            super.onDestroy();
            GLES20.glDeleteTextures(1, new int[]{
                    mFilterSourceTexture2
            }, 0);
            mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
        }

        @Override
        protected void onDrawArraysPre() {
            GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            iconRectF.top = iconRect.top / (float) 920;
            iconRectF.bottom = iconRect.bottom / (float) 920;
            iconRectF.left = iconRect.left / (float) 920;
            iconRectF.right = iconRect.right / (float) 920;
            GLES20.glUniform4f(glImageRectLoc,iconRectF.left, iconRectF.top, iconRectF.right, iconRectF.bottom);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
            GLES20.glUniform1i(mFilterInputTextureUniform2, 3);
            mTexture2CoordinatesBuffer.position(0);
            GLES20.glVertexAttribPointer(mFilterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);
        }

        public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
            float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

            ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
            FloatBuffer fBuffer = bBuffer.asFloatBuffer();
            fBuffer.put(buffer);
            fBuffer.flip();

            mTexture2CoordinatesBuffer = bBuffer;
        }


        @Override
        public void onInitialized() {
            super.onInitialized();
        }

}

