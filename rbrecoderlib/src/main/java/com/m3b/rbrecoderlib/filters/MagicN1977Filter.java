package com.m3b.rbrecoderlib.filters;

import android.content.Context;
import android.opengl.GLES20;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.OpenGLUtils2;
import com.m3b.rbrecoderlib.R;


public class MagicN1977Filter extends GPUImageFilter {
	private int[] inputTextureHandles = {-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1};
	private Context mContext;
	
	public MagicN1977Filter(Context context){
		super(NO_FILTER_VERTEX_SHADER, OpenGLUtils2.readShaderFromRawResource(context, R.raw.n1977));
		mContext = context;
	}

	@Override
	public void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGLUtils2.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	@Override
	public void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGLUtils2.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	@Override
	public void onInit(){
		super.onInit();
		for(int i=0; i < inputTextureUniformLocations.length; i++){
			inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
		}
	}
	
	@Override
	public void onInitialized(){
		super.onInitialized();
	    runOnDraw(new Runnable(){
		    public void run(){
		    	inputTextureHandles[0] = OpenGLUtils2.loadTexture(mContext, "filter/n1977map.png");
				inputTextureHandles[1] = OpenGLUtils2.loadTexture(mContext, "filter/n1977blowout.png");
		    }
	    });
	}
}
