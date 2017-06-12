package com.m3b.rbrecoderlib.filters;

import android.content.Context;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.OpenGLUtils2;
import com.m3b.rbrecoderlib.R;


public class MagicXproIIFilter extends GPUImageFilter {
	private int[] inputTextureHandles = {-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1};
	private Context mContext;
	
	public MagicXproIIFilter(Context context){
		super(NO_FILTER_VERTEX_SHADER, OpenGLUtils2.readShaderFromRawResource(context, R.raw.xproii_filter_shader));
		mContext = context;
	}
	
	public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGLUtils2.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0+i);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGLUtils2.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0+i);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], i+1);
		}
	}
	
	public void onInit(){
		super.onInit();
		inputTextureHandles[0] = OpenGLUtils2.loadTexture(mContext, "filter/xpromap.png");
		inputTextureHandles[1] = OpenGLUtils2.loadTexture(mContext, "filter/vignettemap_new.png");
	}
}
