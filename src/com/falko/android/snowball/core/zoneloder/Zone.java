/*
 * Map.java
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

package com.falko.android.snowball.core.zoneloder;

import android.util.Log;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.graphics.BackgroundDrawable;
import com.falko.android.snowball.core.graphics.DrawableFactory;
import com.falko.android.snowball.core.graphics.Layer;
import com.falko.android.snowball.core.systems.CameraSystem;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.utility.FixedSizeArray;
import com.falko.android.snowball.utility.Vector2D;



/**
 * @author matt
 *
 */
public class Zone extends BaseObject {

	public Zone(int layerCount) {
		layers_ = new FixedSizeArray<Layer>(layerCount);
	}
	
	/* (non-Javadoc)
	 * @see com.falko.android.pinhead.BaseObject#update(float, com.falko.android.pinhead.BaseObject)
	 */
	@Override
	public void update(float timeDelta, BaseObject parent) {
		final int count = layers_.getCount();
		final DrawableFactory factory = sSystemRegistry.drawableFactory;
		final CameraSystem camera = sSystemRegistry.cameraSystem;
		final RenderSystem renderer = sSystemRegistry.renderSystem;
		final float cameraX = camera.getFocusPositionX();
		final float cameraY = camera.getFocusPositionY();
		final float viewWidth = sSystemRegistry.contextParameters.viewWidth;
		final float viewHeight = sSystemRegistry.contextParameters.viewHeight;
		//TODO: Refactor code to use TiledBackgroundVertexGrid
		Vector2D v = new Vector2D(0,0);
		for (int i = 0; i < count; ++i) {
			final Layer layer = layers_.get(i);
			BackgroundDrawable drawable = factory.allocateBackgroundDrawable();
			drawable.init(layer, cameraX, cameraY, viewWidth, viewHeight);
			drawable.setPriority(i);
			renderer.scheduleForDraw(drawable, v, i, false);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.falko.android.pinhead.BaseObject#reset()
	 */
	@Override
	public void reset() {
		
	}
	
	protected void addLayer(Layer layer) {
		layers_.add(layer);
	}
	
	private int height_;
	private int width_;
//	private 
	private FixedSizeArray<Layer> layers_;
}
