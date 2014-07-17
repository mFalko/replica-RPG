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

//TODO: where did I get this from, I didn't write it

package com.replica.core.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.replica.core.systems.OpenGLSystem;
import com.replica.utility.Vector2;

/**
 * @author matt
 * 
 */
public class Layer {

	public Layer(Vector2 bottomLeft, Vector2 topRight, int tileWidth,
			int tileHeight, VertexGrid[] grids, Texture[] textures, int priority) {
		bottomLeft_ = bottomLeft;
		topRight_ = topRight;
		tileWidth_ = tileWidth;
		tileHeight_ = tileHeight;
		grids_ = grids;
		textures_ = textures;
		priority_ = priority;
	}
	
	public int getPriority() {
		return priority_;
	}

	public void draw(float cameraX, float cameraY, float viewWidth,
			float viewHeight) {

		final float layerPixelLength = topRight_.x - bottomLeft_.x;
		final float layerPixelHeight = topRight_.y - bottomLeft_.y;
		final int horizontalTileCount = (int) (layerPixelLength / tileWidth_);
		final int verticalTileCount = (int) (layerPixelHeight / tileHeight_);

		float orginX = (viewWidth /2) - cameraX;
		float orginY = (viewHeight /2) - cameraY;
		
//		final float offsetX = cameraX - bottomLeft_.x;
		final float offsetX = orginX - bottomLeft_.x;
		
		
		Log.v("Replica", "offsetX = " + offsetX);
		
		int startX = 0;
		if (offsetX > tileWidth_) {
			final float XoffsetPrecent = offsetX / layerPixelLength;
			startX = (int) Math.floor(XoffsetPrecent
					* (layerPixelLength / tileWidth_));
		}

//		final float offsetY = cameraY - bottomLeft_.y;
		final float offsetY = orginY - bottomLeft_.y;
		
		Log.v("Replica", "offsetY = " + offsetY);
		
		int startY = 0;
		if (offsetY > tileHeight_) {
			final float YoffsetPrecent = offsetY / layerPixelHeight;
			startY = (int)Math.floor(YoffsetPrecent
					* (layerPixelHeight / tileHeight_));
		}
		
		
		Log.v("Replica", "startX = " + startX);
		Log.v("Replica", "startY = " + startY);
		
		
		int DrawCountX = (int) Math.ceil(viewWidth / tileWidth_) +1;
		DrawCountX = startX + DrawCountX < horizontalTileCount ? DrawCountX
				: horizontalTileCount - startX;

		int DrawCountY = (int)Math.ceil(viewHeight / tileHeight_) +1;
		DrawCountY = startY + DrawCountY <= verticalTileCount ? DrawCountY
				: verticalTileCount - startY;

		if (DrawCountY <= 0 || DrawCountX <= 0) {
			return; // layer is not visible
		}

		final int indexesPerTile = 6;
		final int indexesPerRow = horizontalTileCount * indexesPerTile;
		final int startOffset = (startX * indexesPerTile);
		final int count = DrawCountX * indexesPerTile;
		final int endY = startY + DrawCountY;

		GL10 gl = OpenGLSystem.getGL();
		VertexGrid.BeginDrawingVertexGrid(gl, true);
		gl.glPushMatrix();
		gl.glLoadIdentity();

//		gl.glTranslatef(-cameraX, -cameraY, 0.0f);
		
		gl.glTranslatef(orginX, orginY, 0.0f);
		
//		gl.glTranslatef(-(viewWidth/2 -cameraX),-(viewHeight/2 -cameraY), 0.0f);
		

		for (int g = 0; g < grids_.length; ++g) {
			OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, textures_[g].name);
			grids_[g].beginDrawingStrips(gl, true);
			for (int tileY = startY; tileY <= endY && tileY < verticalTileCount; tileY++) {
				final int row = tileY * indexesPerRow;
				grids_[g].drawStrip(gl, true, row + startOffset, count);

			}
		}

		gl.glPopMatrix();
		VertexGrid.EndDrawingVertexGrid(gl);
	}

	private Vector2 bottomLeft_; // the bottom left corner in pixel coords
	private Vector2 topRight_; // the top right corner in pixel coords
	private int tileWidth_;
	private int tileHeight_;
	private int priority_;
	private VertexGrid[] grids_;
	private Texture[] textures_;

}
