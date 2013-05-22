package com.replica.core.components;

import android.util.Log;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;

public class SimpleAnimationComponent extends GameComponent {

	public enum SimpleAnimations {
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST,
	}
	
	private SpriteComponent spriteComponent_;
	private SimpleAnimations currentAnimation_;

	public SimpleAnimationComponent() {
		super();
		reset();
		
		setPhase(ComponentPhases.ANIMATION.ordinal());
	}
	
	@Override
	public void reset() {
		spriteComponent_ = null;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		//could add in death animations here
		if (spriteComponent_ != null) {

			GameObject parentObject = (GameObject) parent;
			
			final SimpleAnimations oldAnimation = currentAnimation_;
			boolean visible = true;
			float opacity = 1.0f;
			//ok to truncate - if direction is relevant abs will be >= 1;
			final int x = (int) parentObject.facingDirection.x;
			final int y = (int) parentObject.facingDirection.y;
			currentAnimation_ = y > 0? 
								//we're facing north
								x > 0? SimpleAnimations.NORTH_EAST 
							:   x < 0? SimpleAnimations.NORTH_WEST
							:	SimpleAnimations.NORTH 
								
							:	y < 0? 
								//we're facing south	
								x > 0? SimpleAnimations.SOUTH_EAST 
							:   x < 0? SimpleAnimations.SOUTH_WEST
							:	SimpleAnimations.SOUTH
								
							:	x > 0?
								//we're facing east
								SimpleAnimations.EAST
								
							:	x < 0?
								//we're facing west	
								SimpleAnimations.WEST
								
								//play default of north
							:   SimpleAnimations.NORTH ;

					

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
