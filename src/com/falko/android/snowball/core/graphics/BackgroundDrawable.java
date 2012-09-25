/*
 * TexturedVertexGrid.java
 *
 * Copyright (C) 2012 Matt Falkoski
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.falko.android.snowball.core.graphics;


/**
 * @author matt
 * 
 */
public class BackgroundDrawable extends DrawableObject {


	
	/* (non-Javadoc)
	 * @see com.falko.android.pinhead.DrawableObject#draw(float, float, float, float)
	 */
	@Override
	public void draw(float x, float y, float scaleX, float scaleY) {
		layer_.draw(x, y, viewWidth_, viewHeight_);
	}
	
	public void init(Layer layer, float cameraX, float cameraY, float viewWidth, float viewHeight) {
		layer_ = layer;
		cameraX_ = cameraX;
		cameraY_ = cameraY;
		viewWidth_ = viewWidth;
		viewHeight_ = viewHeight;
	}
	
	public void reset() {
		layer_ = null;
		cameraX_ = 0.0f;
		cameraY_ = 0.0f;
		viewWidth_ = 0.0f;
		viewHeight_ = 0.0f;
	}
	
	private Layer layer_;
	private float cameraX_;
	private float cameraY_;
	private float viewWidth_;
	private float viewHeight_;
}
