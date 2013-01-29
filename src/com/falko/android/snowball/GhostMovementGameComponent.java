package com.falko.android.snowball;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.components.GameComponent;
import com.falko.android.snowball.core.components.RenderComponent;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.input.InputButton;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.utility.Vector2D;

public class GhostMovementGameComponent extends GameComponent {
	
	private final int GHOST_MOVEMENT_SPEED = 5;

	public GhostMovementGameComponent() {
		
	}
	
	@Override
	public void reset() {

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		InputGameInterface input = sSystemRegistry.inputGameInterface;
		Vector2D pos =  ((GameObject) parent).getPosition();
		
		
		InputButton dpadup = input.getDpadUpButton();
		if (dpadup.getPressed()) {
			pos.y += GHOST_MOVEMENT_SPEED;
		} 
		
		InputButton dpaddown = input.getDpadDownButton();
		if (dpaddown.getPressed()) {
			pos.y += -GHOST_MOVEMENT_SPEED;
		} 
		
		InputButton dpadleft = input.getDpadLeftButton();
		if (dpadleft.getPressed()) {
			pos.x += -GHOST_MOVEMENT_SPEED;
		} 
		
		InputButton dpadright = input.getDpadRightButton();
		if (dpadright.getPressed()) {
			pos.x += GHOST_MOVEMENT_SPEED;
		} 
		 
		 DrawableBitmap bmap = sSystemRegistry.drawableFactory.allocateDrawableBitmap();
		 bmap.setTexture(sSystemRegistry.shortTermTextureLibrary.getTextureByResource(R.drawable.debug_circle_red));
		 bmap.resize(32, 32);
		 reCom.setDrawable(bmap);
		 
	}
	
	public void setRenderComponent(RenderComponent r) {
		reCom = r;
	}
	
	
	RenderComponent reCom;
}
