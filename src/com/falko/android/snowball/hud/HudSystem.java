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

package com.falko.android.snowball.hud;

import com.falko.android.snowball.R;
import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.ContextParameters;
import com.falko.android.snowball.core.GameObjectManager;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.core.graphics.DrawableFactory;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.utility.SortConstants;
import com.falko.android.snowball.utility.Vector2D;
import com.falko.android.snowball.utility.VectorPool;

/**
 * A very simple manager for orthographic in-game UI elements. TODO: This should
 * probably manage a number of hud objects in keeping with the component-centric
 * architecture of this engine. The current code is monolithic and should be
 * refactored.
 */
public class HudSystem extends BaseObject {
	
	
	private static int DPAD_BUTTON_SIZE_BASE = 4;
	private static final float DPAD_UP_BUTTON_X = 80f;
    private static final float DPAD_UP_BUTTON_Y = 100f;
    
    private static final float DPAD_DOWN_BUTTON_X = 80f;
    private static final float DPAD_DOWN_BUTTON_Y = 20f;
    
    private static final float DPAD_LEFT_BUTTON_X = 40f;
    private static final float DPAD_LEFT_BUTTON_Y = 60f;
    
    private static final float DPAD_RIGHT_BUTTON_X = 120f;
    private static final float DPAD_RIGHT_BUTTON_Y = 60f;
    
    private static final float DPAD_CENTER_BUTTON_X = 80f;
    private static final float DPAD_CENTER_BUTTON_Y = 60f;

	public HudSystem() {
		super();
//		Texture texture = sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.debug_circle_red);
		dpadUp_ = new DrawableBitmap(null, 0, 0);
		dpadDown_ = new DrawableBitmap(null, 0, 0);
		dpadLeft_ = new DrawableBitmap(null, 0, 0);
		dpadRight_ = new DrawableBitmap(null, 0, 0);
		dpadCenter_ = new DrawableBitmap(null, 0, 0);
		reset();
	}

	@Override
	public void reset() {
		dpadUpButtonLocation_.set(DPAD_UP_BUTTON_X, DPAD_UP_BUTTON_Y);
		dpadDownButtonLocation_.set(DPAD_DOWN_BUTTON_X, DPAD_DOWN_BUTTON_Y);
		dpadLeftButtonLocation_.set(DPAD_LEFT_BUTTON_X, DPAD_LEFT_BUTTON_Y);
		dpadRightButtonLocation_.set(DPAD_RIGHT_BUTTON_X, DPAD_RIGHT_BUTTON_Y);
		dpadCenterButtonLocation_.set(DPAD_CENTER_BUTTON_X, DPAD_CENTER_BUTTON_Y);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		final RenderSystem render = sSystemRegistry.renderSystem;
		final VectorPool pool = sSystemRegistry.vectorPool;
		final ContextParameters params = sSystemRegistry.contextParameters;
		final DrawableFactory factory = sSystemRegistry.drawableFactory;

		final GameObjectManager manager = sSystemRegistry.gameObjectManager;

		if (manager != null && manager.getPlayer() != null) {
			// Only draw player-specific HUD elements when there's a player.

		}
		
		
		if (useTouchInterface_) {
//			dpadUp = sSystemRegistry.drawableFactory.allocateDrawableBitmap();
//			dpadLeft = sSystemRegistry.drawableFactory.allocateDrawableBitmap();
//			dpadRight = sSystemRegistry.drawableFactory.allocateDrawableBitmap();
//			dpadDown = sSystemRegistry.drawableFactory.allocateDrawableBitmap();
//			dpadCenter = sSystemRegistry.drawableFactory.allocateDrawableBitmap();

			if (dpadUp_.getWidth() == 0) {
                // first time init
				dpadUp_.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.dpad_inactive_up));
                Texture tex = dpadUp_.getTexture();
                dpadUp_.resize(tex.width, tex.height);
                dpadUp_.setWidth((int)(tex.width / DPAD_BUTTON_SIZE_BASE));
                dpadUp_.setHeight((int)(tex.height / DPAD_BUTTON_SIZE_BASE));
            }
			render.scheduleForDraw(dpadUp_, dpadUpButtonLocation_, SortConstants.HUD, false);
			
			if (dpadDown_.getWidth() == 0) {
                // first time init
				dpadDown_.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.dpad_inactive_down));
                Texture tex = dpadDown_.getTexture();
                dpadDown_.resize(tex.width, tex.height);
                dpadDown_.setWidth((int)(tex.width / DPAD_BUTTON_SIZE_BASE));
                dpadDown_.setHeight((int)(tex.height / DPAD_BUTTON_SIZE_BASE));
            }
			render.scheduleForDraw(dpadDown_, dpadDownButtonLocation_, SortConstants.HUD, false);
			
			if (dpadLeft_.getWidth() == 0) {
                // first time init
				dpadLeft_.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.dpad_inactive_left));
                Texture tex = dpadLeft_.getTexture();
                dpadLeft_.resize(tex.width, tex.height);
                dpadLeft_.setWidth((int)(tex.width / DPAD_BUTTON_SIZE_BASE));
                dpadLeft_.setHeight((int)(tex.height / DPAD_BUTTON_SIZE_BASE));
            }
			render.scheduleForDraw(dpadLeft_, dpadLeftButtonLocation_, SortConstants.HUD, false);
			
			if (dpadRight_.getWidth() == 0) {
                // first time init
				dpadRight_.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.dpad_inactive_right));
                Texture tex = dpadRight_.getTexture();
                dpadRight_.resize(tex.width, tex.height);
                dpadRight_.setWidth((int)(tex.width / DPAD_BUTTON_SIZE_BASE));
                dpadRight_.setHeight((int)(tex.height / DPAD_BUTTON_SIZE_BASE));
            }
			render.scheduleForDraw(dpadRight_, dpadRightButtonLocation_, SortConstants.HUD, false);
			
			if (dpadCenter_.getWidth() == 0) {
                // first time init
				dpadCenter_.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.dpad_center));
                Texture tex = dpadCenter_.getTexture();
                dpadCenter_.resize(tex.width, tex.height);
                dpadCenter_.setWidth((int)(tex.width / DPAD_BUTTON_SIZE_BASE));
                dpadCenter_.setHeight((int)(tex.height / DPAD_BUTTON_SIZE_BASE));
            }
			
			render.scheduleForDraw(dpadCenter_, dpadCenterButtonLocation_, SortConstants.HUD, false);
		}
		

	}
	
	//Begin private members
	private boolean useTouchInterface_ = true;
	DrawableBitmap dpadUp_;
	DrawableBitmap dpadDown_;
	DrawableBitmap dpadLeft_;
	DrawableBitmap dpadRight_;
	DrawableBitmap dpadCenter_;
	
	Vector2D dpadUpButtonLocation_ = new Vector2D();
	Vector2D dpadDownButtonLocation_ = new Vector2D();
	Vector2D dpadLeftButtonLocation_ = new Vector2D();
	Vector2D dpadRightButtonLocation_ = new Vector2D();
	Vector2D dpadCenterButtonLocation_ = new Vector2D();
	
//	DrawableBitmap dpadLeft;
//	DrawableBitmap dpadRight;
//	DrawableBitmap dpadDown;
//	DrawableBitmap dpadCenter;
	
	
	
	//End private members

}
