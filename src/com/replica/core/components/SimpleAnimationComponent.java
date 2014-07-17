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

import android.util.Log;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.core.game.AnimationType;

public class SimpleAnimationComponent extends GameComponent {

	private SpriteComponent spriteComponent_;
	private AnimationType currentAnimation_;

	public SimpleAnimationComponent() {
		super();
		reset();
		
		setPhase(ComponentPhases.ANIMATION.ordinal());
	}
	
	@Override
	public void reset() {
		spriteComponent_ = null;
		currentAnimation_ = null;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		//could add in death animations here
		if (spriteComponent_ != null) {
			
			GameObject parentObject = (GameObject) parent;
			
			final AnimationType oldAnimation = currentAnimation_;
			boolean visible = true;
			float opacity = 1.0f;
			if (parentObject.getCurrentAction() == ActionType.IDLE) {
				currentAnimation_ = AnimationType.IDLE;
			} else {
				
				if (Math.abs(parentObject.facingDirection.x)
						- Math.abs(parentObject.facingDirection.y) > 0) {

					if (parentObject.facingDirection.x > 0) {
						currentAnimation_ = AnimationType.EAST;
					} else {
						currentAnimation_ = AnimationType.WEST;
					}

				} else {

					if (parentObject.facingDirection.y > 0) {
						currentAnimation_ = AnimationType.NORTH;
					} else {
						currentAnimation_ = AnimationType.SOUTH;
					}

				}
				
			}

			spriteComponent_.setVisible(visible);
			spriteComponent_.setOpacity(opacity);
			
			if (oldAnimation != currentAnimation_) {
				spriteComponent_.playAnimation(currentAnimation_.ordinal());
			}
		}
	}
	
	public void setSpriteComponent(SpriteComponent spriteUpdater) {
		spriteComponent_ = spriteUpdater;
	}
	
}
