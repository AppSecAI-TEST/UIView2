package com.m3b.rbrecoderlib;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by m3b on 17/2/23.
 */

public class RBPaopaoFilter extends GPUImageFilter
{
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


    private static final String NORMAL_BLEND_FRAGMENT_SHADER =
            "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform sampler2D inputImageTexture2;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "	lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "	lowp vec4 c1 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "\n" +
                    "	lowp vec4 outputColor;\n" +
                    "	\n" +
                    "	outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);\n" +
                    "\n" +
                    "	outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);\n" +
                    "\n" +
                    "	outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);\n" +
                    "	\n" +
                    "	outputColor.a = c1.a + c2.a * (1.0 - c1.a);\n" +
                    "\n" +
                    "	gl_FragColor = outputColor;\n" +
                    "}";


    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;

    Context context;

    private float frame = 0;

    private int[] textures = new int [10];


    public RBPaopaoFilter(Context _context) {
        this(VERTEX_SHADER, NORMAL_BLEND_FRAGMENT_SHADER);
        context = _context;
    }

    public RBPaopaoFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader

        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);

            setBitmap();
    }

    public void setBitmap() {

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);

                    textures[0] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_001), OpenGlUtils.NO_TEXTURE, false);
                    textures[1] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_002), OpenGlUtils.NO_TEXTURE, false);
                    textures[2] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_003), OpenGlUtils.NO_TEXTURE, false);
                    textures[3] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_004), OpenGlUtils.NO_TEXTURE, false);
                    textures[4] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_005), OpenGlUtils.NO_TEXTURE, false);
                    textures[5] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_006), OpenGlUtils.NO_TEXTURE, false);
                    textures[6] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_007), OpenGlUtils.NO_TEXTURE, false);
                    textures[7] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_008), OpenGlUtils.NO_TEXTURE, false);
                    textures[8] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_009), OpenGlUtils.NO_TEXTURE, false);
                    textures[9] = OpenGlUtils.loadTexture(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_010), OpenGlUtils.NO_TEXTURE, false);

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
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[(int)frame]);

        GLES20.glUniform1i(mFilterInputTextureUniform2, 3);

        frame+=0.2f;
        if(frame > 9){
            frame = 0;
        }
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
