package com.m3b.rbrecoderlib.filters;

import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;

/**
 * Created by m3b on 1/11/16.
 * sigma = 0.1.Optimized Selective Gaussian Blur.
 */
public class SkinBlurHardVideoFilter extends GPUImageFilter {

    private static String FRAGMENTSHADER = "" +
            "precision mediump float;\n" +
            "uniform sampler2D uCamTexture;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "const float maxdelta = 0.08;\n" +
            "uniform mediump float xStep;\n" +
            "uniform mediump float yStep;\n" +
            "const mediump mat3 rgb2yuv = mat3(0.299,-0.147,0.615,0.587,-0.289,-0.515,0.114,0.436,-0.1);\n" +
            "const mediump mat3 gaussianMap = mat3(0.142,0.131,0.104,0.131,0.122,0.096,0.104,0.096,0.075);\n" +
            "void main(){\n" +
            "    vec4 color = texture2D(uCamTexture,textureCoordinate);\n" +
            "    vec3 yuv = rgb2yuv*color.rgb;\n" +
            "    if(yuv.g<-0.225 || yuv.g>0.0 || yuv.b<0.022 || yuv.b>0.206){\n" +
            "        gl_FragColor = color;\n" +
            "        return;\n" +
            "    }\n" +
            "    float xfS = textureCoordinate.x - xStep*2.0;\n" +
            "    float yf = textureCoordinate.y - yStep*2.0;\n" +
            "    int x,y;\n" +
            "    float xf=xfS;\n" +
            "    vec4 sum=vec4(0.0,0.0,0.0,0.0);\n" +
            "    vec4 fact=vec4(0.0,0.0,0.0,0.0);\n" +
            "    vec4 tmp;\n" +
            "    vec4 color2;\n" +
            "    float gauss;\n" +
            "    for(y=-2;y<3;y+=1){\n" +
            "        if (yf < 0.0 || yf > 1.0){\n" +
            "            yf+=yStep;\n" +
            "            continue;\n" +
            "        }\n" +
            "        for(x=-2;x<3;x+=1){\n" +
            "            if (xf < 0.0 || xf > 1.0){\n" +
            "                xf+=xStep;\n" +
            "                continue;\n" +
            "            }\n" +
            "            color2 = texture2D(uCamTexture,vec2(xf,yf));\n" +
            "            tmp = color - color2;\n" +
            "            gauss = gaussianMap[x<0?-x:x][y<0?-y:y];\n" +
            "            if (abs(tmp.r) < maxdelta){\n" +
            "                sum.r += (color2.r*gauss);\n" +
            "                fact.r +=gauss;\n" +
            "            }\n" +
            "            if (abs(tmp.g) < maxdelta){\n" +
            "                sum.g += color2.g*gauss;\n" +
            "                fact.g +=gauss;\n" +
            "            }\n" +
            "            if (abs(tmp.b) < maxdelta){\n" +
            "                sum.b += color2.b*gauss;\n" +
            "                fact.b +=gauss;\n" +
            "            }\n" +
            "            xf+=xStep;\n" +
            "        }\n" +
            "        yf+=yStep;\n" +
            "        xf=xfS;\n" +
            "    }\n" +
            "    vec4 res = sum/fact;\n" +
            "    if(fact.r<1.0){\n" +
            "        tmp.r = color.r;\n" +
            "    }else{\n" +
            "        tmp.r = res.r;\n" +
            "    }\n" +
            "    if(fact.g<1.0){\n" +
            "        tmp.g = color.g;\n" +
            "    }else{\n" +
            "        tmp.g = res.g;\n" +
            "    }\n" +
            "    if(fact.b<1.0){\n" +
            "        tmp.b = color.b;\n" +
            "    }else{\n" +
            "        tmp.b = res.b;\n" +
            "    }\n" +
            "    gl_FragColor = vec4(tmp.rgb,1.0);\n" +
            "}\n";


    private int xStepLoc;
    private int yStepLoc;
    private float stepScale;

    /**
     * @param stepScale suggest:480P = 2,720P = 3
     */
    public SkinBlurHardVideoFilter(int stepScale) {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENTSHADER);
        this.stepScale = (float) stepScale;
    }

    @Override
    public void onInit() {
        yStepLoc = GLES20.glGetUniformLocation(getProgram(), "yStep");
        xStepLoc = GLES20.glGetUniformLocation(getProgram(), "xStep");
    }

    @Override
    public void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glUniform1f(xStepLoc, (float) (stepScale / (float) getOutputWidth()));
        GLES20.glUniform1f(yStepLoc, (float) (stepScale / (float) getOutputHeight()));
    }

    public void setStepScale( float stepScale){
        this.stepScale = (float) stepScale;

    }
}
