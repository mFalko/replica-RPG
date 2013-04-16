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

package com.falko.android.snowball.core.components;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.GameObject.ActionType;
import com.falko.android.snowball.core.collision.CollisionParameters;
import com.falko.android.snowball.core.collision.CollisionParameters.HitType;
import com.falko.android.snowball.input.InputDPad;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.utility.TimeSystem;
import com.falko.android.snowball.utility.Vector2D;
import com.falko.android.snowball.utility.VectorPool;

public class PlayerComponent extends GameComponent {

	public enum State {
		MOVE, ATTACK, HIT_REACT, DEAD, FROZEN
	}

	private static final float HIT_REACT_TIME = 0;
	private final float GHOST_MOVEMENT_SPEED = 3.0f;

	private State mState;
	private float mTimer;
	private float mTimer2;

	private InventoryComponent mInventory;
	private Vector2D mHotSpotTestPoint;
	private HitReactionComponent mHitReaction;
	

	public PlayerComponent() {
		super();
		mHotSpotTestPoint = new Vector2D();
		reset();
		setPhase(ComponentPhases.THINK.ordinal());
	}

	@Override
	public void reset() {
		mState = State.MOVE;
		mTimer = 0.0f;
		mTimer2 = 0.0f;
		mInventory = null;
		mHotSpotTestPoint.zero();
		mHitReaction = null;
	}

	protected void move(float time, float timeDelta, GameObject parentObject) {
		VectorPool pool = sSystemRegistry.vectorPool;
		InputGameInterface input = sSystemRegistry.inputGameInterface;

		if (pool != null && input != null) {
			
			Vector2D pos = ((GameObject) parentObject).getPosition();

			float deltaX = 0;
			float deltaY = 0;

			InputDPad dpad = input.getDpad();
			if (dpad.pressed()) {
				if (dpad.upPressed()) {
					deltaY += GHOST_MOVEMENT_SPEED;
				} else if (dpad.downPressed()) {
					deltaY += -GHOST_MOVEMENT_SPEED;
				}
				if (dpad.leftPressed()) {
					deltaX += -GHOST_MOVEMENT_SPEED;
				} else if (dpad.rightPressed()) {
					deltaX += GHOST_MOVEMENT_SPEED;
				}

				Vector2D d = pool.allocate();
				d.set(deltaX, deltaY);
				d.normalize();
				d.multiply(GHOST_MOVEMENT_SPEED);

				pos.y += d.y;
				pos.x += d.x;
				
				pool.release(d);
			}

		}

	}

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
			} else if (parentObject.getPosition().y < -parentObject.height) {
				// we fell off the bottom of the screen, die.
				parentObject.life = 0;
				gotoDead(gameTime);
			} else if (mState != State.HIT_REACT
					&& parentObject.lastReceivedHitType != HitType.INVALID
					&& parentObject.getCurrentAction() == ActionType.HIT_REACT) {
				gotoHitReact(parentObject, gameTime);
			} else {

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
		default:
			break;
		}

		// update hud relevent items

	}

	protected void gotoMove(GameObject parentObject) {
		parentObject.setCurrentAction(GameObject.ActionType.MOVE);
		mState = State.MOVE;
	}

	protected void stateMove(float time, float timeDelta,
			GameObject parentObject) {

		move(time, timeDelta, parentObject);

		final InputGameInterface input = sSystemRegistry.inputGameInterface;

		// check input buttons here

	}

	protected void gotoHitReact(GameObject parentObject, float time) {
		if (parentObject.lastReceivedHitType == CollisionParameters.HitType.LAUNCH) {
			if (mState != State.FROZEN) {
				gotoFrozen(parentObject);
			}
		} else {
			mState = State.HIT_REACT;
			mTimer = time;

		}
	}

	protected void stateHitReact(float time, float timeDelta,
			GameObject parentObject) {
		// This state just waits until the timer is expired.
		if (time - mTimer > HIT_REACT_TIME) {
			gotoMove(parentObject);
		}
	}

	protected void gotoDead(float time) {
		mState = State.DEAD;
		mTimer = time;
	}

	protected void stateDead(float time, float timeDelta,
			GameObject parentObject) {

		// TODO:what happens on death?
	}

	protected void gotoFrozen(GameObject parentObject) {
		mState = State.FROZEN;
		parentObject.setCurrentAction(ActionType.FROZEN);
	}

	protected void stateFrozen(float time, float timeDelta,
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