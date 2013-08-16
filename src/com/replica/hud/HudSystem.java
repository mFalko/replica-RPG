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
import com.replica.core.GameObject;
import com.replica.core.GameObjectManager;
import com.replica.input.ButtonConstants;
import com.replica.input.InputGameInterface;

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
		for (int i = 0; i < attackButtons_.length; ++i) {
			attackButtons_[i].reset();
		}
		menuButton_.reset();
		dpad_.reset();

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		

		if (firstRun_) {
			init();
			firstRun_ = false;
		}
		
		final GameObjectManager manager = sSystemRegistry.gameObjectManager;
		GameObject player = null;
		if (manager != null) {
			player = manager.getPlayer();
		}
	
		if (player != null) {
			// Only draw player-specific HUD elements when there's a player.

			if (useTouchInterface_) {
				dpad_.update(timeDelta, this);
				menuButton_.update(timeDelta, this);
				for (int i = 0; i < attackButtons_.length; ++i) {
					attackButtons_[i].update(timeDelta, this);
				}
			}
		}		

	}

	private void init() {
		dpad_.init();
		menuButton_.init();
		for (int i = 0; i < attackButtons_.length; ++i) {
			attackButtons_[i].init();
		}
	}
	
	
	public void setButtonTexture(int button, int texture) {
		if (button < 0 || button >= ButtonConstants.GAME_BUTTON_COUNT) {
			// DebugLog.v("Input Interface", "Invalid Button Index");
			return;
		}
		
		attackButtons_[button].setTexture(texture);
	}
	
	public void registerTouchInput(InputGameInterface inputInterface, int viewWidth, int viewHeight) {
		
		dpad_.setBounds(ButtonConstants.D_PAD_REGION_X,
						ButtonConstants.D_PAD_REGION_Y,
						ButtonConstants.D_PAD_REGION_WIDTH,
						ButtonConstants.D_PAD_REGION_HEIGHT);
		inputInterface.setDpadLocation(ButtonConstants.D_PAD_REGION_X,
										ButtonConstants.D_PAD_REGION_Y,
										ButtonConstants.D_PAD_REGION_WIDTH,
										ButtonConstants.D_PAD_REGION_HEIGHT);
		
		int padding = ButtonConstants.BUTTON_PADDING;
		int buttonWidth = ButtonConstants.GAME_BUTTON_WIDTH;
		int buttonHeight = ButtonConstants.GAME_BUTTON_HEIGHT;
		
		int startX = viewWidth - buttonWidth - padding;
		int startY = padding;
		
		int height = 0;
		for (int i = 0; i < attackButtons_.length; ++i) {
			if (height >= ButtonConstants.BUTTON_COLUMN_HEIGHT) {
				startX -= buttonWidth + padding;
				startY = padding;
				height = 0;
			}
			
			attackButtons_[i].setBounds(startX, startY, buttonWidth, buttonHeight);
			attackButtons_[i].setIndex(i);
			
			inputInterface.setGameButtonLocation(i, startX, startY, buttonWidth, buttonHeight);
			
			startY += buttonHeight + padding;
			++height;
		}
		
		menuButton_.setBounds(viewWidth - ButtonConstants.MENU_BUTTON_WIDTH - padding,
				viewHeight - ButtonConstants.MENU_BUTTON_HEIGHT - padding,
				ButtonConstants.MENU_BUTTON_WIDTH,
				ButtonConstants.MENU_BUTTON_HEIGHT);
		
		inputInterface.setMenuButtonLocation(viewWidth - ButtonConstants.MENU_BUTTON_WIDTH - padding,
				viewHeight - ButtonConstants.MENU_BUTTON_HEIGHT - padding,
				ButtonConstants.MENU_BUTTON_WIDTH,
				ButtonConstants.MENU_BUTTON_HEIGHT);
	}
	
	private boolean useTouchInterface_ = true;
	private boolean firstRun_ = true;
	private HUDVirtualDPad dpad_ = new HUDVirtualDPad();
	private HUDVirtualButton menuButton_ = new HUDVirtualButton();
	private HUDVirtualButton[] attackButtons_ = new HUDVirtualButton[ButtonConstants.GAME_BUTTON_COUNT];

	

}
