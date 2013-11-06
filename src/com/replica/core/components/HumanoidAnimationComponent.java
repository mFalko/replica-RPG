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
import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.core.game.AnimationType;
import com.replica.utility.FixedSizeArray;

/**
 * Player Animation game object component. Responsible for selecting an
 * animation to describe the player's current state. Requires the object to
 * contain a SpriteComponent to play animations.
 * 
 * UPDATE:
 * This is no longer a player specific class. It can now work on any humanoid
 * sprite. There is a SpriteUpdater class that provides an interface to allow
 * for multiple layers on a sprite.
 */
public class HumanoidAnimationComponent extends GameComponent {
	

	private static final int NORTH = 1; 
	private static final int SOUTH = 2; 
	private static final int WEST = 3; 
	private static final int EAST = 4; 
	
	private static final int IDLE = AnimationType.HUMANOID_IDLE.ordinal();
	private static final int MOVE = AnimationType.HUMANOID_MOVE.ordinal();
	private static final int DEATH = AnimationType.HUMANOID_DEATH.ordinal();
	private static final int DEAD = AnimationType.HUMANOID_DEAD.ordinal();
	private static final int HIT_REACT = AnimationType.HUMANOID_HIT_REACT.ordinal();
	private static final int SPELL = AnimationType.HUMANOID_ATTACK_SPELL.ordinal();
	private static final int SWORD = AnimationType.HUMANOID_ATTACK_SWORD.ordinal();
	private static final int POLE = AnimationType.HUMANOID_ATTACK_POLE.ordinal();
	private static final int ARROW = AnimationType.HUMANOID_ATTACK_ARROW.ordinal();
	
	private static final AnimationType[] HumanoidAnimationValues = AnimationType.values();

	private SpriteUpdater spriteUpdater_;
	private AnimationType currentAnimation_;

	public HumanoidAnimationComponent() {
		super();
		reset();
		currentAnimation_ = AnimationType.HUMANOID_IDLE;
		setPhase(ComponentPhases.ANIMATION.ordinal());
	}

	@Override
	public void reset() {
		spriteUpdater_ = null;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		
		if (spriteUpdater_ != null) {

			GameObject parentObject = (GameObject) parent;
			GameObject.ActionType currentAction = parentObject.getCurrentAction();
			
			final AnimationType oldAnimation = currentAnimation_;

			boolean visible = true;
			float opacity = 1.0f;
			
			int facing = 0;

			if (Math.abs(parentObject.facingDirection.x)
					- Math.abs(parentObject.facingDirection.y) > 0) {

				if (parentObject.facingDirection.x > 0) {
					facing = EAST;
				} else {
					facing = WEST;
				}

			} else {

				if (parentObject.facingDirection.y > 0) {
					facing = NORTH;
				} else {
					facing = SOUTH;
				}

			}
			
						 
			switch (currentAction) {
			case IDLE:
			case FROZEN:
				currentAnimation_ = HumanoidAnimationValues[IDLE + facing];
				break;
				
			case HIT_REACT:
				// FIXME: there is a bug here..I know it, most GOs do not have
				// hit react and will not draw in this state...
				// I'll fix it later
				if ((parentObject.lastReceivedHitType & HitType.ATTACK) != 0) {
					currentAnimation_ = HumanoidAnimationValues[HIT_REACT + facing];
				}
				break;

			case MOVE:
				currentAnimation_ = HumanoidAnimationValues[MOVE + facing];
				break;
				
			case ATTACK:
				attack(parentObject, facing);
				break;

			case DEATH:
				currentAnimation_ = HumanoidAnimationValues[DEATH + facing];
				break;
				
			case DEAD:
				currentAnimation_ = HumanoidAnimationValues[DEAD + facing];
				break;
	
			} 

			spriteUpdater_.setVisible(visible);
			spriteUpdater_.setOpacity(opacity);
			
			if (oldAnimation != currentAnimation_) {
				spriteUpdater_.playAnimation(currentAnimation_);
			}
		}
	}

	private void attack(GameObject parent, int facing) {
	
		switch (parent.currentAttack.attackType_) {
		case SPELL:
			currentAnimation_ = HumanoidAnimationValues[SPELL + facing];
			break;
			
		case SWORD:
			currentAnimation_ = HumanoidAnimationValues[SWORD + facing];
			break;
			
		case POLE:
			currentAnimation_ = HumanoidAnimationValues[POLE + facing];
			break;
			
		case ARROW:
			currentAnimation_ = HumanoidAnimationValues[ARROW + facing];
			break;
		}
	}

	public void setSpriteUpdater(SpriteUpdater spriteUpdater) {
		spriteUpdater_ = spriteUpdater;
	}

	
	
	
	
	private static interface SpriteUpdater {
		
		public void playAnimation(AnimationType animation);

		public void setOpacity(float opacity);

		public void setVisible(boolean visible);
	}
	
	public static final class MulitLayerSpriteUpdater implements SpriteUpdater {

		private FixedSizeArray<SpriteComponent> sprites_ = new FixedSizeArray<SpriteComponent>(10);
		
		@Override
		public void playAnimation(AnimationType animation) {
			
			final int count = sprites_.getCount();
			for (int i = 0; i < count; i++) {
				SpriteComponent object = sprites_.get(i);
				object.playAnimation(animation.ordinal());
			}
		}

		@Override
		public void setOpacity(float opacity) {

			final int count = sprites_.getCount();
			for (int i = 0; i < count; i++) {
				SpriteComponent object = sprites_.get(i);
				object.setOpacity(opacity);
			}
		}

		@Override
		public void setVisible(boolean visible) {

			final int count = sprites_.getCount();
			for (int i = 0; i < count; i++) {
				SpriteComponent object = sprites_.get(i);
				object.setVisible(visible);
			}
		}
		
		public void addSprite(SpriteComponent sprite) {
			
			if (sprites_.getCount() < sprites_.getCapacity()) {
				sprites_.add(sprite);
			}
		}
		
	}
	
	public static final class SimpleSpriteUpdater implements SpriteUpdater {
		private SpriteComponent sprite_;

		@Override
		public void playAnimation(AnimationType animation) {
			
			if (sprite_ != null) {
				sprite_.playAnimation(animation.ordinal());
			}
		}

		@Override
		public void setOpacity(float opacity) {
			if (sprite_ != null) {
				sprite_.setOpacity(opacity);
			}
		}

		@Override
		public void setVisible(boolean visible) {
			if (sprite_ != null) {
				sprite_.setVisible(visible);
			}
		}
		
		public void setSprite(SpriteComponent sprite) {
				sprite_ = sprite;
		}
	}
}
