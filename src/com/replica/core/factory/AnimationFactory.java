package com.replica.core.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.replica.core.BaseObject;
import com.replica.core.collision.AABoxCollisionVolume;
import com.replica.core.collision.CollisionVolume;
import com.replica.core.game.AnimationType;
import com.replica.core.graphics.AnimationFrame;
import com.replica.core.graphics.SpriteAnimation;
import com.replica.core.graphics.Texture;
import com.replica.utility.FixedSizeArray;

/**
 * Auto loads all animation files in the animations directory. Files must have
 * names that correspond with AnimationType elements
 * 
 * @author matt
 * 
 */
public class AnimationFactory {

	AnimationFactory() {
		
	}

	// TODO: Filmstrip was a bad idea, load crop data from file to allow
	// animations to share a texture
	
	/*TODO: Figure out a way to "smooth" the collision volumes so that
	 * 		volumes that are similar are not allocated multiple times
	 * 
	 * Should probably be done in the file preprocessor 
	 */


	
	public SpriteAnimation loadAnimation(
			int animationFileID,
			int resourceID,
			float width, 
			float height) 
	{

	

		AnimationInfo	animationInfo = loadAnimationInfo(animationFileID);
			
			
		SpriteAnimation animation = new SpriteAnimation(animationInfo.frames);

		Texture tex = BaseObject.sSystemRegistry.longTermTextureLibrary
				.allocateTexture(resourceID);
		
		final float scaleX = width / animationInfo.frameWidth;
		final float scaleY = height / animationInfo.frameHeight;
		for (int i = 0; i < animationInfo.frames; ++i) {

			int[] crop = new int[4];
			crop[0] = (animationInfo.offset *animationInfo.frameWidth )
					+ (animationInfo.frameWidth * i);
			crop[1] = animationInfo.frameHeight;
			crop[2] = animationInfo.frameWidth;
			crop[3] = animationInfo.frameHeight;

			AnimationFrame frame = new AnimationFrame(tex, .1f);
			frame.mCrop = crop;
			frame.attackVolumes = generateCollisionVolumes(
					animationInfo.attackVolumes.get(i), scaleX, scaleY);
			frame.vulnerabilityVolumes = generateCollisionVolumes(
					animationInfo.vulnerabilityVolumes.get(i), scaleX,
					scaleY);
			animation.addFrame(frame);
		}

		animation.setLoop(animationInfo.loop);

		return animation;

	}

	private FixedSizeArray<CollisionVolume> generateCollisionVolumes(
			final FixedSizeArray<CollisionInfo> infoArray,
			final float scaleX, final float scaleY) {

		FixedSizeArray<CollisionVolume> collisionVolumes = new FixedSizeArray<CollisionVolume>(
				infoArray.getCount());

		final int count = infoArray.getCount();
		for (int i = 0; i < count; ++i) {

			final CollisionInfo info = infoArray.get(i);

			final float desiredWidth = info.width * scaleX;
			final float  desiredHeight = info.height * scaleY;

			final float x = info.x * scaleX;
			final float y = info.y * scaleY;
			final int hitType = info.hitType;

			collisionVolumes.add(new AABoxCollisionVolume(x, y, desiredWidth,
					desiredHeight, hitType));
		}

		return collisionVolumes;

	}

	
	//file load stuff
	
	
	private AnimationInfo loadAnimationInfo(int animationFileID) {

		Context context = BaseObject.sSystemRegistry.contextParameters.context;
		AnimationInfo animationInfo = new AnimationInfo();
		try {
			InputStream in = context.getResources().openRawResource(animationFileID);

			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String tagName = parser.getName();

				if (tagName.equals("animation")) {

					parseAnimation(parser, animationInfo);

				} else if (tagName.equals("attackVolumes")) {

					parseCollisionVolumes(parser, animationInfo,
							animationInfo.attackVolumes);

				} else if (tagName.equals("vulnerabilityVolumes")) {

					parseCollisionVolumes(parser, animationInfo,
							animationInfo.vulnerabilityVolumes);
				}
			}
			in.close();
		} catch (IOException e) {
			Log.v("SnowBall", "NO ANIMATION FOUND");

		} catch (XmlPullParserException e) {
			Log.v("SnowBall", "FAILED PARSING ANIMATION");
		} catch (Exception e) {
			Log.v("SnowBall", "Fuck");
		}

		return animationInfo;
	}

	private void parseAnimation(XmlPullParser parser, AnimationInfo info) {
		String attributeName = "";
		int ac = parser.getAttributeCount();
		for (int i = 0; i < ac; ++i) {
			attributeName = parser.getAttributeName(i);
			if (attributeName.equals("frameHeight")) {
				info.frameHeight = Integer
						.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("frameWidth")) {
				info.frameWidth = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("frames")) {
				info.frames = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("offset")) {
				info.offset = Integer.parseInt(parser.getAttributeValue(i));
			} else if (attributeName.equals("loop")) {
				info.loop = Boolean.parseBoolean(parser.getAttributeValue(i));
			}
		}
		info.attackVolumes = new FixedSizeArray<FixedSizeArray<CollisionInfo>>(
				info.frames);
		info.vulnerabilityVolumes = new FixedSizeArray<FixedSizeArray<CollisionInfo>>(
				info.frames);
	}

	private void parseCollisionVolumes(XmlPullParser parser,
			AnimationInfo info,
			FixedSizeArray<FixedSizeArray<CollisionInfo>> volumes)
			throws Exception {

		for (int i = 0; i < info.frames; ++i) {
			parser.nextTag();

			int aabbCount = 0;
			String attributeName = "";
			int ac = parser.getAttributeCount();
			for (int j = 0; j < ac; ++j) {
				attributeName = parser.getAttributeName(j);
				if (attributeName.equals("count")) {
					aabbCount = Integer.parseInt(parser.getAttributeValue(j));
				} else if (attributeName.equals("count")) {
					if (i != Integer.parseInt(parser.getAttributeValue(j))) {
						Log.v("SnowBall", i + "Fuck");
					}
				}
			}

			FixedSizeArray<CollisionInfo> collisionVolumeInfo = new FixedSizeArray<CollisionInfo>(
					aabbCount);

			for (int k = 0; k < aabbCount; ++k) {
				parser.nextTag();

				float height = 0;
				float width = 0;
				float x = 0;
				float y = 0;
				int hitType = 0;

				attributeName = "";
				ac = parser.getAttributeCount();
				for (int j = 0; j < ac; j++) {
					attributeName = parser.getAttributeName(j);
					if (attributeName.equals("height")) {
						height = Integer.parseInt(parser.getAttributeValue(j));
					} else if (attributeName.equals("width")) {
						width = Integer.parseInt(parser.getAttributeValue(j));
					} else if (attributeName.equals("x")) {
						x = Integer.parseInt(parser.getAttributeValue(j));
					} else if (attributeName.equals("y")) {
						y = Integer.parseInt(parser.getAttributeValue(j));
					} else if (attributeName.equals("hitType")) {
						hitType = Integer.parseInt(parser.getAttributeValue(j));
					}
				}

				collisionVolumeInfo.add(new CollisionInfo(x, y, width, height,
						hitType));
				parser.nextTag();
			}

			volumes.add(collisionVolumeInfo);
			parser.nextTag();
		}

	}
	
	
	
	//private data buckets
	private class AnimationInfo {

		int frameHeight;
		int frameWidth;
		int frames;
		int offset;
		boolean loop;

		FixedSizeArray<FixedSizeArray<CollisionInfo>> attackVolumes;
		FixedSizeArray<FixedSizeArray<CollisionInfo>> vulnerabilityVolumes;
		
	}

	private class CollisionInfo {
		public CollisionInfo(float x, float y, float width, float height,
				int hitType) {

			this.height = height;
			this.width = width;
			this.x = x;
			this.y = y;
			this.hitType = hitType;
		}

		float height = 0;
		float width = 0;
		float x = 0;
		float y = 0;
		int hitType = 0;
	}
}
