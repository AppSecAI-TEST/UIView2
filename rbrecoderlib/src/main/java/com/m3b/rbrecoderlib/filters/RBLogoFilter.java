package com.m3b.rbrecoderlib.filters;

import android.graphics.Bitmap;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.OpenGLUtils2;
import com.m3b.rbrecoderlib.Rotation;
import com.m3b.rbrecoderlib.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by m3b on 17/2/22.
 */
public class RBLogoFilter extends GPUImageFilter {
        private static final String LOGO_VERTEX_SHADER =
                "attribute vec4 position;\n" +
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


        public static final String LOGO_FRAGMENT_SHADER =
                " varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " uniform highp vec4 imageRect;\n" +
                " uniform highp float rotate;\n" +
                " lowp vec2 iCamTextureCoord;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "      lowp vec4 c1 = texture2D(inputImageTexture, textureCoordinate);\n" +
                "      if(rotate < 0.5)\n"+
                "      {\n"+
                "      iCamTextureCoord = vec2(1.0-textureCoordinate.y,textureCoordinate.x);\n" +
                "     }else{\n" +
                "      iCamTextureCoord = vec2(1.0-textureCoordinate.y,1.0-textureCoordinate.x);" +
                "      }\n" +
                "      if(iCamTextureCoord.x>imageRect.r && iCamTextureCoord.x<imageRect.b && iCamTextureCoord.y>imageRect.g && iCamTextureCoord.y<imageRect.a)\n" +
                "  {\n" +
                "      lowp vec2 textureCoordinateToUse2 = vec2((iCamTextureCoord.x-imageRect.r)/(imageRect.b-imageRect.r),(iCamTextureCoord.y-imageRect.g)/(imageRect.a-imageRect.g));\n" +
                "      lowp vec4 c2 = texture2D(inputImageTexture2, textureCoordinateToUse2);\n" +
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
        public int mFilterSourceTexture2 =  OpenGLUtils2.NO_TEXTURE;
        private ByteBuffer mTexture2CoordinatesBuffer;
        private Bitmap mBitmap;

        private int glImageRectLoc;
        protected RectF iconRectF;
        protected Rect iconRect;

        private int needRotate;

        private boolean needUpdate;


        public RBLogoFilter(Rect _rect) {
            this(LOGO_VERTEX_SHADER, LOGO_FRAGMENT_SHADER);
            iconRectF = new RectF();
            iconRect = _rect;
            setRotation(Rotation.ROTATION_270, true, true);
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

            needRotate = GLES20.glGetUniformLocation(getProgram(), "rotate");

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
                    if (mFilterSourceTexture2 == OpenGLUtils2.NO_TEXTURE) {
                        if (bitmap == null || bitmap.isRecycled()) {
                            return;
                        }
                        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                        mFilterSourceTexture2 = OpenGLUtils2.loadTexture(bitmap, OpenGLUtils2.NO_TEXTURE, false);
                    }
                }
            });
        }

        public void updateIcon(boolean rotate) {
            needUpdate = rotate;

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

        @Override
        public void onDestroy() {
            super.onDestroy();
            GLES20.glDeleteTextures(1, new int[]{
                    mFilterSourceTexture2
            }, 0);
            mFilterSourceTexture2 = OpenGLUtils2.NO_TEXTURE;
        }



        @Override
        protected void onDrawArraysPre() {
            GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            iconRectF.top = iconRect.top / (float) getOutputHeight();
            iconRectF.bottom = iconRect.bottom / (float) getOutputHeight();
            iconRectF.left = iconRect.left / (float) getOutputWidth();
            iconRectF.right = iconRect.right / (float) getOutputWidth();
            if (needUpdate) {
                GLES20.glUniform1f(needRotate, 1.0f);
            }else{
                GLES20.glUniform1f(needRotate, 0.0f);
            }
            //Log.d("gles","getOutputHeight():" + iconRectF.top + " getOutputWidth():" + iconRectF.left );
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

