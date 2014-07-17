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
import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.utility.TimeSystem;

public class CombatDummyComponent extends GameComponent {

	private static final float HIT_REACT_TIME = 0.8f;
	private static final float DEAD_TIME = 30;
	
	public enum State {
		IDLE,  
		HIT_REACT, 
		DEAD, 
	}
	
	private State mState;
	private float mTimer;
	
	public CombatDummyComponent() {
		super();
        setPhase(ComponentPhases.THINK.ordinal());
		reset();
	}
	
	@Override
	public void reset() {
		mState = State.IDLE;
		mTimer = 0.0f;
	}
	
	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		TimeSystem time = sSystemRegistry.timeSystem;
		GameObject parentObject = (GameObject) parent;
		final float gameTime = time.getGameTime();
		
		if (parentObject.getCurrentAction() == ActionType.INVALID) {
			gotoIdle(parentObject);
		}
		
		if (mState != State.DEAD) {
			if (parentObject.life <= 0) {
				gotoDead(gameTime, parentObject);
			} else if (mState != State.HIT_REACT
					&& parentObject.lastReceivedHitType != HitType.INVALID
					&& parentObject.getCurrentAction() == ActionType.HIT_REACT) {
				gotoHitReact(parentObject, gameTime);
			} 
		}
		
		switch (mState) {
		case HIT_REACT:
			stateHitReact(gameTime, parentObject);
			break;
			
		case DEAD:
			stateDead(gameTime,  parentObject);
			break;
		}
		
	}

	private void gotoIdle(GameObject parentObject) {
		mState = State.IDLE;
		parentObject.setCurrentAction(ActionType.IDLE);
	}
	
	
	private void gotoHitReact(GameObject parentObject, float gameTime) {
		mState = State.HIT_REACT;
		mTimer = -1.0f;
	}
	
	private void stateHitReact(float gameTime, GameObject parentObject) {
		if (mTimer < 0.0f) {
            mTimer = gameTime;
        } 
		
		if (gameTime - mTimer >= HIT_REACT_TIME) {
			if (parentObject.lastReceivedHitType != HitType.INVALID
					&& parentObject.getCurrentAction() == ActionType.HIT_REACT) {
				mTimer = gameTime; //continue to loop while being attacked
			}
			else {
				gotoIdle(parentObject);
			}
			
		}
		
	}
	
	private void gotoDead(float gameTime, GameObject parentObject) {
		parentObject.setCurrentAction(ActionType.DEATH);
		mState = State.DEAD;
		mTimer = -1.0f;	
	}
	
	private void stateDead(float gameTime, GameObject parentObject) {
		if (mTimer < 0.0f) {
            mTimer = gameTime;
        } 
		
		if (gameTime - mTimer >= DEAD_TIME) {
			parentObject.destroyOnDeactivation = true;
		}
	}
}
