package com.replica.hud;

import com.replica.R;
import com.replica.core.BaseObject;
import com.replica.core.graphics.DrawableBitmap;
import com.replica.core.graphics.Texture;
import com.replica.core.systems.RenderSystem;
import com.replica.input.InputGameInterface;
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
		
		
		render.scheduleForDraw(buttonGraphic_, position_,
				SortConstants.HUD, false);
		
		render.scheduleForDraw(buttonGloss_, position_,
				SortConstants.HUD+1, false);
		
	}
	
	void init() {
		{
		// first time init
		buttonGloss_.setTexture(sSystemRegistry.shortTermTextureLibrary
				.getTextureByResource(R.drawable.buttongloss));
		Texture tex = buttonGloss_.getTexture();
		buttonGloss_.resize(tex.width, tex.height);
		buttonGloss_.setWidth((int) width_);
		buttonGloss_.setHeight((int) height_);
		}
		{
		buttonGraphic_.setTexture(sSystemRegistry.shortTermTextureLibrary
				.getTextureByResource(R.drawable.buttonbackground));
		Texture tex = buttonGloss_.getTexture();
		buttonGraphic_.resize(tex.width, tex.height);
		buttonGraphic_.setWidth((int) width_);
		buttonGraphic_.setHeight((int) height_);
		}
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
	
}
