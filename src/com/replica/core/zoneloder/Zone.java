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

package com.replica.core.zoneloder;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.PhasedObjectManager;
import com.replica.core.collision.LineSegment;
import com.replica.core.graphics.Layer;
import com.replica.utility.FixedSizeArray;

/**
 * @author matt
 * 
 */
public class Zone extends PhasedObjectManager{

	public Zone() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.falko.android.pinhead.BaseObject#update(float,
	 * com.falko.android.pinhead.BaseObject)
	 */
	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		
		background.update(timeDelta, parent);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.falko.android.pinhead.BaseObject#reset()
	 */
	@Override
	public void reset() {

	}

	public int getWorldHeight() {
		return worldHeight_;
	}
	
	public int getWorldWidth() {
		return worldWidth_;
	}

	protected void setWorldHeight(int height) {
		worldHeight_ = height;
	}

	protected void setWorldWidth(int width) {
		worldWidth_ = width;
	}
	
	protected void setcollisionLines(FixedSizeArray<LineSegment> collisionLines) {
		backgroundCollisionLines_ = collisionLines;
	}
	
	public FixedSizeArray<LineSegment> getCollisionLines() {
		return backgroundCollisionLines_;
	}
	
	private FixedSizeArray<LineSegment> backgroundCollisionLines_;
	private int worldWidth_;
	private int worldHeight_;
	
	GameObject background;
}
