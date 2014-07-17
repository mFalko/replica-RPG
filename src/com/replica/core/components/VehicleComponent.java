/*
 * Copyright (C) 2010 Matthew Falkoski
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
import com.replica.core.GameObject.ActionType;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

public class VehicleComponent extends GameComponent {

	private SteeringAdapter steering_;

	public VehicleComponent() {
		super();
		setPhase(ComponentPhases.MOVEMENT.ordinal());
	}

	@Override
	public void reset() {
		steering_ = null;
	}

	public void setSteeringAdapter(SteeringAdapter adapter) {
		steering_ = adapter;
	}


	@Override
	public void update(float timeDelta, BaseObject parent) {
		GameObject object = (GameObject) parent;
		
		
		if (object.getCurrentAction() != ActionType.MOVE) {
			return;
		}
		
		VectorPool pool = BaseObject.sSystemRegistry.vectorPool;
		Vector2 newVelocity = pool.allocate();
		steering_.getSteeringForce(newVelocity);
		newVelocity.add(object.getVelocity());
		newVelocity.normalize();
		newVelocity.multiply(object.getMaxSpeed());
		Vector2 positionOffset = pool.allocate(newVelocity);
		positionOffset.multiply(timeDelta);
		if (object.positionLocked == false) {
			object.getPosition().add(positionOffset);
		}
		pool.release(positionOffset);
		object.getVelocity().set(newVelocity);
		pool.release(newVelocity);
	}

	public interface SteeringAdapter {
		public void getSteeringForce(Vector2 steeringForce);
	}

}
