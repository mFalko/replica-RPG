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

package com.replica.core.graphics;


/**
 * @author matt
 * 
 */
public class BackgroundDrawable extends DrawableObject {


	
	/* 
	 * 
	 */
	@Override
	public void draw(float x, float y, float scaleX, float scaleY) {
		layer_.draw(cameraX_, cameraY_, viewWidth_, viewHeight_);
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
