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

package com.replica.input;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.content.Intent;

import com.replica.R;
import com.replica.core.BaseObject;
import com.replica.hud.UIConstants;
import com.replica.menu.MainGameMenuActivity;
import com.replica.utility.DebugLog;

public class InputGameInterface extends BaseObject {

	// TODO: add in hard controls, currently only supports touch

	// touch controls
	private InputDPad dpad_ = new InputDPad();
	private InputTouchButton[] touchButtons_ = new InputTouchButton[ButtonConstants.TOTAL_BUTTON_COUNT];

	private static final float MENU_BUTTON_TOUCH_DELAY = 0.5f;

	public InputGameInterface() {
		super();
		for (int i = 0; i < ButtonConstants.TOTAL_BUTTON_COUNT; ++i) {
			touchButtons_[i] = new InputTouchButton();
		}
		reset();
	}

	@Override
	public void reset() {
		dpad_.reset();
		for (int i = 0; i < ButtonConstants.TOTAL_BUTTON_COUNT; ++i) {
			touchButtons_[i].reset();
		}
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		final float gameTime = sSystemRegistry.timeSystem.getGameTime();
		final float lastMenuPress = touchButtons_[ButtonConstants.MENU_BUTTON_INDEX]
				.getLastPressedTime();

		dpad_.update(gameTime, this);
		boolean buttonPressed = false;
		for (int i = 0; i < ButtonConstants.TOTAL_BUTTON_COUNT; ++i) {
			if (!buttonPressed) {
				touchButtons_[i].release();
				touchButtons_[i].update(gameTime, parent);
				buttonPressed = touchButtons_[i].pressed();
			} else {
				touchButtons_[i].release();
			}
		}

		boolean gotoMenu = touchButtons_[ButtonConstants.MENU_BUTTON_INDEX]
				.pressed();
		if (gotoMenu && gameTime - lastMenuPress > MENU_BUTTON_TOUCH_DELAY) {

			Context context = BaseObject.sSystemRegistry.contextParameters.context;
			Intent i = new Intent(context, MainGameMenuActivity.class);
			context.startActivity(i);

			if (UIConstants.mOverridePendingTransition != null) {
				try {
					UIConstants.mOverridePendingTransition.invoke(context,
							R.anim.activity_fade_in, R.anim.activity_fade_out);
				} catch (InvocationTargetException ite) {
					DebugLog.d("Activity Transition",
							"Invocation Target Exception");
				} catch (IllegalAccessException ie) {
					DebugLog.d("Activity Transition",
							"Illegal Access Exception");
				}
			}
		}
	}

	public void setDpadLocation(float x, float y, float width, float height) {
		dpad_.setBounds(x, y, height, width);
	}

	public void setGameButtonLocation(int button, float x, float y,
			float width, float height) {
		if (button < 0 || button >= ButtonConstants.GAME_BUTTON_COUNT) {
			// DebugLog.v("Input Interface", "Invalid Button Index");
			return;
		}
		touchButtons_[button].setBounds(x, y, width, height);
	}

	public void setMenuButtonLocation(float x, float y, float width,
			float height) {
		touchButtons_[ButtonConstants.MENU_BUTTON_INDEX].setBounds(x, y, width,
				height);
	}

	public InputDPad getDpad() {
		return dpad_;
	}

	public boolean getDpadPressed() {
		return dpad_ != null && dpad_.pressed();
	}

	public void setUseOnScreenControls(boolean onscreen) {
		// mUseOnScreenControls = onscreen;
	}

	public boolean getButtonPressed(int index) {
		if (index < 0 || index >= ButtonConstants.TOTAL_BUTTON_COUNT) {
			// DebugLog.v("Input Interface", "Invalid Button Index");
			return false;
		}
		return touchButtons_[index].pressed();
	}

	public int getGameButtonIndex(float x, float y) {
		int index = -1;
		for (int i = 0; i < ButtonConstants.GAME_BUTTON_COUNT; ++i) {
			if (touchButtons_[i].containsPoint(x, y)) {
				index = i;
				break;
			}
		}
		return index;
	}

}
