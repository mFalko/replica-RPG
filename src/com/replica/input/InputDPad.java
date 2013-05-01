
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
