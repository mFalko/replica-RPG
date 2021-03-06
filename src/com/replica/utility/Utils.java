/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/*
 * This file has been modified from the original.
 * 
 * The original file can be found at:
 *		https://code.google.com/p/replicaisland/
 */
 
package com.replica.utility;

import java.lang.reflect.Field;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import com.replica.core.graphics.Texture;

public class Utils {

	public static int getResId(String variableName, Context context, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(variableName);
			return idField.getInt(idField);
		} catch (Exception e) {
			Log.e("ERROR REPLICA", e.toString());
			return -1;
		}
	}

	private static int mTextureNameWorkspace[] = new int[1];
	public static Texture loadTexture(GL10 gl, Bitmap bitmap, Texture texture) {

		assert gl != null;
		if (texture.loaded) {
			return texture;
		}

		gl.glGenTextures(1, mTextureNameWorkspace, 0);

		int error = gl.glGetError();
		assert error == GL10.GL_NO_ERROR;

		int textureName = mTextureNameWorkspace[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

		error = gl.glGetError();
		assert error == GL10.GL_NO_ERROR;

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE); // GL10.GL_REPLACE);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		error = gl.glGetError();
		assert error == GL10.GL_NO_ERROR;

		int mCropWorkspace[] = new int[4];

		mCropWorkspace[0] = 0;
		mCropWorkspace[1] = bitmap.getHeight();
		mCropWorkspace[2] = bitmap.getWidth();
		mCropWorkspace[3] = -bitmap.getHeight();

		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
				GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

		texture.name = textureName;
		texture.width = bitmap.getWidth();
		texture.height = bitmap.getHeight();

		error = gl.glGetError();
		assert error == GL10.GL_NO_ERROR;

		texture.loaded = true;
		
		return texture;
	}
	
	public static void deleteTexture(GL10 gl, Texture texture) {
		if (texture.resource != -1 && texture.loaded) {
        	assert texture.name != -1;
            mTextureNameWorkspace[0] = texture.name;
            texture.name = -1;
            texture.loaded = false;
            gl.glDeleteTextures(1, mTextureNameWorkspace, 0);
            int error = gl.glGetError();
            assert error == GL10.GL_NO_ERROR;
        }
	}
	
	private static final float EPSILON = 0.0001f;

    public final static boolean close(float a, float b) {
        return close(a, b, EPSILON);
    }

    public final static boolean close(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    public final static int sign(float a) {
        if (a >= 0.0f) {
            return 1;
        } else {
            return -1;
        }
    }
    
    public final static int clamp(int value, int min, int max) {
        int result = value;
        if (min == max) {
            if (value != min) {
                result = min;
            }
        } else if (min < max) {
            if (value < min) {
                result = min;
            } else if (value > max) {
                result = max;
            }
        } else {
            result = clamp(value, max, min);
        }
        
        return result;
    }
   
    
    public final static int byteArrayToInt(byte[] b) {
        if (b.length != 4) {
            return 0;
        }

        // Same as DataInputStream's 'readInt' method
        /*int i = (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) 
                | (b[3] & 0xff));*/
        
        // little endian
        int i = (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) 
                | (b[0] & 0xff));
    
        return i;
    }
    
    public final static float byteArrayToFloat(byte[] b) {
        
        // intBitsToFloat() converts bits as follows:
        /*
        int s = ((i >> 31) == 0) ? 1 : -1;
        int e = ((i >> 23) & 0xff);
        int m = (e == 0) ? (i & 0x7fffff) << 1 : (i & 0x7fffff) | 0x800000;
        */
    
        return Float.intBitsToFloat(byteArrayToInt(b));
    }
    
    public final static float framesToTime(int framesPerSecond, int frameCount) {
        return (1.0f / framesPerSecond) * frameCount;
    }

}
