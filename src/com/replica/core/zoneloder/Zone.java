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

import com.replica.core.collision.LineSegment;
import com.replica.core.graphics.Texture;
import com.replica.utility.FixedSizeArray;

/**
 * @author matt
 * 
 */
public class Zone{

	

	public Zone() {
		
	}

	public int getWorldHeight() {
		return worldTileHeight_ * tilePixelHeight_;
	}
	
	public int getWorldWidth() {
		return worldTileWidth_ * tilePixelWidth_;
	}

	public int getWorldTileHeight() {
		return worldTileHeight_;
	}
	
	public int getWorldTileWidth() {
		return worldTileWidth_;
	}
	
	public int getTilePixelHeight() {
		return tilePixelHeight_;
	}
	
	public int getTilePixelWidth() {
		return tilePixelWidth_;
	}
	
	protected void setcollisionLines(FixedSizeArray<LineSegment> collisionLines) {
		backgroundCollisionLines_ = collisionLines;
	}
	
	public FixedSizeArray<LineSegment> getCollisionLines() {
		return backgroundCollisionLines_;
	}
	
	public DrawLayerInfo[] getDrawLayerInfo() {
		return drawLayerInfo_;
	}
	
	public Texture getTexture() {
		return texture_;
	}
	
	public void setTexture(Texture texture) {
		texture_ = texture;
	}
	
	protected DrawLayerInfo[] drawLayerInfo_;
	protected Texture texture_; //atlas for this zone
	protected FixedSizeArray<LineSegment> backgroundCollisionLines_;//TODO: make this a quadtree and pass the tree to the background collision system
//	protected int worldPixelWidth_;  //full map width in pixels
//	protected int worldPixelHeight_; //full map height in pixels
	protected int worldTileWidth_;
	protected int worldTileHeight_;
	protected int tilePixelHeight_;
	protected int tilePixelWidth_;
	
	public static class DrawLayerInfo {
		public int priority_;
		public int[][] mapDataUVs_;
	}

	
	
}
