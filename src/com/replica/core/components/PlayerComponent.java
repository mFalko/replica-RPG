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
import com.replica.core.GameObject.Team;
import com.replica.core.GameObjectManager;
import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.core.factory.GameObjectFactory;
import com.replica.core.game.AttackConstants;
import com.replica.input.InputDPad;
import com.replica.input.InputGameInterface;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.TimeSystem;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

public class PlayerComponent extends GameComponent {


	private static final float HIT_REACT_TIME = 0;
	public static final float PLAYER_MOVEMENT_SPEED = 2.0f;

	public enum State {
		MOVE, 
		ATTACK, 
		HIT_REACT, 
		DEAD, 
		FROZEN
	}
	
	private State mState;
	private float mTimer;
	
    public FixedSizeArray<AttackConstants> attackList;

	private InventoryComponent mInventory;
	private Vector2 mHotSpotTestPoint;
	private HitReactionComponent mHitReaction;
	private LaunchProjectileComponent mlauncher;

	public PlayerComponent() {
		super();
		mHotSpotTestPoint = new Vector2();
		reset();
		setPhase(ComponentPhases.THINK.ordinal());
		//TODO: Magic number, need to figure out where to define this
		attackList = new FixedSizeArray<AttackConstants>(6); 
	}

	@Override
	public void reset() {
		mState = State.MOVE;
		mTimer = 0.0f;
		mInventory = null;
		mHotSpotTestPoint.zero();
		mHitReaction = null;
//		attackList.clear();
	}

	protected void move(float time, float timeDelta, GameObject parentObject) {
		VectorPool pool = sSystemRegistry.vectorPool;
		InputGameInterface input = sSystemRegistry.inputGameInterface;

		if (pool != null && input != null) {
			
			Vector2 pos = parentObject.getPosition();
			
			InputDPad dpad = input.getDpad();
			Vector2 facingDir = parentObject.facingDirection;
			if (dpad.getDirection(facingDir)) {

				Vector2 directionDelta = pool.allocate(facingDir);
				directionDelta.multiply(PLAYER_MOVEMENT_SPEED);

				pos.y += directionDelta.y;
				pos.x += directionDelta.x;
				
				pool.release(directionDelta);

				parentObject.setCurrentAction(ActionType.MOVE);
				
//				DebugLog.d("SnowBall", "Position: X = " + pos.x + " : Y = " + pos.y);
			} else {
				parentObject.setCurrentAction(ActionType.IDLE);
			}
		}
	}
	
	@Override
	public void update(float timeDelta, BaseObject parent) {

		TimeSystem time = sSystemRegistry.timeSystem;
		GameObject parentObject = (GameObject) parent;

		final float gameTime = time.getGameTime();

		if (parentObject.getCurrentAction() == ActionType.INVALID) {
			gotoMove(parentObject);
		}

		// Watch for hit reactions or death interrupting the state machine.
		if (mState != State.DEAD) {
			if (parentObject.life <= 0) {
				gotoDead(gameTime);
			} else if (mState != State.HIT_REACT
					&& parentObject.lastReceivedHitType != HitType.INVALID
					&& parentObject.getCurrentAction() == ActionType.HIT_REACT) {
				gotoHitReact(parentObject, gameTime);
			} 
		}

		switch (mState) {
		case MOVE:
			stateMove(gameTime, timeDelta, parentObject);
			break;
		case HIT_REACT:
			stateHitReact(gameTime, timeDelta, parentObject);
			break;
		case DEAD:
			stateDead(gameTime, timeDelta, parentObject);
			break;
		case FROZEN:
			stateFrozen(gameTime, timeDelta, parentObject);
			break;
		case ATTACK:
			stateAttack(gameTime, timeDelta, parentObject);
			break;
		default:
			break;
		}
	}

	protected void goToAttack(GameObject parentObject) {
		
//		if (parentObject.currentAttack == null) {
//			gotoMove(parentObject);
//			return;
//		}
		
		parentObject.setCurrentAction(GameObject.ActionType.ATTACK);
		mState = State.ATTACK;
		mTimer = -1.0f;	
	}
	
	private void stateAttack(float gameTime, float timeDelta,
			GameObject parentObject) {
		
		if (mTimer < 0.0f) {
            mTimer = gameTime;
            GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          GameObjectManager manager = sSystemRegistry.gameObjectManager;
            
            GameObject object = factory.spawnQuake(
            		parentObject.getCenteredPositionX(), parentObject.getPosition().y);
            object.team = Team.PLAYER;
            manager.add(object);
            
        } 
		
		
		
		
		
//		parentObject.currentAttack.castTime
		if (gameTime - mTimer >= 0.6f) {
//			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//            GameObjectManager manager = sSystemRegistry.gameObjectManager;
//            VectorPool pool = sSystemRegistry.vectorPool;
//            
//            
//            
//            GameObject object = factory.spawnFireball(
//            		parentObject.getPosition().x + (20*parentObject.facingDirection.x)+10, parentObject.getPosition().y + (10*parentObject.facingDirection.y)+10);
//            
//            Vector2 vel = pool.allocate();
//            vel.set(parentObject.facingDirection);
//            vel.multiply(300);
//            object.setMaxSpeed(300);
//            object.setVelocity(vel);
//            object.facingDirection.set(parentObject.facingDirection);
//            pool.release(vel);
//            
//            manager.add(object);
            
			gotoMove(parentObject);
		}
		
		
		//TODO: attack stuff
	}

	private void gotoMove(GameObject parentObject) {
		mState = State.MOVE;
	}

	private void stateMove(float time, float timeDelta,
			GameObject parentObject) {

		move(time, timeDelta, parentObject);

		final InputGameInterface input = sSystemRegistry.inputGameInterface;

		// check input buttons here
		
		if (input.getButtonPressed(0)) {
			goToAttack(parentObject);
		}
		
//		if (input.getButtonPressed(1)) {
//			goToAttack(parentObject);
//		}

	}
	
	private void gotoHitReact(GameObject parentObject, float time) {
//		if (parentObject.lastReceivedHitType == CollisionParameters.HitType.LAUNCH) {
//			if (mState != State.FROZEN) {
//				gotoFrozen(parentObject);
//			}
//		} else {
//			mState = State.HIT_REACT;
//			mTimer = time;
//
//		}
	}

	private void stateHitReact(float time, float timeDelta,
			GameObject parentObject) {
		
		//TODO: do different stuff based on the last hit
		gotoMove(parentObject);
	}

	private void gotoDead(float time) {
		mState = State.DEAD;
		mTimer = time;
	}

	private void stateDead(float time, float timeDelta,
			GameObject parentObject) {

		// TODO:what happens on death?
	}

	private void gotoFrozen(GameObject parentObject) {
		mState = State.FROZEN;
		parentObject.setCurrentAction(ActionType.FROZEN);
	}

	private void stateFrozen(float time, float timeDelta,
			GameObject parentObject) {
		if (parentObject.getCurrentAction() == ActionType.MOVE) {
			gotoMove(parentObject);
		}
	}
	
	public final void setInventory(InventoryComponent inventory) {
		mInventory = inventory;
	}

	public final void setHitReactionComponent(HitReactionComponent hitReact) {
		mHitReaction = hitReact;
	}

}