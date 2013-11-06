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
				final int x = (int) Math.ceil(parentObject.facingDirection.x);
				final int y = (int) Math.ceil(parentObject.facingDirection.y);
				currentAnimation_ = y > 0? 
									//we're facing north
									x > 0? AnimationType.NORTH_EAST 
								:   x < 0? AnimationType.NORTH_WEST
								:	AnimationType.NORTH 
									
								:	y < 0? 
									//we're facing south	
									x > 0? AnimationType.SOUTH_EAST 
								:   x < 0? AnimationType.SOUTH_WEST
								:	AnimationType.SOUTH
									
								:	x > 0?
									//we're facing east
										AnimationType.EAST
									
								:	x < 0?
									//we're facing west	
										AnimationType.WEST
									
									//play default of north
								:   AnimationType.NORTH ;
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
