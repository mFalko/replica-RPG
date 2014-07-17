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
 

package com.replica.hud;

import com.replica.R;
import com.replica.core.BaseObject;
import com.replica.core.graphics.DrawableBitmap;
import com.replica.core.graphics.Texture;
import com.replica.core.systems.RenderSystem;
import com.replica.input.ButtonConstants;
import com.replica.input.InputGameInterface;
import com.replica.utility.DebugLog;
import com.replica.utility.SortConstants;
import com.replica.utility.Vector2;

public class HUDVirtualButton extends BaseObject {

	public HUDVirtualButton() {
		this(0, 0, 0, 0);
	}

	public HUDVirtualButton(float x, float y, float width, float height) {
		buttonGloss_ = new DrawableBitmap(null, 0, 0);
		buttonGraphic_ = new DrawableBitmap(null, 0, 0);
		setBounds(x, y, height, width);
	}

	public void setBounds(float x, float y, float width, float height) {
		position_.x = x;
		position_.y = y;
		width_ = width;
		height_ = height;
		
		reset();
	}
	
	@Override
	public void reset() {
		position_.set(position_.x, position_.y);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		final RenderSystem render = sSystemRegistry.renderSystem;
		InputGameInterface input = sSystemRegistry.inputGameInterface;
		
		
		if (input.getButtonPressed(index_)) {
			buttonGraphic_.setOpacity(0.85f);
		} else {
			buttonGraphic_.setOpacity(0.5f);
		}
		
		render.scheduleForDraw(buttonGraphic_, position_,
				SortConstants.HUD, false);
		
//		render.scheduleForDraw(buttonGloss_, position_,
//				SortConstants.HUD+1, false);
		
	}
	
	protected void init() {
//		{
//		// first time init
//		buttonGloss_.setTexture(sSystemRegistry.shortTermTextureLibrary
//				.getTextureByResource(R.drawable.buttongloss));
//		Texture tex = buttonGloss_.getTexture();
//		buttonGloss_.resize(tex.width, tex.height);
//		buttonGloss_.setWidth((int) width_);
//		buttonGloss_.setHeight((int) height_);
//		}
		{
		buttonGraphic_.setTexture(sSystemRegistry.shortTermTextureLibrary
				.getTextureByResource(R.drawable.sq_butotn));
		Texture tex = buttonGraphic_.getTexture();
		buttonGraphic_.resize(tex.width, tex.height);
		buttonGraphic_.setWidth((int) width_);
		buttonGraphic_.setHeight((int) height_);
		buttonGraphic_.setOpacity(0.5f);
		}
	}
	
	public void setTexture(int texture) {
		buttonGraphic_.setTexture(sSystemRegistry.shortTermTextureLibrary
				.getTextureByResource(texture));
	}
	
	public void setIndex(int index) {
		if (index < 0 || index >= ButtonConstants.GAME_BUTTON_COUNT) {
			DebugLog.v("Input Interface", "Invalid Button Index");
			return;
		}
		index_ = index;
	}
	
	public enum ButtonShape {
		RECTANGLE,
		CIRCLE
	}
	
	private ButtonShape shape;
	private DrawableBitmap buttonGloss_;
	private DrawableBitmap buttonGraphic_;
	
	private Vector2 position_ = new Vector2();
	private float width_;
	private float height_;
	private int index_ = -1;
	
}
