/*
 * Pinhead.java
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

package com.replica;



import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.replica.core.Game;
import com.replica.core.graphics.GLSurfaceView;
import com.replica.utility.DebugLog;
import com.replica.utility.EventReporter;

/**
 * @author matt
 * 
 */
public class SnowBall extends Activity {

	public static final int VERSION = 14;
	private Game game_;
	GLSurfaceView GLView_;
	private long mLastTouchTime = 0L;
	private EventReporter eventReporter_ = null;
	private Thread eventReporterThread_ = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		GLView_ = (GLSurfaceView) findViewById(R.id.glsurfaceview);
		GLView_.setEGLConfigChooser(false); // 16 bit, no z-buffer
		GLView_.setKeepScreenOn(true);
		GLView_.setPreserveEGLContextOnPause(true);
		game_ = new Game();
		game_.setSurfaceView(GLView_);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int defaultWidth = 480;
		int defaultHeight = 320;
		if (dm.widthPixels != defaultWidth) {
			float ratio = ((float) dm.widthPixels) / dm.heightPixels;
			defaultWidth = (int) (defaultHeight * ratio);
		}

		game_.bootstrap(this, dm.widthPixels, dm.heightPixels, defaultWidth,
				defaultHeight);
		GLView_.setRenderer(game_.getRenderer());
		
		game_.start();

//		eventReporter_ = new EventReporter();
//		eventReporterThread_ = new Thread(eventReporter_);
//		eventReporterThread_.setName("EventReporter");
//		eventReporterThread_.start();
	}

	@Override
	protected void onDestroy() {
		game_.stop();
		if (eventReporterThread_ != null) {
			eventReporter_.stop();
			try {
				eventReporterThread_.join();
			} catch (InterruptedException e) {
				eventReporterThread_.interrupt();
			}
		}

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		DebugLog.d("AndouKun", "onPause");


		game_.onPause();
		GLView_.onPause();
//		game_.getRenderer().onPause(); // hack!

//		if (mMethodTracing) {
//			Debug.stopMethodTracing();
//			mMethodTracing = false;
//		}
//		if (mSensorManager != null) {
//			mSensorManager.unregisterListener(this);
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		DebugLog.d("AndouKun", "onResume");
		GLView_.onResume();
        game_.onResume(this, false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!game_.isPaused()) {
			game_.onTouchEvent(event);
	    	
	        final long time = System.currentTimeMillis();
	        if (event.getAction() == MotionEvent.ACTION_MOVE && time - mLastTouchTime < 32) {
		        // Sleep so that the main thread doesn't get flooded with UI events.
		        try {
		            Thread.sleep(32);
		        } catch (InterruptedException e) {
		            // No big deal if this sleep is interrupted.
		        }
		        game_.getRenderer().waitDrawingComplete();
	        }
	        mLastTouchTime = time;
    	}
        return true;
	}

}
