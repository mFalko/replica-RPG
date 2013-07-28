package com.replica.core.components;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.utility.TimeSystem;

public class CombatDummyComponent extends GameComponent {

	private static final float HIT_REACT_TIME = 2;
	private static final float DEAD_TIME = 120;
	
	public enum State {
		IDLE,  
		HIT_REACT, 
		DEAD, 
	}
	
	private State mState;
	private float mTimer;
	
	public CombatDummyComponent() {
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
