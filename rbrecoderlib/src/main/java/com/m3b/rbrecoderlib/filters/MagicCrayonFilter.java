package com.m3b.rbrecoderlib.filters;

import android.content.Context;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.OpenGLUtils2;
import com.m3b.rbrecoderlib.R;


public class MagicCrayonFilter extends GPUImageFilter {
	
	private int mSingleStepOffsetLocation;
	//1.0 - 5.0
	private int mStrength;
	private Context mContext;
	
	public MagicCrayonFilter(Context context){
		super(NO_FILTER_VERTEX_SHADER, OpenGLUtils2.readShaderFromRawResource(context, R.raw.crayon));
		mContext = context;
	}

    @Override
	public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrength = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrength, 2.0f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
	}
	
	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
