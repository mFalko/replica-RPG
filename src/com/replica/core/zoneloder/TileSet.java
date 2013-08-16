/*
 * TileSet.java
 *
 * Copyright (C) 2012 Matt Falkoski
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.replica.core.zoneloder;

import java.util.ArrayList;

import com.replica.core.BaseObject;
import com.replica.core.graphics.Texture;
import com.replica.core.graphics.TextureLibrary;
import com.replica.utility.FixedSizeArray;

public class TileSet extends BaseObject{

	public TileSet() {
		sheets_ = new FixedSizeArray<Sheet>(6);
	}
	
	@Override
	public void reset() {
	}

	public int GIDToUV(long GID, float[][] uv, Sheet[] sheets) {

		Sheet tileSheet = null;
		long id = GID &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
		int sheetIndex = getSheetIndex(id, sheets);
		tileSheet = sheets[sheetIndex];
		
		assert tileSheet != null;

		final int pixelWidth = tileSheet.texture_.width;
		final int pixelHeight = tileSheet.texture_.height;
		final int width = pixelWidth / tileWidth_;

		//FIXME: allow tile rotations!!! This is important
//		final boolean flippedHorizontally = (GID & FLIPPED_HORIZONTALLY_FLAG) > 1;
//		final boolean flippedVertically = (GID & FLIPPED_VERTICALLY_FLAG) > 1;
//		final boolean flippedDiagonally = (GID & FLIPPED_DIAGONALLY_FLAG) > 1;

		long gid = id - tileSheet.firstGID_;// zero based
		if (gid < 0) gid = 0;
		final float y = (gid / width) * tileHeight_;
		final float x = (gid % width) * tileWidth_;

		float[] uv0 = { (x + GL_MAGIC_OFFSET) / pixelWidth ,
						(y + GL_MAGIC_OFFSET) / pixelHeight};
		
		float[] uv1 = { (x + GL_MAGIC_OFFSET) / pixelWidth, 
						(y + tileHeight_ - GL_MAGIC_OFFSET) / pixelHeight};
		
		float[] uv2 = { (x + tileWidth_ - GL_MAGIC_OFFSET) / pixelWidth,
						(y + tileHeight_ - GL_MAGIC_OFFSET) / pixelHeight };
		
		float[] uv3 = { (x + tileWidth_ - GL_MAGIC_OFFSET) / pixelWidth,
						(y + GL_MAGIC_OFFSET) / pixelHeight };

		uv[0] = uv0;
		uv[1] = uv1;
		uv[2] = uv2;
		uv[3] = uv3;
		
		return sheetIndex;
	}

	public void addSheet(int firstGID, int resourceID, int width, int height) {
		TextureLibrary library = sSystemRegistry.shortTermTextureLibrary; 
		Texture texture = library.allocateTexture(resourceID);
		texture.width = width;
		texture.height = height;
		
		Sheet sheet = new Sheet(firstGID, texture);
		sheets_.add(sheet);
	}
	
	private int getSheetIndex(long GID, Sheet[] sheets) {
		long id = GID &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
		int retVal = 0;

		for (int i = 0; i < sheets.length; ++i) {	
			if (i + 1 < sheets.length) {		
				int fid = sheets[i].firstGID_;
				int eid = sheets[i+1].firstGID_;
				
				if (id >= fid && id < eid) {
					retVal = i;
					break;
				}
			} else {
				retVal = i;
				break;
			}
		}
		return retVal;
	}
	
	public Sheet[] getSheets(long[][] data) {
		boolean[] totalShets = new boolean[sheets_.getCount()];
		ArrayList<Sheet> list = new ArrayList<Sheet>();
		
		//TODO: fix this
		Sheet[] sheets = new Sheet[sheets_.getCount()];
		for (int i = 0; i < sheets_.getCount(); ++i)
			sheets[i] = sheets_.get(i);
		
		for (int i = 0; i < data.length; ++i) {
			for (int j = 0; j < data[i].length; ++j) {
				totalShets[getSheetIndex(data[i][j], sheets)] = true;
			}
		}
		
		int retVal = 0;
		for (int i = 0; i < totalShets.length; ++i) {
			if (totalShets[i]) {
				retVal++;
				list.add(sheets_.get(i));
				
			}
		}
		Sheet[] retSheets = new Sheet[retVal];
		list.toArray(retSheets);
		return sheets;
	}
	
	public int getTileWidth() {
		return tileWidth_;
	}
	
	public int getTileHeight() {
		return tileHeight_;
	}
	
	public void setTileWidth(int tileWidth) {
		tileWidth_ = tileWidth;
	}
	
	public void setTileHeight(int tileHeight) {
		tileHeight_ = tileHeight;
	}
	
	public Texture[] getTextures() {
		Texture[] textures = new Texture[sheets_.getCount()];
		for (int i = 0; i < textures.length; i++) {
			textures[i] = sheets_.get(i).texture_;
		}
		return textures;
	}
	
	private static final float GL_MAGIC_OFFSET = 0.45f;
	private FixedSizeArray<Sheet> sheets_;
	private int tileWidth_;
	private int tileHeight_;
	
	private static final long FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
	private static final long FLIPPED_VERTICALLY_FLAG = 0x40000000;
	private static final long FLIPPED_DIAGONALLY_FLAG = 0x20000000;

	protected class Sheet {
		public Sheet(int firstGID, Texture texture) {
			firstGID_ = firstGID;
			texture_ = texture;
		}
		public final int firstGID_;
		public final Texture texture_;
	}

	
}
