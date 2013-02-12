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

package com.falko.android.snowball.core.systems;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.collision.HitPoint;
import com.falko.android.snowball.core.collision.HitPointPool;
import com.falko.android.snowball.core.collision.LineSegment;
import com.falko.android.snowball.utility.FixedSizeArray;
import com.falko.android.snowball.utility.QuadTree;
import com.falko.android.snowball.utility.TObjectPool;
import com.falko.android.snowball.utility.Vector2D;
import com.falko.android.snowball.utility.VectorPool;

/**
 * Collision detection system.  Provides a ray-based interface for finding surfaces in the collision
 * world.   This version is based on a collision world of line segments, organized into an array of
 * tiles.  The underlying detection algorithm isn't relevant to calling code, however, so this class
 * may be extended to provide a completely different collision detection scheme.  
 * 
 * This class also provides a system for runtime-generated collision segments.  These temporary
 * segments are cleared each frame, and consequently must be constantly re-submitted if they are
 * intended to persist.  Temporary segments are useful for dynamic solid objects, such as moving
 * platforms.
 * 
 * CollisionSystem.TileVisitor is an interface for traversing individual collision tiles.  Ray casts
 * can be used to run user code over the collision world by passing different TileVisitor
 * implementations to executeRay.  Provided is TileTestVisitor, a visitor that compares the segments
 * of each tile visited with the ray and searches for points of intersection.
 *
 */
public class CollisionSystem extends BaseObject {
    
	QuadTree<LineSegment> lineSegmentQuadtree_;
	QuadTree<LineSegment> temporaryLineSegmentQuadtree_;
	
    private FixedSizeArray<LineSegment> pendingTemporaryLineSegments_;
    private FixedSizeArray<LineSegment> lineSegmentsQuery_;
    
    private LineSegmentPool lineSegmentPool_;
    
    private static final int MAX_LINE_SEGMENTS = 1000;
    private static final int MAX_TEMPORARY_LINE_SEGMENTS = 256;
    
    public CollisionSystem() {
        super();
        lineSegmentQuadtree_ = new QuadTree<LineSegment>(MAX_LINE_SEGMENTS);
        temporaryLineSegmentQuadtree_ = new QuadTree<LineSegment>(MAX_TEMPORARY_LINE_SEGMENTS);
        lineSegmentPool_ = new LineSegmentPool(MAX_LINE_SEGMENTS + MAX_TEMPORARY_LINE_SEGMENTS);
        pendingTemporaryLineSegments_ = new FixedSizeArray<LineSegment>(MAX_TEMPORARY_LINE_SEGMENTS);
        lineSegmentsQuery_ = new FixedSizeArray<LineSegment>(MAX_LINE_SEGMENTS + MAX_TEMPORARY_LINE_SEGMENTS);
    }
    
    @Override
    public void reset() {
    	
    	clearTree(lineSegmentQuadtree_);
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
    public void initialize(int tileWidth, int tileHeight) {
//        mWorld = world;
        //TODO: load map data here.
        // Unlike Replica Island, this program will have to reload all
        // of the collision files every time a new zone loads. The line segments
        // will be map specific and will be part of the map file
   
    }
    
  
    
 
    
    /* Inserts a temporary surface into the collision world.  It will persist for one frame. */
    public void addTemporarySurface(Vector2D startPoint, Vector2D endPoint, Vector2D normal, 
            GameObject ownerObject) {
        LineSegment newSegment = lineSegmentPool_.allocate();
        
        newSegment.set(startPoint, endPoint);
        newSegment.setOwner(ownerObject);
       
        pendingTemporaryLineSegments_.add(newSegment);
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
    	
		// Clear temporary surfaces
    	clearTree(temporaryLineSegmentQuadtree_);
    	
        int pendingLineSegmentsCount = pendingTemporaryLineSegments_.getCount();
        for (int i = 0; i < pendingLineSegmentsCount; ++i) {
        	temporaryLineSegmentQuadtree_.add(pendingTemporaryLineSegments_.get(i));
        }
        pendingTemporaryLineSegments_.clear();
        
        

    }

    
   
    
    /* 
     * Given a list of segments and a ray, this function performs an intersection search and
     * returns the closest intersecting segment, if any exists.
     */
    protected static boolean testSegmentAgainstList(FixedSizeArray<LineSegment> segments, 
            Vector2D startPoint, Vector2D endPoint, Vector2D hitPoint, Vector2D hitNormal, 
            Vector2D movementDirection, GameObject excludeObject) {
        boolean foundHit = false;
        float closestDistance = -1;
        float hitX = 0;
        float hitY = 0;
        float normalX = 0;
        float normalY = 0;
        final int count = segments.getCount();
        final Object[] segmentArray = segments.getArray();
        for (int x = 0; x < count; x++) {
            LineSegment segment = (LineSegment)segmentArray[x];
            // If a movement direction has been passed, filter out invalid surfaces by ignoring
            // those that do not oppose movement.  If no direction has been passed, accept all
            // surfaces.
//            final float dot = movementDirection.length2() > 0.0f ? 
//                    movementDirection.dot(segment.mNormal) : -1.0f;
                    
//            if (//dot < 0.0f &&
//                    (excludeObject == null || segment.owner != excludeObject) &&
//                    segment.calculateIntersection(startPoint, endPoint, hitPoint, segment.mNormal)) {
//                final float distance = hitPoint.distance2(startPoint);
//
//                if (!foundHit || closestDistance > distance) {
//                    closestDistance = distance;
//                    foundHit = true;
////                    normalX = segment.mNormal.x;
////                    normalY = segment.mNormal.y;
//                    // Store the components on their own so we don't have to allocate a vector
//                    // in this loop.
//                    hitX = hitPoint.x;
//                    hitY = hitPoint.y;
//                }
//            }
        }
        
        if (foundHit) {
            hitPoint.set(hitX, hitY);
            hitNormal.set(normalX, normalY);
        }
        return foundHit;
    }
    
    protected static boolean testBoxAgainstList(FixedSizeArray<LineSegment> segments, 
            float left, float right, float top, float bottom,
            Vector2D movementDirection, GameObject excludeObject, Vector2D outputOffset, 
            FixedSizeArray<HitPoint> outputHitPoints) {
        int hitCount = 0;
        final int maxSegments = outputHitPoints.getCapacity() - outputHitPoints.getCount();
        final int count = segments.getCount();
        final Object[] segmentArray = segments.getArray();
        
        VectorPool vectorPool = sSystemRegistry.vectorPool;
        HitPointPool hitPool = sSystemRegistry.hitPointPool;

        Vector2D tempHitPoint = vectorPool.allocate();
        
        for (int x = 0; x < count && hitCount < maxSegments; x++) {
            LineSegment segment = (LineSegment)segmentArray[x];
            // If a movement direction has been passed, filter out invalid surfaces by ignoring
            // those that do not oppose movement.  If no direction has been passed, accept all
            // surfaces.
//            final float dot = movementDirection.length2() > 0.0f ? 
//                    movementDirection.dot(segment.mNormal) : -1.0f;
                    
//            if (//dot < 0.0f &&
//                    (excludeObject == null || segment.owner != excludeObject) &&
//                    segment.calculateIntersectionBox(left, right, top, bottom, tempHitPoint, segment.mNormal)) {
//
//                Vector2D hitPoint = vectorPool.allocate(tempHitPoint);
//                Vector2D hitNormal = vectorPool.allocate();
//               
//                hitPoint.add(outputOffset);
//                HitPoint hit = hitPool.allocate();
//                
//                hit.hitPoint = hitPoint;
//                hit.hitNormal = hitNormal;
//                
//                outputHitPoints.add(hit);
//                
//                hitCount++;
//            }
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
            ((LineSegment)entry).owner = null;
            super.release(entry);
        }
    }
}
