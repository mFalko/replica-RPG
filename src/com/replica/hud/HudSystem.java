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

package com.replica.hud;

import com.replica.core.BaseObject;
import com.replica.core.GameObjectManager;

/**
 * A very simple manager for orthographic in-game UI elements. TODO: This should
 * probably manage a number of hud objects in keeping with the component-centric
 * architecture of this engine. The current code is monolithic and should be
 * refactored.
 */
public class HudSystem extends BaseObject {

	//TODO: extend objectManager instead of baseObject
	
	public HudSystem() {
		super();
		for (int i = 0; i < attackButtons_.length; ++i) {
			attackButtons_[i] = new HUDVirtualButton();
		}

		reset();
	}

	@Override
	public void reset() {

		dpad.reset();

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		final GameObjectManager manager = sSystemRegistry.gameObjectManager;

		if (manager != null && manager.getPlayer() != null) {
			// Only draw player-specific HUD elements when there's a player.

		}

		if (firstRun_) {
			init();
			firstRun_ = false;
		}

		if (useTouchInterface_) {

			dpad.update(timeDelta, this);
//			for (int i = 0; i < attackButtons_.length; ++i) {
				attackButtons_[0].update(timeDelta, this);
//			}
			
			
		}

	}

	private void init() {
		dpad.init();
		for (int i = 0; i < attackButtons_.length; ++i) {
			attackButtons_[i].init();
		}
	}
	
	public void setTouchDPadBounds(float x, float y, float width, float height) {
		dpad.setBounds(x, y, width, height);
	}
	
	public void setAttackButtonBounds(int button, float x, float y, float width, float height) {
		attackButtons_[button].setBounds(x, y, width, height);
	}

	// Begin private members
	private boolean useTouchInterface_ = true;
	private boolean firstRun_ = true;
	private HUDVirtualDPad dpad = new HUDVirtualDPad();
	
	private HUDVirtualButton[] attackButtons_ = new HUDVirtualButton[4];
	
	// End private members

}
