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

public class InputGameInterface extends BaseObject {

	private InputDPad dpad_ = new InputDPad();

	public InputGameInterface() {
		super();
		reset();
	}

	@Override
	public void reset() {
		dpad_.reset();
	}

	public void setDpadLocation(float x, float y, float width, float height) {
		dpad_.setBounds(x, y, height, width);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		final float gameTime = sSystemRegistry.timeSystem.getGameTime();

		dpad_.update(gameTime, this);
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

}
