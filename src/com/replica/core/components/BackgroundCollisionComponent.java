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

package com.replica.core.components;

import java.util.Comparator;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.collision.HitPoint;
import com.replica.core.collision.HitPointPool;
import com.replica.core.collision.LineSegment;
import com.replica.core.systems.CollisionSystem;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.RectF;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

/**
 * Handles collision against the background. Snaps colliding objects out of
 * collision and reports the hit to the parent game object.
 */
public class BackgroundCollisionComponent extends GameComponent {
	private Vector2 mPreviousPosition;
	private int mWidth;
	private int mHeight;
	private int mHorizontalOffset;
	private int mVerticalOffset;

	// Workspace vectors. Allocated up front for speed.
	private RectF queryRect;
	private Vector2 mMergedNormal;
	private FixedSizeArray<HitPoint> outputHitPoints;
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

		mPreviousPosition = new Vector2();
		mWidth = width;
		mHeight = height;
		mHorizontalOffset = horzOffset;
		mVerticalOffset = vertOffset;

		queryRect = new RectF();
		mMergedNormal = new Vector2();
		outputHitPoints = new FixedSizeArray<HitPoint>(10);
		queryRect.set(0, 0, mWidth, mHeight);
	}

	public BackgroundCollisionComponent() {
		super();
		setPhase(ComponentPhases.COLLISION_RESPONSE.ordinal());
		mPreviousPosition = new Vector2();
		outputHitPoints = new FixedSizeArray<HitPoint>(10);
		queryRect = new RectF();
		mMergedNormal = new Vector2();
	}

	@Override
	public void reset() {
		mPreviousPosition.zero();
	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
		queryRect.set(0, 0, mWidth, mHeight);
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
		
		CollisionSystem collision = sSystemRegistry.collisionSystem;
		GameObject parentObject = (GameObject) parent;
		parentObject.setBackgroundCollisionNormal(Vector2.ZERO);

		float x = parentObject.getCenteredPositionX() - parentObject.width / 2;
		float y = parentObject.getCenteredPositionY() - parentObject.height / 2;
		queryRect.offsetTo(x + mHorizontalOffset, y + mVerticalOffset);

		FixedSizeArray<LineSegment> queryResult = collision.queryBackgroundCollision(queryRect);

		boolean hit = CollisionSystem.testBoxAgainstList(queryResult,
				queryRect.left_, queryRect.right_, queryRect.top_,
				queryRect.bottom_, parentObject, Vector2.ZERO, outputHitPoints);
		
		if (hit) {
			parentObject.getPosition().set(mPreviousPosition);
			HitPointPool hitPool = sSystemRegistry.hitPointPool;
			VectorPool vectorPool = sSystemRegistry.vectorPool;
			while (outputHitPoints.getCount() > 0) {
				HitPoint hitPoint = outputHitPoints.get(0);
				vectorPool.release(hitPoint.hitNormal);
				vectorPool.release(hitPoint.hitPoint);
				hitPool.release(outputHitPoints.get(0));
	    		outputHitPoints.remove(0);
	    	}
			parentObject.touchingWall = true;
		} else {
			mPreviousPosition.set(parentObject.getPosition());
		}
		
		
//		draw debug boxes
//		FixedSizeArray<LineSegment> segments = sSystemRegistry.zone
//				.getCollisionLines();
//		DebugSystem dsys = sSystemRegistry.debugSystem;
//		for (int i = 0; i < segments.getCount(); ++i) {
//			RectF segBound = segments.get(i).getBounds();
//			dsys.drawShape(segBound.left_, segBound.bottom_, segBound.width(),
//					segBound.height(), DebugSystem.SHAPE_BOX,
//					DebugSystem.COLOR_BLUE);
//		}
//		
//		dsys.drawShape(queryRect.left_, queryRect.bottom_, queryRect.width(),
//				queryRect.height(), DebugSystem.SHAPE_BOX,
//				DebugSystem.COLOR_BLUE);
	}

	/** Comparator for hit points. */
	@SuppressWarnings("unused")
	private static class HitPointDistanceComparator implements
			Comparator<HitPoint> {
		private Vector2 mOrigin;

		public HitPointDistanceComparator() {
			super();
			mOrigin = new Vector2();
		}

		public final void setOrigin(Vector2 origin) {
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
