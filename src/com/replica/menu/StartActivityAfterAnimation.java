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
//TODO: This class was pulled from another class in Replica Island...but where?
/*
 * This file has been modified from the original.
 * 
 * The original file can be found at:
 *		https://code.google.com/p/replicaisland/
 */


package com.replica.menu;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Intent;
import android.view.animation.Animation;

import com.replica.R;
import com.replica.hud.UIConstants;
import com.replica.utility.DebugLog;

class StartActivityAfterAnimation implements Animation.AnimationListener {
    /**
	 * 
	 */
	private Activity mActivity;
	private Intent mIntent;
    
    StartActivityAfterAnimation(Activity activity, Intent intent) {
    	mActivity = activity;
		mIntent = intent;
    }
        
    public void onAnimationEnd(Animation animation) {
    	mActivity.startActivity(mIntent);      
        
        if (UIConstants.mOverridePendingTransition != null) {
	       try {
	    	   UIConstants.mOverridePendingTransition.invoke(mActivity, R.anim.activity_fade_in, R.anim.activity_fade_out);
	       } catch (InvocationTargetException ite) {
	           DebugLog.d("Activity Transition", "Invocation Target Exception");
	       } catch (IllegalAccessException ie) {
	    	   DebugLog.d("Activity Transition", "Illegal Access Exception");
	       }
        }
    }

    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
        
    }

    public void onAnimationStart(Animation animation) {
    }

	public void setIntent(Intent i) {
		mIntent = i;
	}  
}