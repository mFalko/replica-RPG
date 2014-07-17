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
 *		name changed to SnowBall.java from AndouKun.java
 */
 
package com.replica;



import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.replica.core.Game;
import com.replica.core.graphics.GLSurfaceView;
import com.replica.hud.UIConstants;
import com.replica.utility.DebugLog;
import com.replica.utility.EventReporter;

/**
 * @author matt
 * 
 */
public class SnowBall extends Activity {

	
	private static final int ROLL_TO_FACE_BUTTON_DELAY = 100;
    
    public static final int QUIT_GAME_DIALOG = 0;
	
	
	public static final int VERSION = 1;
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

//		int defaultWidth = 480;
//		int defaultHeight = 320;
		
		int defaultWidth = 600;
		int defaultHeight = 400;
		
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
		game_.getRenderer().onPause(); // hack!

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

	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	boolean result = true;
	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
				final long time = System.currentTimeMillis();
	    		if (time - mLastTouchTime > ROLL_TO_FACE_BUTTON_DELAY) {
	    			showDialog(QUIT_GAME_DIALOG);
	    			result = true;
	    		}
	    	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
	    		result = true;
	    		//TODO: implement pause overlay
	    		if (game_.isPaused()) {
//	    			hidePauseMessage();
//	    			game_.onResume(this, true);
	    		} else {
	    			final long time = System.currentTimeMillis();
	    	        if (time - mLastTouchTime > ROLL_TO_FACE_BUTTON_DELAY) {
//	    	        	showPauseMessage();
//	    	        	game_.onPause();
	    	        }
	    		}
	    	} else {
			    result = game_.onKeyDownEvent(keyCode);
			    // Sleep so that the main thread doesn't get flooded with UI events.
			    try {
			        Thread.sleep(4);
			    } catch (InterruptedException e) {
			        // No big deal if this sleep is interrupted.
			    }
	    	}
	        return result;
	    }
	     
	    @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event) {
	    	boolean result = false;
	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		result = true;
	    	} else if (keyCode == KeyEvent.KEYCODE_MENU){ 

	    	} else {
	    		result = game_.onKeyUpEvent(keyCode);
		        // Sleep so that the main thread doesn't get flooded with UI events.
		        try {
		            Thread.sleep(4);
		        } catch (InterruptedException e) {
		            // No big deal if this sleep is interrupted.
		        }
	    	}
	        return result;
	    }
	    
	    
	    
	    
	    @Override
	    protected Dialog onCreateDialog(int id) {
	        Dialog dialog = null;
	        if (id == QUIT_GAME_DIALOG) {
	        	//TODO: move strings to strings.xml
	        	
	            dialog = new AlertDialog.Builder(this)
	                .setTitle("Quit")
	                .setPositiveButton("Quit Game", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	finish();
	                    	if (UIConstants.mOverridePendingTransition != null) {
	         	 		       try {
	         	 		    	  UIConstants.mOverridePendingTransition.invoke(SnowBall.this, R.anim.activity_fade_in, R.anim.activity_fade_out);
	         	 		       } catch (InvocationTargetException ite) {
	         	 		           DebugLog.d("Activity Transition", "Invocation Target Exception");
	         	 		       } catch (IllegalAccessException ie) {
	         	 		    	   DebugLog.d("Activity Transition", "Illegal Access Exception");
	         	 		       }
	         	            }
	                    }
	                })
	                .setNegativeButton("Cancel", null)
	                .setMessage("Do you want to quit the game?")
	                .create();
	        }
	        return dialog;
	    }
	
	
}
