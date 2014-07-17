
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
