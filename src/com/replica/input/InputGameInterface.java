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

import com.replica.core.BaseObject;
import com.replica.utility.DebugLog;

public class InputGameInterface extends BaseObject {
	
	
	private int pressedButtonIndex_;

	//TODO: add in hard controls, currently only supports touch
	
	//touch controls
	private InputDPad dpad_ = new InputDPad();
	private InputTouchButton[] touchButtons_ = new InputTouchButton[ButtonConstants.TOTAL_BUTTON_COUNT];

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

		dpad_.update(gameTime, this);
		boolean buttonPressed = false;
		for (int i = 0; i < ButtonConstants.TOTAL_BUTTON_COUNT; ++i) {
			if (!buttonPressed) {
				touchButtons_[i].update(gameTime, parent);
				buttonPressed = touchButtons_[i].pressed();
				pressedButtonIndex_ = buttonPressed? i : -1;
			} else {
				touchButtons_[i].release();
			}	
		}
		
		if (pressedButtonIndex_ == ButtonConstants.MENU_BUTTON_INDEX) {
			//TODO: switch game state to menu
		}
	}
	
	public void setDpadLocation(float x, float y, float width, float height) {
		dpad_.setBounds(x, y, height, width);
	}
	
	public void setGameButtonLocation(int button, float x, float y, float width, float height) {
		if (button < 0 || button >= ButtonConstants.GAME_BUTTON_COUNT) {
			DebugLog.v("Input Interface", "Invalid Button Index");
			return;
		}
		touchButtons_[button].setBounds(x, y, width, height);
	}
	
	public void setMenuButtonLocation(float x, float y, float width, float height) {
		touchButtons_[ButtonConstants.MENU_BUTTON_INDEX].setBounds(x, y, width, height);
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

	public boolean getMenuButtonPressed() {
		return touchButtons_[ButtonConstants.MENU_BUTTON_INDEX].pressed();
	}
	
	public int getGameButtonPressed() {
		return pressedButtonIndex_;
	}
	
	public boolean getButtonPressed(int index) {
		if (index < 0 || index >= ButtonConstants.TOTAL_BUTTON_COUNT) {
			DebugLog.v("Input Interface", "Invalid Button Index");
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
