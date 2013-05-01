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

package com.replica.core.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

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

	private Boolean mGenerated;

	private VertexGrid[] grids_;
	private Texture[] textures_;

	public TiledVertexGrid(Texture[] texture, VertexGrid[] grids, int width,
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

		mGenerated = false;

		textures_ = texture;
		grids_ = grids;
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

		final float percentageScrollRight = scrollOriginX != 0.0f ? scrollOriginX / worldPixelWidth : 0.0f;
		final float tileSpaceX = percentageScrollRight * mTilesPerRow;
		final int leftTile = (int) tileSpaceX;

		// calculate the top tile index
		final float worldPixelHeight = mWorldPixelHeight;

		final float percentageScrollUp = scrollOriginY != 0.0f ? scrollOriginY / worldPixelHeight : 0.0f;
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

		for (int g = 0; g < grids_.length; ++g) {
			OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, textures_[g].name);
			grids_[g].beginDrawingStrips(gl, true);
			for (int tileY = startY; tileY < endY && tileY < mTilesPerColumn; tileY++) {
				final int row = tileY * indexesPerRow;
				grids_[g].drawStrip(gl, true, row + startOffset, count);
			}
		}

		gl.glPopMatrix();
		VertexGrid.EndDrawingVertexGrid(gl);
	}

}
