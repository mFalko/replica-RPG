
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
 
package com.replica.input;

import com.replica.core.BaseObject;
import com.replica.utility.Vector2;

public class InputTouchButton extends BaseObject {

	public InputTouchButton() {
		this(0,0,0,0);
	}
	
	public InputTouchButton(float x, float y, float width, float height) {
		setBounds(x, y, height, width);
	}
	
	public void setBounds(float x, float y, float width, float height) {
		position_.x = x;
		position_.y = y;
		height_ = height;
		width_ = width;
	}
	
	@Override
	public void reset() {
		button_.reset();
	}
	
	@Override
	public void update(float currentTime, BaseObject parent) {
		InputSystem input = sSystemRegistry.inputSystem;
		final InputTouchScreen touch = input.getTouchScreen();
		final InputXY buttonTouch = touch.findPointerInRegion(position_.x,
				position_.y, width_, height_);
		
		if (buttonTouch != null) {
			button_.press(currentTime, 1.0f);
		} else {
			button_.release();
		}
		
	}
	
	public float getLastPressedTime() {
		return button_.getLastPressedTime();
	}
	
	public boolean pressed() {
		return button_.getPressed();
	}
	
	public final float getPressedDuration(float currentTime) {
		return button_.getPressedDuration(currentTime);
	}
	
	public final float getMagnitude() {
		return button_.getMagnitude();
	}
	
	public void release() {
		button_.release();
	}
	
	public boolean containsPoint(float x, float y){
		return x >= position_.x 
				&& x <= position_.x + width_ 
				&& y >= position_.y
				&& y <= position_.y + height_;
	}
	
	private InputButton button_ = new InputButton();
	private Vector2 position_ = new Vector2();
	private float width_;
	private float height_;
	
}
