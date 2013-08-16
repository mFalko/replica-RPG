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

import com.replica.core.GameObject;
import com.replica.core.collision.LineSegment;
import com.replica.utility.FixedSizeArray;

/**
 * @author matt
 * 
 */
public class Zone extends GameObject{

	public Zone() {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.falko.android.pinhead.BaseObject#reset()
	 */
	@Override
	public void reset() {
		worldWidth_ = -1;
		worldHeight_ = -1;
//		backgroundCollisionLines_.clear();
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
	
	//TODO: make this a quadtree and pass the tree to the background collision system
	private FixedSizeArray<LineSegment> backgroundCollisionLines_;
	private int worldWidth_;
	private int worldHeight_;
}
