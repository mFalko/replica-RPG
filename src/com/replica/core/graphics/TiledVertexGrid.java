/*
 * Copyright (C) 2010 The Android Open Source Project
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
 
/*
 * This file has been modified from the original.
 * 
 * The original file can be found at:
 *		https://code.google.com/p/replicaisland/
 */
 
package com.replica.core.graphics;

import javax.microedition.khronos.opengles.GL10;

import com.replica.core.BaseObject;
import com.replica.core.systems.OpenGLSystem;

public class TiledVertexGrid extends BaseObject {

	private int mTileWidth;
	private int mTileHeight;

	private int mWidth; // view width
	private int mHeight; // view height

	private float mWorldPixelWidth;
	private float mWorldPixelHeight;

	private int mTilesPerRow;
	private int mTilesPerColumn;

//	private Boolean mGenerated;

	private VertexGrid grid_;
	private Texture texture_;

	public TiledVertexGrid(Texture texture, VertexGrid grid, int width,
			int height, int tileWidth, int tileHeight, int worldTileWidth,
			int worldTileHeight) {
		super();

		mTileWidth = tileWidth;
		mTileHeight = tileHeight;

		mWidth = width;
		mHeight = height;

		mTilesPerRow = worldTileWidth;
		mTilesPerColumn = worldTileHeight;

		mWorldPixelWidth = tileWidth * worldTileWidth;
		mWorldPixelHeight = tileHeight * worldTileHeight;

//		mGenerated = false;

		texture_ = texture;
		grid_ = grid;
	}

	public void setTexture(Texture texture) {
		texture_ = texture;
	}

	public void setTilePixelWidth(int tilePixelWidth) {
		mTileWidth = tilePixelWidth;
		mWorldPixelWidth = mTileWidth * mTilesPerRow;
	}

	public void setTilePixelHeight(int tilePixelHeight) {
		mTileHeight = tilePixelHeight;
		mWorldPixelHeight = mTileHeight * mTilesPerColumn;
	}
	
	@Override
	public void reset() {
	}

	public void draw(float x, float y, float scrollOriginX, float scrollOriginY) {
		
		GL10 gl = OpenGLSystem.getGL();
//		if (!mGenerated && gl != null) {
//
//			BufferLibrary bufferLibrary = sSystemRegistry.bufferLibrary;
//
//			mGenerated = true;
//			if (grid != null) {
//				bufferLibrary.add(grid);
//				if (sSystemRegistry.contextParameters.supportsVBOs) {
//					grid.generateHardwareBuffers(gl);
//				}
//			}
//
//		}

		if (gl == null) {
			return;
		}

		int originX = (int) (x - scrollOriginX);
		int originY = (int) (y - scrollOriginY);
		
		

		final float worldPixelWidth = mWorldPixelWidth;

		final float percentageScrollRight = (scrollOriginX-x) != 0.0f ? (scrollOriginX-x) / worldPixelWidth : 0.0f;
		final float tileSpaceX = percentageScrollRight * mTilesPerRow;
		final int leftTile = (int) tileSpaceX;

		// calculate the top tile index
		final float worldPixelHeight = mWorldPixelHeight;

		final float percentageScrollUp = (scrollOriginY-y) != 0.0f ? (scrollOriginY-y) / worldPixelHeight : 0.0f;
		final float tileSpaceY = percentageScrollUp * mTilesPerColumn;
		final int bottomTile = (int) tileSpaceY;

		// calculate any sub-tile slop that our scroll position may require.
		final int horizontalSlop = ((tileSpaceX - leftTile) * mTileWidth) > 0 ? 1 : 0;
		final int verticalSlop = ((tileSpaceY - bottomTile) * mTileHeight) > 0 ? 1 : 0;

		final int horzTileCount = (int) Math.ceil((float) mWidth / mTileWidth);
		final int vertTileCount = (int) Math.ceil((float) mHeight / mTileHeight);
		
		// draw vertex strips
		final int startX = leftTile;
		final int startY = bottomTile;
		final int endX = startX + horizontalSlop + horzTileCount;
		final int endY = startY + verticalSlop + vertTileCount;

		VertexGrid.BeginDrawingVertexGrid(gl, true);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(originX, originY, 0.0f);

		final int indexesPerTile = 6;
		final int indexesPerRow = mTilesPerRow * indexesPerTile;
		final int startOffset = (startX * indexesPerTile);
		final int count = (endX - startX) * indexesPerTile;


		OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, texture_.name);
		grid_.beginDrawingStrips(gl, true);
		for (int tileY = startY; tileY < endY && tileY < mTilesPerColumn; tileY++) {
			final int row = tileY * indexesPerRow;
			if (row + startOffset > 0)
				grid_.drawStrip(gl, true, row + startOffset, count);
		}
		

		gl.glPopMatrix();
		VertexGrid.EndDrawingVertexGrid(gl);
	}



}
