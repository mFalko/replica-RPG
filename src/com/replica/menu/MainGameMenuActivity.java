/*
 * Copyright (C) 2010 Matthew Falkoski
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

package com.replica.menu;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.replica.R;
import com.replica.SnowBall;
import com.replica.hud.UIConstants;
import com.replica.utility.DebugLog;

public class MainGameMenuActivity extends Activity {

	private boolean mPaused;


	private void gotoActivity(Intent intent) {
		mPaused = true;
		startActivity(intent);
		if (UIConstants.mOverridePendingTransition != null) {
			try {
				UIConstants.mOverridePendingTransition.invoke(this, R.anim.activity_fade_in, R.anim.activity_fade_out);
			} catch (InvocationTargetException ite) {
				DebugLog.d("Activity Transition", "Invocation Target Exception");
			} catch (IllegalAccessException ie) {
				DebugLog.d("Activity Transition", "Illegal Access Exception");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_game_main);

		final Context context = getBaseContext();
		
		findViewById(R.id.mainMenuBackButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuBackButtonImage) {
					Intent intent = new Intent(context, SnowBall.class);
					@Override
					protected void onClick() {
						if (!mPaused) {
							gotoActivity(intent);		
						}
					}
			});
		
		findViewById(R.id.mainMenuCharacterButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuCharacterButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
									
						}
					}
			});
		
		findViewById(R.id.mainMenuSkillsButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuSkillsButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
										
						}
					}
			});
		
		findViewById(R.id.mainMenuInventoryButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuInventoryButtonImage) {
					Intent intent = new Intent(context, InventoryActivity.class);
					@Override
					protected void onClick() {
						if (!mPaused) {
							gotoActivity(intent);			
						}
					}
			});
		
		findViewById(R.id.mainMenuQuestLogButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuQuestLogButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
									
						}
					}
			});
		
		findViewById(R.id.mainMenuSettingsButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuSettingsButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
										
						}
					}
			});
		
		findViewById(R.id.mainMenuSaveButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuSaveButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
										
						}
					}
			});
		
		findViewById(R.id.mainMenuStoreButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuStoreButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
										
						}
					}
			});
		
		findViewById(R.id.mainMenuExitButton).setOnTouchListener(
				new ButtonTouchListener(R.id.mainMenuExitButtonImage) {
					@Override
					protected void onClick() {
						if (!mPaused) {
										
						}
					}
			});
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private abstract class ButtonTouchListener implements OnTouchListener {

		private int mViewID;

		public ButtonTouchListener(int id) {
			mViewID = id;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean handled = true;
			ImageView image = (ImageView) v.findViewById(mViewID);

			switch (event.getActionMasked()) {

			case MotionEvent.ACTION_DOWN:
				image.setImageResource(R.drawable.button_large_down);
				break;

			case MotionEvent.ACTION_UP:
				image.setImageResource(R.drawable.button_large_up);
				onClick();
				break;

			default:
				handled = false;
			}
			if (handled) {
				image.invalidate();
			}
			return handled;
		}
		
		protected abstract void onClick();
	}
	
	

}
