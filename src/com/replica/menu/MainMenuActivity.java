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
 

package com.replica.menu;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.replica.PreferenceConstants;
import com.replica.R;
import com.replica.SnowBall;

public class MainMenuActivity extends Activity {

	
	private boolean mPaused;
    private View mNewGameButton;
    private View mLoadGameButton;
    private View mOptionsButton;
    private View mExitButton;
    
    private View mBackground;
    private View mTicker;
    
    private Animation mButtonFlickerAnimation;
    private Animation mFadeOutAnimation;
    private Animation mAlternateFadeOutAnimation;
    
    private boolean mJustCreated;
	
    
    private View.OnClickListener sNewGameButtonListener = new View.OnClickListener() {
    	
    	private Animation.AnimationListener flikerAnimationListener = new Animation.AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				mButtonFlickerAnimation.setAnimationListener(null);
				mBackground.startAnimation(mFadeOutAnimation);
				mNewGameButton.startAnimation(mAlternateFadeOutAnimation);
				mLoadGameButton.startAnimation(mAlternateFadeOutAnimation);
				mOptionsButton.startAnimation(mAlternateFadeOutAnimation);
				mExitButton.startAnimation(mAlternateFadeOutAnimation);
				mTicker.startAnimation(mAlternateFadeOutAnimation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
        	
        };
    	
    	
        public void onClick(View v) {
            if (!mPaused) {
                Intent i = new Intent(getBaseContext(), SnowBall.class);
                mButtonFlickerAnimation.setAnimationListener(flikerAnimationListener);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(MainMenuActivity.this, i));
                v.startAnimation(mButtonFlickerAnimation);

                mPaused = true;
            }
        }
    };
    
    private View.OnClickListener sOptionButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
//                Intent i = new Intent(getBaseContext(), SetPreferencesActivity.class);
//                v.startAnimation(mButtonFlickerAnimation);
//                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
//                mBackground.startAnimation(mFadeOutAnimation);
//                mStartButton.startAnimation(mAlternateFadeOutAnimation);
//                mExtrasButton.startAnimation(mAlternateFadeOutAnimation);
//                mTicker.startAnimation(mAlternateFadeOutAnimation);
//                mPaused = true;
            }
        }
    };
    
    private View.OnClickListener sExitButtonListener = new View.OnClickListener() {
    	
    	
    	private Animation.AnimationListener flikerAnimationListener = new Animation.AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				mButtonFlickerAnimation.setAnimationListener(null);
				mBackground.startAnimation(mFadeOutAnimation);
				mNewGameButton.startAnimation(mAlternateFadeOutAnimation);
				mLoadGameButton.startAnimation(mAlternateFadeOutAnimation);
				mOptionsButton.startAnimation(mAlternateFadeOutAnimation);
				mExitButton.startAnimation(mAlternateFadeOutAnimation);
				mTicker.startAnimation(mAlternateFadeOutAnimation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
        	
        };
    	
        public void onClick(View v) {

            if (!mPaused) {
            	mButtonFlickerAnimation.setAnimationListener(flikerAnimationListener);
                mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						mPaused = true;
		                finish();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {	
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
          
                });
                v.startAnimation(mButtonFlickerAnimation);
            }
        }
    };
    
    private View.OnClickListener sLoadGameButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
//            	Intent i = new Intent(getBaseContext(), DifficultyMenuActivity.class);
//            	i.putExtra("newGame", true);
//                v.startAnimation(mButtonFlickerAnimation);
//                mButtonFlickerAnimation.setAnimationListener(new StartActivityAfterAnimation(i));

//                mPaused = true;
                
            }
        }
    };
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_main);
		
		
		mPaused = true;

		mNewGameButton = findViewById(R.id.newGameButton);
		mNewGameButton.setOnClickListener(sNewGameButtonListener);
		
		mLoadGameButton = findViewById(R.id.loadGameButton);
		mLoadGameButton.setOnClickListener(sLoadGameButtonListener);
		
		mOptionsButton = findViewById(R.id.mainOptionButton);
		mOptionsButton.setOnClickListener(sOptionButtonListener);

		mExitButton = findViewById(R.id.exitButton);
		mExitButton.setOnClickListener(sExitButtonListener);
		
		mBackground = findViewById(R.id.mainMenuBackground);

		mButtonFlickerAnimation = AnimationUtils.loadAnimation(this, R.anim.button_flicker);
		mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);

		
		//TODO: init zone factory here?
		

		mTicker = findViewById(R.id.ticker);
		if (mTicker != null) {
			mTicker.setFocusable(true);
			mTicker.requestFocus();
			mTicker.setSelected(true);
		}

		mJustCreated = true;
		
	}

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        
        mButtonFlickerAnimation.setAnimationListener(null);
        
  
        SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		final int lastVersion = prefs.getInt(PreferenceConstants.PREFERENCE_LAST_VERSION, 0);
		if (lastVersion == 0) {
			// This is the first time the game has been run.
			// Pre-configure the control options to match the device.
		}

		// TODO: update / new install code goes here
		// check version, show dialog
		// check play count and ask for money
            
        
      
        
        if (mBackground != null) {
        	mBackground.clearAnimation();
        }
   
        if (mJustCreated) {
        	
        	if (mNewGameButton != null) {
        		mNewGameButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_slide));
            }
        	
        	if (mLoadGameButton != null) {
        		Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
                anim.setStartOffset(500L);
        		mLoadGameButton.startAnimation(anim);
            }
            
            if (mOptionsButton != null) {
            	Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
                anim.setStartOffset(1000L);
                mOptionsButton.startAnimation(anim);
            }
            
            if (mExitButton != null) {
            	Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
                anim.setStartOffset(1500L);
                mExitButton.startAnimation(anim);
            }
            
            mJustCreated = false;
            
        } else {
        	mNewGameButton.clearAnimation();
        	mLoadGameButton.clearAnimation();
        	mOptionsButton.clearAnimation();
        	mExitButton.clearAnimation();
        }
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
