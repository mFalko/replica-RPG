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

import android.util.Log;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.utility.Interpolator;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

/**
 * A game component that implements velocity-based movement.
 */
public class MovementComponent extends GameComponent {
	// If multiple game components were ever running in different threads, this
	// would need
	// to be non-static.
	private static Interpolator sInterpolator = new Interpolator();

	private Vector2 mAcceleration;

	public MovementComponent() {
		super();
		setPhase(ComponentPhases.MOVEMENT.ordinal());
		mAcceleration = new Vector2();
		reset();
	}

	@Override
	public void reset() {
		mAcceleration.set(Vector2.ZERO);
	}

	public void setAcceleration(Vector2 acceleration) {
		if (acceleration != null) {
			mAcceleration.set(acceleration);
		} else {
			mAcceleration.set(Vector2.ZERO);
		}
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		GameObject object = (GameObject) parent;

//		sInterpolator.set(object.getVelocity().x, mAcceleration.x);
//		float offsetX = sInterpolator.interpolate(timeDelta);
//		float newX = object.getPosition().x + offsetX;
//		float newVelocityX = sInterpolator.getCurrent();
//
//		sInterpolator.set(object.getVelocity().y, mAcceleration.y);
//		float offsetY = sInterpolator.interpolate(timeDelta);
//		float newY = object.getPosition().y + offsetY;
//		float newVelocityY = sInterpolator.getCurrent();
		
		

        
        if (object.getCurrentAction() != ActionType.MOVE) {
			return;
		}
		
		VectorPool pool = BaseObject.sSystemRegistry.vectorPool;
		
		Vector2 positionOffset = pool.allocate(object.getVelocity());
		positionOffset.multiply(timeDelta);
		if (object.positionLocked == false) {
			object.getPosition().add(positionOffset);
		}
		pool.release(positionOffset);
	
        
//
//		if (object.positionLocked == false) {
//			object.getPosition().set(newX, newY);
//		}
//
//		Vector2 velocity = object.getVelocity();
//		velocity.set(newVelocityX, newVelocityY);
//		if (velocity.length() > object.getMaxSpeed()) {
//			velocity.normalize();
//			velocity.multiply(object.getMaxSpeed());
//		}
	}

}
