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
	
	
	public enum HumanoidAnimations {
		IDLE,
		IDLE_NORTH,
		IDLE_SOUTH,
		IDLE_WEST,
		IDLE_EAST, 
		
		MOVE,
		MOVE_NORTH,
		MOVE_SOUTH,
		MOVE_WEST,
		MOVE_EAST,
		
		DEATH,
		DEATH_NORTH,
		DEATH_SOUTH,
		DEATH_WEST,
		DEATH_EAST,

		SPELL,
		ATTACK_SPELL_NORTH,
		ATTACK_SPELL_SOUTH,
		ATTACK_SPELL_WEST,
		ATTACK_SPELL_EAST,
		
		SWORD,
		ATTACK_SWORD_NORTH,
		ATTACK_SWORD_SOUTH,
		ATTACK_SWORD_WEST,
		ATTACK_SWORD_EAST,
		
		POLE,
		ATTACK_POLE_NORTH,
		ATTACK_POLE_SOUTH,
		ATTACK_POLE_WEST,
		ATTACK_POLE_EAST,
		
		ARROW,
		ATTACK_ARROW_NORTH,
		ATTACK_ARROW_SOUTH,
		ATTACK_ARROW_WEST,
		ATTACK_ARROW_EAST,
	}
	
	private static final int NORTH = 1; 
	private static final int SOUTH = 2; 
	private static final int WEST = 3; 
	private static final int EAST = 4; 
	
	private static final int IDLE = HumanoidAnimations.IDLE.ordinal();
	private static final int MOVE = HumanoidAnimations.MOVE.ordinal();
	private static final int DEATH = HumanoidAnimations.DEATH.ordinal();
	private static final int SPELL = HumanoidAnimations.SPELL.ordinal();
	private static final int SWORD = HumanoidAnimations.SWORD.ordinal();
	private static final int POLE = HumanoidAnimations.POLE.ordinal();
	private static final int ARROW = HumanoidAnimations.ARROW.ordinal();
	
	private static final HumanoidAnimations[] HumanoidAnimationValues = HumanoidAnimations.values();

	private SpriteUpdater spriteUpdater_;
	private HumanoidAnimations currentAnimation_;

	public HumanoidAnimationComponent() {
		super();
		reset();
		currentAnimation_ = HumanoidAnimations.IDLE;
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
			
			final HumanoidAnimations oldAnimation = currentAnimation_;

			boolean visible = true;
			float opacity = 1.0f;
			
			int facing = parentObject.facingDirection.y > 0 ? NORTH
						 : parentObject.facingDirection.y < 0 ? SOUTH
						 : parentObject.facingDirection.x < 0 ? WEST 
						 : parentObject.facingDirection.x > 0 ? EAST 
						 : NORTH;
						 
			switch (currentAction) {
			case IDLE:
			case HIT_REACT:
			case FROZEN:
				currentAnimation_ = HumanoidAnimationValues[IDLE + facing];
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
		
		public void playAnimation(HumanoidAnimations animation);

		public void setOpacity(float opacity);

		public void setVisible(boolean visible);
	}
	
	public static final class MulitLayerSpriteUpdater implements SpriteUpdater {

		private FixedSizeArray<SpriteComponent> sprites_ = new FixedSizeArray<SpriteComponent>(10);
		
		@Override
		public void playAnimation(HumanoidAnimations animation) {
			
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
		public void playAnimation(HumanoidAnimations animation) {
			
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
