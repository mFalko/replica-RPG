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
import com.replica.input.InputGameInterface;
import com.replica.utility.SortConstants;
import com.replica.utility.Vector2;

public class HUDVirtualDPad extends BaseObject {

	public HUDVirtualDPad() {
		this(0, 0, 0, 0);
	}

	public HUDVirtualDPad(float x, float y, float width, float height) {
		circleInner_ = new DrawableBitmap(null, 0, 0);
		circleOuter_ = new DrawableBitmap(null, 0, 0);
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
		circleInnerLocation_.set(position_.x, position_.y);
		circleLocation_.set(position_.x, position_.y);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		final RenderSystem render = sSystemRegistry.renderSystem;
		InputGameInterface input = sSystemRegistry.inputGameInterface;

		if (input.getDpadPressed()) {
			circleInner_.setOpacity(0.85f);
		} else {
			circleInner_.setOpacity(0.65f);
			
//			render.scheduleForDraw(circleOuter_, circleLocation_,
//					SortConstants.HUD+1, false);
		}
		
		render.scheduleForDraw(circleInner_, circleInnerLocation_,
				SortConstants.HUD, false);

	}

	void init() {
		
		
		if (circleInner_.getWidth() == 0) {
			// first time init
			circleInner_.setTexture(sSystemRegistry.shortTermTextureLibrary
					.getTextureByResource(R.drawable.dpad));
			Texture tex = circleInner_.getTexture();
			circleInner_.resize(tex.width, tex.height);
			circleInner_.setWidth((int) width_);
			circleInner_.setHeight((int) height_);
		}
		
//		if (circleInner_.getWidth() == 0) {
//			// first time init
//			circleInner_.setTexture(sSystemRegistry.shortTermTextureLibrary
//					.getTextureByResource(R.drawable.joystick_circle_inner));
//			Texture tex = circleInner_.getTexture();
//			circleInner_.resize(tex.width, tex.height);
//			circleInner_.setWidth((int) width_);
//			circleInner_.setHeight((int) height_);
//		}
//
//		if (circleOuter_.getWidth() == 0) {
//			// first time init
//			circleOuter_.setTexture(sSystemRegistry.shortTermTextureLibrary
//					.getTextureByResource(R.drawable.joystick_circle_outer));
//			Texture tex = circleOuter_.getTexture();
//			circleOuter_.resize(tex.width, tex.height);
//			circleOuter_.setWidth((int) width_);
//			circleOuter_.setHeight((int) height_);
//		}
	}

	DrawableBitmap circleInner_;
	DrawableBitmap circleOuter_;
	Vector2 circleInnerLocation_ = new Vector2();
	Vector2 circleLocation_ = new Vector2();

	private Vector2 position_ = new Vector2();
	private float width_;
	private float height_;
}
