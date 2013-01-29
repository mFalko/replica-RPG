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

package com.falko.android.snowball.input;

import android.util.Log;

import com.falko.android.snowball.core.BaseObject;

public class InputGameInterface extends BaseObject {

	private InputButton mJumpButton = new InputButton();
	private InputButton mAttackButton = new InputButton();
	
	private InputXY mDirectionalPad = new InputXY();

	private InputButton dpadUpButton = new InputButton();
	private InputButton dpadDownButton = new InputButton();
	private InputButton dpadLeftButton = new InputButton();
	private InputButton dpadRightButton = new InputButton();

	public InputGameInterface() {
		super();
		reset();
	}

	@Override
	public void reset() {
		mJumpButton.release();
		mAttackButton.release();
		mDirectionalPad.release();
		
		dpadUpButton.release();
		dpadDownButton.release();
		dpadLeftButton.release();
		dpadRightButton.release();
		
		
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		InputSystem input = sSystemRegistry.inputSystem;

		final InputTouchScreen touch = input.getTouchScreen();
		final float gameTime = sSystemRegistry.timeSystem.getGameTime();

		
		
		final InputXY dpadTouch = touch.findPointerInRegion(
				40, 20, 100, 100);
		
		if (dpadTouch != null) {
			
			float touchx = dpadTouch.getX() - 40;
			float touchy = dpadTouch.getY() - 20;
			
			if (touchy >= 70f && (touchx >= 30f && touchx <= 70f)) {
				dpadUpButton.press(dpadTouch.getLastPressedTime(), 1);
			} else {
				dpadUpButton.release();
			}
			
			if (touchy <= 30f && (touchx >= 30f && touchx <= 70f)) {
				dpadDownButton.press(dpadTouch.getLastPressedTime(), 1);
			} else {
				dpadDownButton.release();
			}
			
			if (touchx <= 30f && (touchy >= 30f && touchy <= 70f)) {
				dpadLeftButton.press(dpadTouch.getLastPressedTime(), 1);
			} else {
				dpadLeftButton.release();
			}
			
			if (touchx >= 70f && (touchy >= 30f && touchy <= 70f)) {
				dpadRightButton.press(dpadTouch.getLastPressedTime(), 1);
			} else {
				dpadRightButton.release();
			}
	
		} else {
			dpadUpButton.release();
			dpadDownButton.release();
			dpadLeftButton.release();
			dpadRightButton.release();
		}
		
	
	}

	public final InputXY getDirectionalPad() {
		return mDirectionalPad;
	}
	
	public final InputButton getDpadUpButton() {
		return dpadUpButton;
	}

	public final InputButton getDpadDownButton() {
		return dpadDownButton;
	}
	
	public final InputButton getDpadLeftButton() {
		return dpadLeftButton;
	}
	
	public final InputButton getDpadRightButton() {
		return dpadRightButton;
	}
	
	public final InputButton getJumpButton() {
		return mJumpButton;
	}

	public final InputButton getAttackButton() {
		return mAttackButton;
	}


	public void setUseOnScreenControls(boolean onscreen) {
		// mUseOnScreenControls = onscreen;
	}

}
