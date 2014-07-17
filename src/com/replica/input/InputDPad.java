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

public class InputDPad extends BaseObject {

	public InputDPad() {
		this(0,0,0,0);
	}

	public InputDPad(float x, float y, float width, float height) {
		setBounds(x, y, height, width);
	}

	public void setBounds(float x, float y, float width, float height) {
		position_.x = x;
		position_.y = y;
		height_ = height;
		width_ = width;

		topThird_ = (height / 3.0f) * 2.0f;
		bottomThird_ = (height / 3.0f);
		leftThird_ = (width / 3.0f);
		rightThird_ = (width / 3.0f) * 2.0f;
	}

	@Override
	public void reset() {
		dpadXY_.release();
	}

	@Override
	public void update(float currentTime, BaseObject parent) {

		InputSystem input = sSystemRegistry.inputSystem;
		final InputTouchScreen touch = input.getTouchScreen();
		final InputXY dpadTouch = touch.findPointerInRegion(position_.x,
				position_.y, width_, height_);

		if (dpadTouch != null) {
			float touchx = (dpadTouch.getX() - position_.x);
			float touchy = (dpadTouch.getY() - position_.y);
			dpadXY_.press(currentTime, touchx, touchy);
		} else {
			dpadXY_.release();
		}
	}
	
	
	public boolean getDirection(Vector2 direction) {
		
		if (!dpadXY_.getPressed()) {
			return false;
		}
		
		float x = dpadXY_.getX() - (width_ * 0.5f);
		float y = dpadXY_.getY() - (height_ * 0.5f);
		
		
		if (Math.sqrt((x*x) + (y*y)) < (width_ * 0.20f)) {
			return false;
		}
		
		
		direction.set(x, y);
		direction.normalize();
		return true;
	}

	public boolean pressed() {
		return dpadXY_.getPressed();
	}

	public boolean upPressed() {
		return dpadXY_.getPressed()
				&& (dpadXY_.getY() >= topThird_ && (dpadXY_.getX() >= leftThird_ && dpadXY_
						.getX() <= rightThird_));
	}

	public boolean downPressed() {
		return dpadXY_.getPressed()
				& (dpadXY_.getY() <= bottomThird_ && (dpadXY_.getX() >= leftThird_ && dpadXY_
						.getX() <= rightThird_));
	}

	public boolean leftPressed() {
		return dpadXY_.getPressed()
				& (dpadXY_.getX() <= leftThird_ && (dpadXY_.getY() >= bottomThird_ && dpadXY_
						.getY() <= topThird_));
	}

	public boolean rightPressed() {
		return dpadXY_.getPressed()
				& (dpadXY_.getX() >= rightThird_ && (dpadXY_.getY() >= bottomThird_ && dpadXY_
						.getY() <= topThird_));
	}
	
	private InputXY dpadXY_ = new InputXY();
	private Vector2 position_ = new Vector2();
	private float width_;
	private float height_;

	private float topThird_;
	private float bottomThird_;
	private float leftThird_;
	private float rightThird_;

}
