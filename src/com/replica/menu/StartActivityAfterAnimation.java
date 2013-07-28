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