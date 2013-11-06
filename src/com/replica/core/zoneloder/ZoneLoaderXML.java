package com.replica.core.zoneloder;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import com.replica.R;
import com.replica.core.collision.LineSegment;
import com.replica.core.graphics.Texture;
import com.replica.core.zoneloder.Zone.DrawLayerInfo;
import com.replica.utility.DebugLog;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.SortConstants;
import com.replica.utility.Utils;

public class ZoneLoaderXML implements ZoneLoader {
	
	public ZoneLoaderXML() {}
	
	@Override
	public Zone loadZone(InputStream in, Context c) {
		zone_ = new Zone();
		context_ = c;
		try {
			
			XmlPullParser parser = Xml.newPullParser();
			
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			
			parser.setInput(in, null);
			
			parseXML(parser);
		} catch (IOException e) {
			DebugLog.v("SnowBall", e.toString());
			zone_ = null; //return null if error
		} catch (XmlPullParserException e) {
			DebugLog.v("SnowBall", e.toString());
			zone_ = null; //return null if error
		}
		
		Zone tempZone = zone_;
		zone_ = null;
		context_ = null;
		return tempZone;
	}
	
	/**
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseXML(XmlPullParser parser) 
			throws XmlPullParserException, IOException {

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
				
			} else if (tagName.equals("collision")) {
				parseCollisionGroup(parser);
				
			}
		}
	}
	
	/**
	 * 
	 * @param parser
	 */
	private void parseMap(XmlPullParser parser) {

		String attributeName = "";
		int ac = parser.getAttributeCount();
		int layerCount = 0;
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("MapTileWidth")) {
				
				zone_.worldTileWidth_ = Integer.parseInt(parser.getAttributeValue(i));
				
			} else if (attributeName.equals("MapTileHeight")) {
				
				zone_.worldTileHeight_ = Integer.parseInt(parser.getAttributeValue(i));
				
			} else if (attributeName.equals("TilePixilHeight")) {
				
				zone_.tilePixelHeight_ = Integer.parseInt(parser.getAttributeValue(i));
				
			} else if (attributeName.equals("TilePixilWidth")) {
				
				zone_.tilePixelWidth_ = Integer.parseInt(parser.getAttributeValue(i));
				
			} else if (attributeName.equals("DrawLayerCount")) {
				
				layerCount = Integer.parseInt(parser.getAttributeValue(i));

			}
		}
		
		zone_.drawLayerInfo_ = new DrawLayerInfo[layerCount];
		
		for (int i = 0; i < layerCount; ++i) {
			DrawLayerInfo layer = new DrawLayerInfo();
			layer.priority_ = -1;
			layer.mapDataUVs_ = new int[zone_.worldTileHeight_][zone_.worldTileWidth_];
			zone_.drawLayerInfo_[i] = layer;
		}
	}	
	
	/**
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseTileSet(XmlPullParser parser)
			throws XmlPullParserException, IOException {
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
			} else if (attributeName.equals("imageHeight")) {
				imageHeight = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("imageWidth")) {
				imageWidth = Integer.parseInt(parser.getAttributeValue(i));
			}
		}
		
		//FIXME: don't use texture
		zone_.texture_ = new Texture();
		zone_.texture_.resource = resourceID;
		zone_.texture_.width = imageWidth;
		zone_.texture_.height = imageHeight;
	}	
	
	/**
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseLayer(XmlPullParser parser) throws XmlPullParserException, IOException {

		int priority = 0;
		int layerNumber = 0;
		String attributeName = "";
		int ac = parser.getAttributeCount();
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("drawLevel")) {

				priority += parser.getAttributeValue(i).equalsIgnoreCase("background") ? SortConstants.BACKGROUND_START : SortConstants.HIGHGROUND_START;
				
			} else if (attributeName.equals("priority")) {
				
				priority += Integer.parseInt(parser.getAttributeValue(i));
				
			} else if (attributeName.equals("LayerNumber")) {
				
				layerNumber = Integer.parseInt(parser.getAttributeValue(i));
				
			}
		}
		
		zone_.drawLayerInfo_[layerNumber].priority_ = priority;
		
		parser.nextTag();
		if (parser.next() != XmlPullParser.TEXT) {
			throw new IOException("No Tile Data");
		}

		final int worldHeight = zone_.worldTileHeight_;
		final int worldWidth = zone_.worldTileWidth_;
		String mapDataStr = parser.getText();
		String[] mapStrArray = mapDataStr.split(",");

		for (int tileY = worldHeight - 1; tileY >= 0; tileY--) {
			for (int tileX = 0; tileX < worldWidth; tileX++) {
				
				final int y = worldHeight - tileY - 1;
				final int gid = Integer.parseInt(mapStrArray[y * worldWidth + tileX].trim());
						
				zone_.drawLayerInfo_[layerNumber].mapDataUVs_[tileY][tileX] = gid;
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseCollisionGroup(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		String lineCountAttrName = parser.getAttributeName(0);
		if (!lineCountAttrName.equals("lineCount")) {
			DebugLog.e("SnowBall", "Collision");
			return;
		}
		
		int lineCount = Integer.parseInt(parser.getAttributeValue(0));
		zone_.backgroundCollisionLines_ = new FixedSizeArray<LineSegment>(lineCount);
		String attributeName = "";
		for (int i = 0; i < lineCount; ++i) {
			
			parser.nextTag();
			
			int startx = 0;
			int starty = 0;
			int endx = 0;
			int endy = 0;
			
			int ac = parser.getAttributeCount();
			for (int j = 0; j < ac; ++j) {
				attributeName = parser.getAttributeName(j);
				if (attributeName.equals("startX")) {
					startx = Integer.parseInt(parser.getAttributeValue(j));
				} else if (attributeName.equals("startY")) {
					starty = Integer.parseInt(parser.getAttributeValue(j));
				} else if (attributeName.equals("endX")) {
					endx = Integer.parseInt(parser.getAttributeValue(j));
				} else if (attributeName.equals("endY")) {
					endy = Integer.parseInt(parser.getAttributeValue(j));
				}
			}
			
			zone_.backgroundCollisionLines_.add(new LineSegment(startx, starty, endx, endy));
			parser.nextTag();
		}	
	}	
	
	private Context context_;
	private Zone zone_;
}
