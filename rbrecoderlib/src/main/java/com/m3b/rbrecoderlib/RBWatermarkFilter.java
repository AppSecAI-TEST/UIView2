package com.m3b.rbrecoderlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.m3b.rbrecoderlib.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by m3b on 17/2/24.
 */

public class RBWatermarkFilter extends GPUImageFilter{
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

    public static final String TEXT_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform vec4 imageRect;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 c1 = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec2 vCamTextureCoord2 = vec2(1.0-textureCoordinate.y,textureCoordinate.x);\n" +
            "   if(vCamTextureCoord2.x>imageRect.r && vCamTextureCoord2.x<imageRect.b && vCamTextureCoord2.y>imageRect.g && vCamTextureCoord2.y<imageRect.a)\n" +
            "   {\n" +
            "       vec2 textureCoordinateToUse2 = vec2((vCamTextureCoord2.x-imageRect.r)/(imageRect.b-imageRect.r),(vCamTextureCoord2.y-imageRect.g)/(imageRect.a-imageRect.g));\n" +
            "\t     lowp vec4 c2 = texture2D(inputImageTexture2, textureCoordinateToUse2);\n" +
            "     \n" +
            "       lowp vec4 outputColor = c2+c1*c1.a*(1.0-c2.a);\n" +
            "       outputColor.a = 1.0;\n" +
            "       gl_FragColor = outputColor;\n" +
            "    }else{\n" +
            "        gl_FragColor = c1;\n" +
            "    }\n" +
            " }";

    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;


    private int glImageRectLoc;

    private Bitmap textBitmap;
    private CharSequence text;
    private int textColor= Color.WHITE;
    private int textSize=30;
    private boolean textNeedUpdate;
    private boolean postionNeedUpdate;

    private Rect textRect =new Rect(0, 0, 0, 0);
    private int gravity;
    private int verticalMargin;
    private int horizontalMargin;
    protected RectF textRectF = new RectF();

    public RBWatermarkFilter() {
        this(VERTEX_SHADER, TEXT_FRAGMENT_SHADER);
    }

    public RBWatermarkFilter(String vertexShader, String fragmentShader) {
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
        updateText();
        textRectF.top = textRect.top / (float) 920;
        textRectF.bottom = textRect.bottom / (float) 920;
        textRectF.left = textRect.left / (float) 920;
        textRectF.right = textRect.right / (float) 920;
        GLES20.glUniform4f(glImageRectLoc,textRectF.left, textRectF.top, textRectF.right, textRectF.bottom);
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


    /**
     *  update text.
     * @param charSequence html,spannablestring supported.
     * @param textColor if (textColor == Color.TRANSPARENT), The text color will become inverted.
     * @param textSize text size in pixel
     */
    public void setText(CharSequence charSequence, @ColorInt int textColor, int textSize) {
        this.text = charSequence;
        this.textColor = textColor;
        this.textSize = textSize;
        textNeedUpdate = true;
        postionNeedUpdate = true;
    }


    private void updateText() {
        if (textNeedUpdate) {
            int w = 0, h = 0;
            TextPaint textPaint = new TextPaint();
            if (textColor == Color.TRANSPARENT) {
                textPaint.setColor(Color.BLACK);
            } else {
                textPaint.setColor(textColor);
            }
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);
            StaticLayout innerStaticLayout = new StaticLayout(text,
                    textPaint,
                    920 - horizontalMargin,//width
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,//相对行间距,字体高度倍数
                    0.0f,//在基础行距上添加多少
                    false);
            int lineCount = innerStaticLayout.getLineCount();
            for (int i = 0; i < lineCount; i++) {
                float lineW = innerStaticLayout.getLineWidth(i);
                if (lineW > w) {
                    w = (int) (lineW + 0.5f);
                }
            }
            h = innerStaticLayout.getHeight();
            Canvas innerCanvas;
            if (textBitmap == null || textBitmap.getWidth() != w || textBitmap.getHeight() != h) {
                textBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            }
            innerCanvas = new Canvas(textBitmap);
            innerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            innerStaticLayout.draw(innerCanvas);
            setBitmap(textBitmap);
            textNeedUpdate = false;
        }


        if (postionNeedUpdate) {
            updatePostion();
            postionNeedUpdate = false;
        }
    }

    public void setPostion(int gravity, int verticalMargin, int horizontalMargin) {
        this.gravity = gravity;
        if (this.verticalMargin != verticalMargin || this.horizontalMargin != horizontalMargin) {
            textNeedUpdate = true;
            this.verticalMargin = verticalMargin;
            this.horizontalMargin = horizontalMargin;
        }
        postionNeedUpdate = true;
    }

    private void updatePostion() {
        int v, h;
        int top, bottom, left, right;
        v = gravity & RBTextFilter.Gravity.VERTICAL_MASK;
        h = gravity & RBTextFilter.Gravity.HORIZONTA_MASK;
        switch (v) {
            case RBTextFilter.Gravity.CENTER_VERTICAL:
                top = (920 - textBitmap.getHeight()) / 2;
                bottom = top + textBitmap.getHeight();
                break;
            case RBTextFilter.Gravity.BOTTOM:
                bottom = 920 - verticalMargin;
                top = bottom - textBitmap.getHeight();
                break;
            case RBTextFilter.Gravity.TOP:
            default:
                top = verticalMargin;
                bottom = verticalMargin + textBitmap.getHeight();
                break;
        }
        switch (h) {
            case RBTextFilter.Gravity.CENTER_HORIZONTAL:
                left = (920 - textBitmap.getWidth()) / 2;
                right = left + textBitmap.getWidth();
                break;
            case RBTextFilter.Gravity.RIGHT:
                right = 920 - horizontalMargin;
                left = right - textBitmap.getWidth();
                break;
            case RBTextFilter.Gravity.LEFT:
            default:
                left = horizontalMargin;
                right = horizontalMargin + textBitmap.getWidth();
                break;
        }
        textRect.set(left, top, right, bottom);
    }

    public class Gravity {
        public static final int TOP = 0x0000;
        public static final int BOTTOM = 0x0001;
        public static final int CENTER_VERTICAL = 0x0010;
        public static final int VERTICAL_MASK = 0x0011;
        public static final int LEFT = 0x0000;
        public static final int RIGHT = 0x0100;
        public static final int CENTER_HORIZONTAL = 0x1000;
        public static final int HORIZONTA_MASK = 0x1100;
    }
}
