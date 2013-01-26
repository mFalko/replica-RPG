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

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.ContextParameters;

public class InputGameInterface extends BaseObject {

	private InputButton mJumpButton = new InputButton();
	private InputButton mAttackButton = new InputButton();
	
	private InputXY mDirectionalPad = new InputXY();

	private InputButton scrollRightButton = new InputButton();
	private InputButton scrollLeftButton = new InputButton();

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

		ContextParameters params = sSystemRegistry.contextParameters;
		float scrollRightRegionX = params.gameWidth - 100;
		float scrollRightWidthX = 100;
		float scrollleftRegionX = 0;
		float scrollLeftWidthX = 100;

		final InputXY scrollRightTouch = touch.findPointerInRegion(
				scrollRightRegionX, 0, scrollRightWidthX, params.gameHeight);

		final InputXY scrollLeftTouch = touch.findPointerInRegion(
				scrollleftRegionX, 0, scrollLeftWidthX, params.gameHeight);

		if (scrollRightTouch != null) {
			scrollRightButton.press(scrollRightTouch.getLastPressedTime(), 1);
		} else {
			scrollRightButton.release();
		}

		if (scrollLeftTouch != null) {
			scrollLeftButton.press(scrollLeftTouch.getLastPressedTime(), 1);
		} else {
			scrollLeftButton.release();
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

	public final InputButton getSLButton() {
		return scrollLeftButton;
	}

	public final InputButton getSRButton() {
		return scrollRightButton;
	}

	public void setUseOnScreenControls(boolean onscreen) {
		// mUseOnScreenControls = onscreen;
	}

}
