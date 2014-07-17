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
 
package com.replica.core.systems;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.collision.HitPoint;
import com.replica.core.collision.HitPointPool;
import com.replica.core.collision.LineSegment;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.QuadTree;
import com.replica.utility.RectF;
import com.replica.utility.TObjectPool;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

/**
 * Collision detection system.
 * 
 */
public class CollisionSystem extends BaseObject {

	QuadTree<LineSegment> lineSegmentQuadtree_;
	QuadTree<LineSegment> temporaryLineSegmentQuadtree_;
	QuadTree<GameObject> GameObjectTree_;

	private FixedSizeArray<LineSegment> pendingTemporaryLineSegments_;
	private FixedSizeArray<GameObject> pendingGameObjects_;
	private FixedSizeArray<LineSegment> lineSegmentsQuery_;
	private FixedSizeArray<GameObject> gameObjectQuery_;

	private LineSegmentPool lineSegmentPool_;

	private static final int MAX_LINE_SEGMENTS = 2000;
	private static final int MAX_TEMPORARY_LINE_SEGMENTS = 256;
	private static final int MAX_GAME_OBJECTS = 400;

	public CollisionSystem() {
		super();

		// TODO: move static segment quadtree into zone and pass the whole tree
		// to this system
		lineSegmentQuadtree_ = new QuadTree<LineSegment>(MAX_LINE_SEGMENTS);
		temporaryLineSegmentQuadtree_ = new QuadTree<LineSegment>(
				MAX_TEMPORARY_LINE_SEGMENTS);
		GameObjectTree_ = new QuadTree<GameObject>(MAX_GAME_OBJECTS);

		pendingTemporaryLineSegments_ = new FixedSizeArray<LineSegment>(
				MAX_TEMPORARY_LINE_SEGMENTS);
		lineSegmentsQuery_ = new FixedSizeArray<LineSegment>(MAX_LINE_SEGMENTS
				+ MAX_TEMPORARY_LINE_SEGMENTS);
		pendingGameObjects_ = new FixedSizeArray<GameObject>(
				MAX_GAME_OBJECTS);
		gameObjectQuery_ = new FixedSizeArray<GameObject>(
				MAX_GAME_OBJECTS);

		lineSegmentPool_ = new LineSegmentPool(MAX_TEMPORARY_LINE_SEGMENTS * 2);

	}

	@Override
	public void reset() {

		lineSegmentQuadtree_.reset();
		clearTree(temporaryLineSegmentQuadtree_);

		final int pendingCount = pendingTemporaryLineSegments_.getCount();
		for (int x = 0; x < pendingCount; x++) {
			lineSegmentPool_.release(pendingTemporaryLineSegments_.get(x));
			pendingTemporaryLineSegments_.set(x, null);
		}
		pendingTemporaryLineSegments_.clear();
	}

	private void clearTree(QuadTree<LineSegment> tree) {
		lineSegmentsQuery_.clear();

		tree.Query(tree.getBounds(), lineSegmentsQuery_);
		final int lineSegmentcount = lineSegmentsQuery_.getCount();
		for (int x = 0; x < lineSegmentcount; x++) {
			lineSegmentPool_.release(lineSegmentsQuery_.get(x));
			lineSegmentsQuery_.set(x, null);
		}
		lineSegmentsQuery_.clear();
		tree.reset();
	}

	/* Sets the current collision world to the supplied tile world. */
	public void initialize(FixedSizeArray<LineSegment> segments, int width,
			int height) {
		reset();
		lineSegmentQuadtree_.setBounds(0, 0, width, height);
		temporaryLineSegmentQuadtree_.setBounds(0, 0, width, height);

		if (segments == null) {
			return;
		}

		final int count = segments.getCount();
		for (int i = 0; i < count; ++i) {
			lineSegmentQuadtree_.add(segments.get(i));
		}
	}

	public FixedSizeArray<LineSegment> queryBackgroundCollision(RectF queryRect) {
		lineSegmentsQuery_.clear();
		lineSegmentQuadtree_.Query(queryRect, lineSegmentsQuery_);
		temporaryLineSegmentQuadtree_.Query(queryRect, lineSegmentsQuery_);

		return lineSegmentsQuery_;
	}

	public FixedSizeArray<GameObject> queryActiveGameObjects(RectF queryRect) {
		gameObjectQuery_.clear();
		GameObjectTree_.Query(queryRect, gameObjectQuery_);

		return gameObjectQuery_;

	}

	/*
	 * Inserts a temporary surface into the collision world. It will persist for
	 * one frame.
	 */
	public void addTemporarySurface(Vector2 startPoint, Vector2 endPoint,
			Vector2 normal, GameObject ownerObject) {
		LineSegment newSegment = lineSegmentPool_.allocate();

		newSegment.set(startPoint, endPoint);
		newSegment.setOwner(ownerObject);

		pendingTemporaryLineSegments_.add(newSegment);
	}

	public void addGameObject(GameObject object) {
		pendingGameObjects_.add(object);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		// Clear temporary surfaces
		clearTree(temporaryLineSegmentQuadtree_);
		int pendingLineSegmentsCount = pendingTemporaryLineSegments_.getCount();
		for (int i = 0; i < pendingLineSegmentsCount; ++i) {
			temporaryLineSegmentQuadtree_.add(pendingTemporaryLineSegments_
					.get(i));
		}
		pendingTemporaryLineSegments_.clear();
		
		GameObjectTree_.reset();
		int pendingObjectCount = pendingGameObjects_.getCount();
		for (int i = 0; i < pendingObjectCount; ++i) {
			GameObjectTree_.add(pendingGameObjects_.get(i));
		}
		pendingGameObjects_.clear();
	}

	/*
	 * Given a list of segments and a ray, this function performs an
	 * intersection search and returns the closest intersecting segment, if any
	 * exists.
	 */
	public static boolean testSegmentAgainstList(
			FixedSizeArray<LineSegment> segments, Vector2 startPoint,
			Vector2 endPoint, Vector2 hitPoint, Vector2 hitNormal,
			Vector2 movementDirection, GameObject excludeObject) {
		boolean foundHit = false;
		float closestDistance = -1;
		float hitX = 0;
		float hitY = 0;
		float normalX = 0;
		float normalY = 0;
		final int count = segments.getCount();
		final Object[] segmentArray = segments.getArray();
		for (int x = 0; x < count; x++) {
			LineSegment segment = (LineSegment) segmentArray[x];

			if ((excludeObject == null || segment.owner != excludeObject)
					&& segment.calculateIntersection(startPoint, endPoint,
							hitPoint)) {

				final float distance = hitPoint.distance2(startPoint);

				if (!foundHit || closestDistance > distance) {
					closestDistance = distance;
					foundHit = true;
					// normalX = segment.mNormal.x;
					// normalY = segment.mNormal.y;
					// TODO: Store the components on their own so we don't have
					// to allocate a vector
					// in this loop.
					hitX = hitPoint.x;
					hitY = hitPoint.y;
				}
			}
		}

		if (foundHit) {
			hitPoint.set(hitX, hitY);
			hitNormal.set(normalX, normalY);
		}
		return foundHit;
	}

	public static boolean testBoxAgainstList(
			FixedSizeArray<LineSegment> segments, float left, float right,
			float top, float bottom, GameObject excludeObject,
			Vector2 outputOffset, FixedSizeArray<HitPoint> outputHitPoints) {
		int hitCount = 0;
		final int maxSegments = outputHitPoints.getCapacity()
				- outputHitPoints.getCount();
		final int count = segments.getCount();
		final Object[] segmentArray = segments.getArray();

		VectorPool vectorPool = sSystemRegistry.vectorPool;
		HitPointPool hitPool = sSystemRegistry.hitPointPool;

		Vector2 tempHitPoint = vectorPool.allocate();

		for (int x = 0; x < count && hitCount < maxSegments; x++) {
			LineSegment segment = (LineSegment) segmentArray[x];

			if ((excludeObject != null && segment.owner != excludeObject)
					&& segment.calculateIntersectionBox(left, right, top,
							bottom, tempHitPoint)) {

				// TODO: implement runtime normal calculation to allow for
				// collision smoothing

				Vector2 hitPoint = vectorPool.allocate(tempHitPoint);
				Vector2 hitNormal = vectorPool.allocate();

				hitPoint.add(outputOffset);
				HitPoint hit = hitPool.allocate();

				hit.hitPoint = hitPoint;
				hit.hitNormal = hitNormal;

				outputHitPoints.add(hit);

				hitCount++;
			}
		}

		vectorPool.release(tempHitPoint);

		return hitCount > 0;
	}


	/**
	 * A pool of line segments.
	 */
	protected class LineSegmentPool extends TObjectPool<LineSegment> {
		public LineSegmentPool() {
			super();
		}

		public LineSegmentPool(int count) {
			super(count);
		}

		@Override
		public void reset() {

		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				getAvailable().add(new LineSegment());
			}
		}

		@Override
		public void release(Object entry) {
			((LineSegment) entry).owner = null;
			super.release(entry);
		}
	}
}
