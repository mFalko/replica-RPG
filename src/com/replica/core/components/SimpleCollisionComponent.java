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

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.systems.CollisionSystem;
import com.replica.utility.RectF;
import com.replica.utility.Utils;
import com.replica.utility.Vector2;

// Simple collision detection component for objects not requiring complex collision (projectiles, etc)
public class SimpleCollisionComponent extends GameComponent {
	private Vector2 mPreviousPosition;
	private Vector2 mCurrentPosition;
	private Vector2 mMovementDirection;
	private Vector2 mHitPoint;
	private Vector2 mHitNormal;
	private RectF   mbounds;
	
	public SimpleCollisionComponent() {
		super();
		setPhase(ComponentPhases.COLLISION_DETECTION.ordinal());
		mPreviousPosition = new Vector2();
		mCurrentPosition = new Vector2();
		mMovementDirection = new Vector2();
		mHitPoint = new Vector2();
		mHitNormal = new Vector2();
		mbounds = new RectF();
	}
	
	@Override
	public void reset() {
		mPreviousPosition.zero();
		mCurrentPosition.zero();
		mMovementDirection.zero();
		mHitPoint.zero();
		mHitNormal.zero();
	}
	
	@Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject) parent;
        
        if (mPreviousPosition.length2() > 0.0f) {
        	mCurrentPosition.set(parentObject.getCenteredPositionX(), parentObject.getCenteredPositionY());
        	mMovementDirection.set(mCurrentPosition);
        	mMovementDirection.subtract(mPreviousPosition);
        	if (mMovementDirection.length2() > 0.0f) {
        		final CollisionSystem collision = sSystemRegistry.collisionSystem;
        		if (collision != null) {
        			
        			final float x = parentObject.getPosition().x;
        			final float y = parentObject.getPosition().y;
        			final float width = parentObject.width;
        			final float height = parentObject.height;
        			mbounds.set(x, y, width, height);
        			
        			final boolean hit = CollisionSystem.testBoxAgainstList(
        					collision.query(mbounds), x, x+width, y+height, y, parentObject, null, null);
        			
        			if (hit) {
        				// snap
        				final float halfWidth = parentObject.width / 2.0f;
        				final float halfHeight = parentObject.height / 2.0f;
        				if (!Utils.close(mHitNormal.x, 0.0f)) {
        					parentObject.getPosition().x = mHitPoint.x - halfWidth;
        				} 
        				
        				if (!Utils.close(mHitNormal.y, 0.0f)) {
        					parentObject.getPosition().y = mHitPoint.y - halfHeight;
        				}
        				
        				//FIXME: implement background collision normals
        				mHitNormal.set(1, 1);
    	                parentObject.setBackgroundCollisionNormal(mHitNormal);
    	                    
        			}
        		}
        	}
        }
        
        mPreviousPosition.set(parentObject.getCenteredPositionX(), parentObject.getCenteredPositionY());
	}
}
