package com.falko.android.snowball.hud;

import com.falko.android.snowball.R;
import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.utility.SortConstants;
import com.falko.android.snowball.utility.Vector2D;

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
		// TODO Auto-generated method stub
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
			
			render.scheduleForDraw(circleOuter_, circleLocation_,
					SortConstants.HUD+1, false);
		}
		
		render.scheduleForDraw(circleInner_, circleInnerLocation_,
				SortConstants.HUD, false);

	}

	void init() {
		if (circleInner_.getWidth() == 0) {
			// first time init
			circleInner_.setTexture(sSystemRegistry.shortTermTextureLibrary
					.getTextureByResource(R.drawable.joystick_circle_inner));
			Texture tex = circleInner_.getTexture();
			circleInner_.resize(tex.width, tex.height);
			circleInner_.setWidth((int) width_);
			circleInner_.setHeight((int) height_);
		}

		if (circleOuter_.getWidth() == 0) {
			// first time init
			circleOuter_.setTexture(sSystemRegistry.shortTermTextureLibrary
					.getTextureByResource(R.drawable.joystick_circle_outer));
			Texture tex = circleOuter_.getTexture();
			circleOuter_.resize(tex.width, tex.height);
			circleOuter_.setWidth((int) width_);
			circleOuter_.setHeight((int) height_);
		}
	}

	DrawableBitmap circleInner_;
	DrawableBitmap circleOuter_;
	Vector2D circleInnerLocation_ = new Vector2D();
	Vector2D circleLocation_ = new Vector2D();

	private Vector2D position_ = new Vector2D();
	private float width_;
	private float height_;
}
