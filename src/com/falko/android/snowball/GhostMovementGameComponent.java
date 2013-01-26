package com.falko.android.snowball;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.components.GameComponent;
import com.falko.android.snowball.core.components.RenderComponent;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.utility.Vector2D;

public class GhostMovementGameComponent extends GameComponent {

	public GhostMovementGameComponent() {
		
	}
	
	@Override
	public void reset() {

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		InputGameInterface input = sSystemRegistry.inputGameInterface;
		Vector2D pos =  ((GameObject) parent).getPosition();
		 if (input.getSRButton().getPressed()) {
//			 Log.v("SnowBall", "Ghost update +10");
			pos.add(5, 0);
		 } else if (input.getSLButton().getPressed()) {
			 pos.add(-5, 0);
//			 Log.v("SnowBall", "Ghost update -10");
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
