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


package com.replica.replicaisland;

import java.util.Comparator;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.collision.HitPoint;
import com.falko.android.snowball.core.systems.CollisionSystem;
import com.falko.android.snowball.core.zoneloder.Zone;
import com.falko.android.snowball.utility.TimeSystem;
import com.falko.android.snowball.utility.Utils;
import com.falko.android.snowball.utility.Vector2D;

/**
 * Handles collision against the background. Snaps colliding objects out of
 * collision and reports the hit to the parent game object.
 */
public class BackgroundCollisionComponent extends GameComponent {
	private Vector2D mPreviousPosition;
	private int mWidth;
	private int mHeight;
	private int mHorizontalOffset;
	private int mVerticalOffset;

	// Workspace vectors. Allocated up front for speed.
	private Vector2D mHorizontalHitPoint;
	private Vector2D mHorizontalHitNormal;
	private Vector2D mVerticalHitPoint;
	private Vector2D mVerticalHitNormal;
	
	private Vector2D mMergedNormal;

	/**
	 * Sets up the collision bounding box. This box may be a different size than
	 * the bounds of the sprite that this object controls.
	 * 
	 * @param width
	 *            The width of the collision box.
	 * @param height
	 *            The height of the collision box.
	 * @param horzOffset
	 *            The offset of the collision box from the object's origin in
	 *            the x axis.
	 * @param vertOffset
	 *            The offset of the collision box from the object's origin in
	 *            the y axis.
	 */
	public BackgroundCollisionComponent(int width, int height, int horzOffset,
			int vertOffset) {
		super();
		setPhase(ComponentPhases.COLLISION_RESPONSE.ordinal());
		
		mPreviousPosition = new Vector2D();
		mWidth = width;
		mHeight = height;
		mHorizontalOffset = horzOffset;
		mVerticalOffset = vertOffset;

		
		mHorizontalHitPoint = new Vector2D();
		mHorizontalHitNormal = new Vector2D();
		mVerticalHitPoint = new Vector2D();
		mVerticalHitNormal = new Vector2D();
		mMergedNormal = new Vector2D();
	}

	public BackgroundCollisionComponent() {
		super();
		setPhase(ComponentPhases.COLLISION_RESPONSE.ordinal());
		mPreviousPosition = new Vector2D();
		mHorizontalHitPoint = new Vector2D();
		mHorizontalHitNormal = new Vector2D();
		mVerticalHitPoint = new Vector2D();
		mVerticalHitNormal = new Vector2D();
		mMergedNormal = new Vector2D();
	}

	@Override
	public void reset() {
		mPreviousPosition.zero();
	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
		// TODO: Resize might cause new collisions.
	}

	public void setOffset(int horzOffset, int vertOffset) {
		mHorizontalOffset = horzOffset;
		mVerticalOffset = vertOffset;
	}

	/**
	 * This function is the meat of the collision response logic. 
	 */
	@Override
	public void update(float timeDelta, BaseObject parent) {
		GameObject parentObject = (GameObject) parent;
		parentObject.setBackgroundCollisionNormal(Vector2D.ZERO);
		if (mPreviousPosition.length2() != 0) {
			
		}
		mPreviousPosition.set(parentObject.getPosition());
	}


	/** Comparator for hit points. */
	@SuppressWarnings("unused")
	private static class HitPointDistanceComparator implements
			Comparator<HitPoint> {
		private Vector2D mOrigin;

		public HitPointDistanceComparator() {
			super();
			mOrigin = new Vector2D();
		}

		public final void setOrigin(Vector2D origin) {
			mOrigin.set(origin);
		}

		public final void setOrigin(float x, float y) {
			mOrigin.set(x, y);
		}

		public int compare(HitPoint object1, HitPoint object2) {
			int result = 0;
			if (object1 != null && object2 != null) {
				final float obj1Distance = object1.hitPoint.distance2(mOrigin);
				final float obj2Distance = object2.hitPoint.distance2(mOrigin);
				final float distanceDelta = obj1Distance - obj2Distance;
				result = distanceDelta < 0.0f ? -1 : 1;
			} else if (object1 == null && object2 != null) {
				result = 1;
			} else if (object2 == null && object1 != null) {
				result = -1;
			}
			return result;
		}
	}
}
