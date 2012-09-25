/*
 * XMLMapLoader.java
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

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.falko.android.snowball.R;
import com.falko.android.snowball.core.graphics.Layer;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.graphics.VertexGrid;
import com.falko.android.snowball.core.zoneloder.TileSet.Sheet;
import com.falko.android.snowball.utility.FixedSizeArray;
import com.falko.android.snowball.utility.Utils;
import com.falko.android.snowball.utility.Vector2D;

/**
 * @author matt
 * 
 */
public class XMLZoneLoader implements ZoneLoader {

	public XMLZoneLoader() {
		layers_ = new FixedSizeArray<Layer>(MAX_LAYERS);
		tileSet_ = new TileSet();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.falko.android.pinhead.MapLoader#loadMap(java.io.InputStream,
	 * android.content.Context)
	 */
	public Zone loadZone(InputStream in, Context c) {

		context_ = c;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parseXML(parser);
		} catch (XmlPullParserException e) {
			Log.v("pinhead", e.getMessage());
		} catch (IOException e) {
			Log.v("pinhead", e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// stuff happens
			}
		}

		Zone map = buildMap();
		assert map != null;
		return map;
	}

	private Zone buildMap() {
		float[][] uvWorkspace = new float[4][2];
		for (int i = 0; i < layerCount_; ++i) {
			Sheet[] sheets = tileSet_.getSheets(mapData_[i]);
			int gridCount = sheets.length;

			VertexGrid[] grids = new VertexGrid[gridCount];
			Texture[] textures = new Texture[gridCount];
			
			for (int j = 0; j < grids.length; ++j) {
				grids[j] = new VertexGrid(worldWidth_, worldHeight_);
				textures[j] = sheets[j].texture_;
			} 
			
			final int tileHeight = tileSet_.getTileHeight(mapData_[i][0][0]);
			final int tileWidth = tileSet_.getTileWidth(mapData_[i][0][0]);
		
			for (int tileY = 0; tileY < worldHeight_; tileY++) {
				for (int tileX = 0; tileX < worldWidth_; tileX++) {
					
					final long GID = mapData_[i][tileY][tileX];
					final float offsetX = tileX * tileWidth;
					final float offsetY = tileY * tileHeight; 
					
					final float[] p0 = { offsetX,
							offsetY + tileHeight, 0.0f };
					final float[] p1 = { offsetX, offsetY, 0.0f };
					final float[] p2 = { offsetX + tileWidth,
							offsetY, 0.0f };
					final float[] p3 = { offsetX + tileWidth,
							offsetY + tileHeight, 0.0f };

					final float[][] positions = { p0, p1, p2, p3 };
					int gridIndex = tileSet_.GIDToUV(GID, uvWorkspace, sheets);
					grids[gridIndex].set(tileX, tileY, positions, uvWorkspace);
				}
			}
			
			Vector2D bottomLeft = new Vector2D();
			Vector2D topRight = new Vector2D(tileWidth*worldWidth_, tileHeight*worldHeight_);
			layers_.add(new Layer(bottomLeft, topRight, tileWidth, tileHeight, grids, textures));
		}
		
		Zone map = new Zone(layerCount_);
		for (int i = 0; i < layers_.getCount(); ++i) {
			map.addLayer(layers_.get(i));
		}
		return map;
	}

	private void parseXML(XmlPullParser parser) throws XmlPullParserException,
			IOException {

		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();

			if (tagName.equals("map")) {

				parseMap(parser);

			} else if (tagName.equals("tileset")) {

				parseTileSet(parser);

			} else if (tagName.equals("layer")) {

				 parseLayer(parser);

			}
		}
	}

	private void parseMap(XmlPullParser parser) {

		String attributeName = "";
		int ac = parser.getAttributeCount();
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("width")) {
				worldWidth_ = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("height")) {
				worldHeight_ = Integer.parseInt(parser.getAttributeValue(i));
			}
		}
		mapData_ = new long[MAX_LAYERS][worldHeight_][worldWidth_];
	}

	private void parseTileSet(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int firstGID = 0;
		int tileWidth = 0;
		int tileHeight = 0;
		int resourceID = 0;
		int imageWidth = 0;
		int imageHeight = 0;

		String attributeName = "";
		int ac = parser.getAttributeCount();
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("name")) {
				resourceID = Utils.getResId(parser.getAttributeValue(i),
						context_, R.drawable.class);
			} else if (attributeName.equals("tilewidth")) {
				tileWidth = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("tileheight")) {
				tileHeight = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("firstgid")) {
				firstGID = Integer.parseInt(parser.getAttributeValue(i));
			}

		}

		parser.nextTag();
		assert (parser.getEventType() == XmlPullParser.START_TAG);

		ac = parser.getAttributeCount();
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("width")) {
				imageWidth = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("height")) {
				imageHeight = Integer.parseInt(parser.getAttributeValue(i));
			}
		}

		tileSet_.addSheet(firstGID, tileWidth, tileHeight, resourceID,
				imageWidth, imageHeight);
	}

	private void parseLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
		

		parser.nextTag();
		if (parser.next() != XmlPullParser.TEXT) {
			throw new IOException("No Tile Data");
		}

		String mapDataStr = parser.getText();
		String[] mapStrArray = mapDataStr.split(",");

		for (int tileY = worldHeight_ - 1; tileY >= 0; tileY--) {
			for (int tileX = 0; tileX < worldWidth_; tileX++) {
				final int y = worldHeight_ - tileY - 1;
				mapData_[layerCount_][tileY][tileX] = Long.parseLong(mapStrArray[y
						* worldWidth_ + tileX].trim());
			}
		}
		layerCount_++;
	}

	private static final int MAX_LAYERS = 3;
	private FixedSizeArray<Layer> layers_;
	private long[][][] mapData_;
	private TileSet tileSet_;
	private Context context_;
	private int worldWidth_;
	private int worldHeight_;
	private int layerCount_;
}
