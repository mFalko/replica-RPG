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
import com.replica.core.GameObject.ActionType;
import com.replica.core.components.SteeringBehavior.Behavior;
import com.replica.utility.DebugSystem;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

public class NPCComponent extends GameComponent {

	private HitReactionComponent mHitReactComponent;

	private int mGameEvent;
	private boolean mSpawnGameEventOnDeath;

	private boolean mReactToHits;

	private float mDeathTime;
	private float mDeathFadeDelay;
	
	private Vector2 mTarget;

	private SteeringBehavior mSteering;

	private static final float DEATH_FADE_DELAY = 4.0f;
	private static final int DEFAULT_ATTACK_DISTANCE = 20;
	
	private static final float TIME_ATTACK = 1.0f;

	public enum State {
		IDLE, WANDER, SEEKING, ATTACK, HIT_REACT, DEAD,
	}

	private State mState;

	public NPCComponent() {
		super();
		setPhase(ComponentPhases.THINK.ordinal());
		
		reset();
	}

	@Override
	public void reset() {
		mHitReactComponent = null;
		mSteering = null;
		mGameEvent = -1;
		mSpawnGameEventOnDeath = false;
		mReactToHits = false;
		mDeathTime = -1.0f;
		mDeathFadeDelay = DEATH_FADE_DELAY;
		mTarget = null;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		GameObject parentObject = (GameObject) parent;

		if (mReactToHits && parentObject.getCurrentAction() == ActionType.HIT_REACT) {
			

			// hit react here

		} else if (parentObject.getCurrentAction() == ActionType.DEATH) {
			
			mDeathTime = (mDeathTime < 0) ? timeDelta : mDeathTime + timeDelta;
			if (mSpawnGameEventOnDeath && mGameEvent != -1) {
				//spawn 
				mSpawnGameEventOnDeath = false;
			}
			
			if (mDeathTime <= DEATH_FADE_DELAY) {
				//fade out
			}
			
			return;
		} else if (parentObject.life <= 0) {
			parentObject.setCurrentAction(ActionType.DEATH);
			parentObject.getVelocity().zero();
			return;
		} else if (parentObject.getCurrentAction() == ActionType.INVALID
				|| (!mReactToHits && parentObject.getCurrentAction() == ActionType.HIT_REACT)) {
			
			parentObject.setCurrentAction(ActionType.MOVE);
		} else if (parentObject.getCurrentAction() == ActionType.MOVE) {

		}

		if (parentObject.getCurrentAction() == ActionType.MOVE) {
			if (mState != State.WANDER) {
				gotoWander(parentObject);
			}
		}

		switch (mState) {
		case WANDER:
			Wander(parentObject);
			break;
		case IDLE:
			idle(parentObject);
			break;
		default:
			break;
		}

	}

	
	
	private void gotoIdle(GameObject parentObject) {
		
		mState = State.IDLE;
//		parentObject.setCurrentAction(ActionType.IDLE);
//		parentObject.getVelocity().set(Vector2.ZERO);
	}
	
	private void idle(GameObject parentObject) {
		lookForTarget();
		if (!closeEnough(parentObject, mTarget)) {
			gotoWander(parentObject);
			parentObject.facingDirection.set(mTarget);
			parentObject.facingDirection.subtract(parentObject.getPosition());
			parentObject.facingDirection.normalize();
		} 
//		else {
//			mSteering.postCommand(Behavior.WallAvoidance, null);
//			mSteering.postCommand(Behavior.Wander, null);
//		}
		
	}
	
	
	private void gotoWander(GameObject parentObject) {
		parentObject.setCurrentAction(ActionType.MOVE);
		mState = State.WANDER;
	}

	private void Wander(GameObject parentObject) {
		
		lookForTarget();
		if (!closeEnough(parentObject, mTarget)) {
			mSteering.postCommand(Behavior.Seek, mTarget);
			mSteering.postCommand(Behavior.WallAvoidance);
			DebugSystem debug = BaseObject.sSystemRegistry.debugSystem;
			debug.drawShape(mTarget.x-10, mTarget.y-10, 20, 20,
					DebugSystem.SHAPE_CIRCLE, DebugSystem.COLOR_BLUE);
			
			

		} else {
			mSteering.postCommand(Behavior.WallAvoidance);
			mSteering.postCommand(Behavior.Wander);
			
//			gotoIdle(parentObject);
		}
		
		parentObject.facingDirection.set(parentObject.getVelocity());
			parentObject.facingDirection.normalize();
		
	}
	

	
	private boolean closeEnough(GameObject parentObject, Vector2 target) {
		
		
		if (mTarget == null) {
			return true;
		}

		final float x1 = parentObject.getCenteredPositionX();
		final float y1 = parentObject.getCenteredPositionY();
		VectorPool pool = BaseObject.sSystemRegistry.vectorPool;
		Vector2 centerPosition = pool.allocate();
		centerPosition.set(x1, y1);
		final float distance_ = centerPosition.distance(target);
		pool.release(centerPosition);
		return DEFAULT_ATTACK_DISTANCE - distance_ > 0;

	}
	
	private void lookForTarget() {
		

	}
	
	public void setSteering(SteeringBehavior steering) {
		mSteering = steering;
	}

	public void setHitReactionComponent(HitReactionComponent hitReact) {
		mHitReactComponent = hitReact;
	}

	public void setGameEvent(int event, boolean spawnOnDeath) {
		mGameEvent = event;
		mSpawnGameEventOnDeath = spawnOnDeath;
	}


}
