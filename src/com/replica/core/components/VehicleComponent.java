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
