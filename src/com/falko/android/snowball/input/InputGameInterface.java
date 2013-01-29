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



	public InputGameInterface() {
		super();
		reset();
	}

	@Override
	public void reset() {
		mJumpButton.release();
		mAttackButton.release();
		mDirectionalPad.release();

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		InputSystem input = sSystemRegistry.inputSystem;

		final InputTouchScreen touch = input.getTouchScreen();
		final float gameTime = sSystemRegistry.timeSystem.getGameTime();

		
		
		final InputXY dapdTouch = touch.findPointerInRegion(
				40, 20, 100, 100);
		
		if (dapdTouch != null) {
			
			final int centerX = 80;
			final int centerY = 60;
			
			int deltaX = dapdTouch.getX() - centerX > 40? 5 : dapdTouch.getX() - centerX < -40? -5 : 0;
			int deltaY = dapdTouch.getY() - centerY > 30? 5 : dapdTouch.getY() - centerY < -30? -5 : 0;
			mDirectionalPad.press(dapdTouch.getLastPressedTime(),deltaX, deltaY);

			Log.v("SNowBAll", "Touch Dpad");
			
		} else {
			mDirectionalPad.release();
		}
		
	
	}

	public final InputXY getDirectionalPad() {
		return mDirectionalPad;
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
