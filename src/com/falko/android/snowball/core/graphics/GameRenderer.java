/*
 * GameRenderer.java
 *
 * Copyright (C) 2012 Matt Falkoski
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.falko.android.snowball.core.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.SystemClock;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.ContextParameters;
import com.falko.android.snowball.core.Game;
import com.falko.android.snowball.core.ObjectManager;
import com.falko.android.snowball.core.systems.OpenGLSystem;
import com.falko.android.snowball.core.systems.RenderSystem.RenderElement;
import com.falko.android.snowball.utility.FixedSizeArray;

/**
 * @author matt
 * 
 */
public class GameRenderer implements GLSurfaceView.Renderer {

	private int width_;
	private int height_;
	private int halfWidth_;
	private int halfHeight_;
	private float scaleX_;
	private float scaleY_;
	private Context context_;
	private long lastTime_;
	private Game game_;
	private ObjectManager drawQueue_;
	private boolean drawQueueChanged_;
	private Object drawLock_;
	private float cameraX_;
	private float cameraY_;
	private boolean callbackRequested_;

	/**
	 * 
	 * @param context
	 * @param gameWidth
	 * @param gameHeight
	 */
	public GameRenderer(Context context, Game game, int gameWidth,
			int gameHeight) {
		context_ = context;
		game_ = game;
		width_ = gameWidth;
		height_ = gameHeight;
		halfWidth_ = gameWidth / 2;
		halfHeight_ = gameHeight / 2;
		scaleX_ = 1.0f;
		scaleY_ = 1.0f;
		drawQueueChanged_ = false;
		drawLock_ = new Object();
		cameraX_ = 0.0f;
		cameraY_ = 0.0f;
		drawQueue_ = null;
		callbackRequested_ = false;
	}

	/**
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10,
	 *      javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
		String version = gl.glGetString(GL10.GL_VERSION);
		String renderer = gl.glGetString(GL10.GL_RENDERER);
		boolean isSoftwareRenderer = renderer.contains("PixelFlinger");
		boolean isOpenGL10 = version.contains("1.0");
		boolean supportsDrawTexture = extensions.contains("draw_texture");
		// VBOs are standard in GLES1.1
		// No use using VBOs when software renderering, esp. since older
		// versions of the software renderer
		// had a crash bug related to freeing VBOs.
		boolean supportsVBOs = !isSoftwareRenderer
				&& (!isOpenGL10 || extensions.contains("vertex_buffer_object"));
		ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
		params.supportsDrawTexture = supportsDrawTexture;
		params.supportsVBOs = supportsVBOs;

		game_.onSurfaceCreated(gl, context_);
	}

	/**
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10,
	 *      int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		float scaleX = (float) width / width_;
		float scaleY = (float) height / height_;
		final int viewportWidth = (int) (width_ * scaleX);
		final int viewportHeight = (int) (height_ * scaleY);
		gl.glViewport(0, 0, viewportWidth, viewportHeight);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, viewportWidth, 0.0f, viewportHeight, 0.0f, 1.0f);
		scaleX_ = scaleX;
		scaleY_ = scaleY;

		float ratio = (float) width_ / height_;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

		game_.onSurfaceReady();

	}

	/**
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {

		long time = SystemClock.uptimeMillis();
		long time_delta = (time - lastTime_);

		synchronized (drawLock_) {
			if (!drawQueueChanged_) {
				while (!drawQueueChanged_) {
					try {
						drawLock_.wait();
					} catch (InterruptedException e) {
						// No big deal if this wait is interrupted.
					}
				}
			}
			drawQueueChanged_ = false;
		}

		if (callbackRequested_) {
			game_.onSurfaceReady();
			callbackRequested_ = false;
		}

		DrawableBitmap.beginDrawing(gl, width_, height_);
		
		synchronized (this) {
            if (drawQueue_ != null && drawQueue_.getObjects().getCount() > 0) {
                OpenGLSystem.setGL(gl);
                FixedSizeArray<BaseObject> objects = drawQueue_.getObjects();
                Object[] objectArray = objects.getArray();
                final int count = objects.getCount();
                final float scaleX = scaleX_;
                final float scaleY = scaleY_;
                final float halfWidth = halfWidth_;
                final float halfHeight = halfHeight_;
                for (int i = 0; i < count; i++) {
                    RenderElement element = (RenderElement)objectArray[i];
                    if (element == null) continue;
                    float x = element.x;
                    float y = element.y;
                    if (element.cameraRelative) {
                    	x = (x - cameraX_) + halfWidth;
                    	y = (y - cameraY_) + halfHeight;
                    }
                    element.mDrawable.draw(x, y, scaleX, scaleY);
                }
                OpenGLSystem.setGL(null);
            } else if (drawQueue_ == null) {
                // If we have no draw queue, clear the screen.  If we have a draw queue that
                // is empty, we'll leave the frame buffer alone.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }
        }


		DrawableBitmap.endDrawing(gl);
	}

	public void setDrawQueue(ObjectManager objectManager, float cameraX,
			float cameraY) {
		drawQueue_ = objectManager;
		cameraX_ = cameraX;
		cameraY_ = cameraY;
    	synchronized(drawLock_) {
    		drawQueueChanged_ = true;
    		drawLock_.notify();
    	}

	}
	
	public synchronized void onPause() {
    	// Stop waiting to avoid deadlock.
    	// TODO: this is a hack.  Probably this renderer
    	// should just use GLSurfaceView's non-continuious render
    	// mode.
    	synchronized(drawLock_) {
    		drawQueueChanged_ = true;
    		drawLock_.notify();
    	}
    }

	public void setContext(Context context) {
		context_ = context;
	}

	public synchronized void waitDrawingComplete() {
	}

}
