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

package com.replica.replicaisland;


import com.replica.replicaisland.CollisionParameters.HitType;
import com.replica.replicaisland.GameObject.ActionType;
import com.replica.replicaisland.SoundSystem.Sound;

/**
 * Player Animation game object component. Responsible for selecting an
 * animation to describe the player's current state. Requires the object to
 * contain a SpriteComponent to play animations.
 */
public class AnimationComponent extends GameComponent {

	public enum PlayerAnimations {
		IDLE_NORTH, IDLE_SOUTH, IDLE_WEST, IDLE_EAST,  MOVE_NORTH, MOVE_SOUTH, MOVE_WEST, MOVE_EAST,
		// HIT_REACT,
		// DEATH,
		// FROZEN
	}

	private SpriteComponent mSprite;
	PlayerAnimations idleAnimation_ ;


	public AnimationComponent() {
		super();
		reset();
		setPhase(ComponentPhases.ANIMATION.ordinal());
	}

	@Override
	public void reset() {
		mSprite = null;
		idleAnimation_ = PlayerAnimations.IDLE_SOUTH;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		if (mSprite != null) {

			GameObject parentObject = (GameObject) parent;

			final float velocityX = parentObject.getVelocity().x;
			final float velocityY = parentObject.getVelocity().y;

			GameObject.ActionType currentAction = parentObject
					.getCurrentAction();

			final TimeSystem time = sSystemRegistry.timeSystem;
			final float gameTime = time.getGameTime();

			boolean visible = true;

			float opacity = 1.0f;
			if (currentAction == ActionType.IDLE) {
				switch (idleAnimation_) {
				case IDLE_NORTH:
					mSprite.playAnimation(PlayerAnimations.IDLE_NORTH.ordinal());
					break;
				case IDLE_SOUTH:
					mSprite.playAnimation(PlayerAnimations.IDLE_SOUTH.ordinal());
					break;
				case IDLE_WEST:
					mSprite.playAnimation(PlayerAnimations.IDLE_WEST.ordinal());
					break;
				case IDLE_EAST:
					mSprite.playAnimation(PlayerAnimations.IDLE_EAST.ordinal());
					break;
				default:
					break;
				}
				
				
			} else if (currentAction == ActionType.MOVE) {
				InputGameInterface input = sSystemRegistry.inputGameInterface;
				
				InputDPad dpad = input.getDpad();
				if (dpad.pressed()) {
					if (dpad.upPressed()) {
						mSprite.playAnimation(PlayerAnimations.MOVE_NORTH.ordinal());
						idleAnimation_ = PlayerAnimations.IDLE_NORTH;
					} else if (dpad.downPressed()) {
						mSprite.playAnimation(PlayerAnimations.MOVE_SOUTH.ordinal());
						idleAnimation_ = PlayerAnimations.IDLE_SOUTH;
					} else if (dpad.leftPressed()) {
						mSprite.playAnimation(PlayerAnimations.MOVE_WEST.ordinal());
						idleAnimation_ = PlayerAnimations.IDLE_WEST;
					} else if (dpad.rightPressed()) {
						mSprite.playAnimation(PlayerAnimations.MOVE_EAST.ordinal());
						idleAnimation_ = PlayerAnimations.IDLE_EAST;
					}
				}

			} else if (currentAction == ActionType.ATTACK) {

			} else if (currentAction == ActionType.HIT_REACT) {

			} else if (currentAction == ActionType.DEATH) {

			} else if (currentAction == ActionType.FROZEN) {

			}

			mSprite.setVisible(visible);
			mSprite.setOpacity(opacity);
		}
	}

	public void setSprite(SpriteComponent sprite) {
		mSprite = sprite;
	}


}
